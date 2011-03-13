package com.zand.areaguard;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AreaDatabase {
	private static AreaDatabase instance = null;

	public static AreaDatabase getInstance() {
		if (instance == null) {
			instance = new AreaDatabase();
		}
		return instance;
	}

	private String driver;
	private String url;
	private String user;
	private String password;
	private String tablePrefix;
	private boolean keepConn = false;

	private Connection conn = null;

	protected AreaDatabase() {
		// Exists only to defeat instantiation.
	}
	
	/**
	 * Gets the Id from world name and creates it if it doesn't exist.
	 * @param name The name of the world to look up
	 * @return The world id and -1 on error
	 */
	public int getWorldId(String name) {
		int ret = -1;
		String sql = "SELECT Id FROM `" + tablePrefix + "Worlds` WHERE Name = ? LIMIT 1";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) ret = rs.getInt(1);
				else ret = addWorld(name);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return -1;
			}

			disconnect();
		} else return -1;
		return ret;
	}
	
	public String getWorldName(int id) {
		String ret = "NOT FOUND";
		String sql = "SELECT Name FROM `" + tablePrefix + "Worlds` WHERE Id = ? LIMIT 1";
		
		if (id < 0) return "ERROR";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) ret = rs.getString(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return "Error";
			}

			disconnect();
		} else return "ERROR: Not Connected";
		return ret;
	}
	
	public int addWorld(String name) {
		int ret = -1;

		String insert = "INSERT INTO `" + tablePrefix + "Worlds`"
				+ "(Name)"
				+ "VALUES (?);";
		connect();
		if (conn == null)
			return -2;
		try {
			PreparedStatement ps = conn.prepareStatement(insert);
			ps.setString(1, name);
			ps.execute();

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT LAST_INSERT_"
					+ (url.toLowerCase().contains("sqlite") ? "ROW" : "")
					+ "ID();");
			if (rs.next())
				ret = rs.getInt(1);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -2;
		}
		disconnect();
		return ret;
	}

	public int addArea(int world, String name, int coords[]) {
		int ret = -1;

		String insert = "INSERT INTO `" + tablePrefix + "Areas`"
				+ "(WorldId, Name, x1, y1, z1, x2, y2, z2)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		connect();
		if (conn == null)
			return -2;
		try {
			PreparedStatement ps = conn.prepareStatement(insert);
			ps.setInt(1, world);
			ps.setString(2, name);
			for (int i = 0; i < 6; i++)
				ps.setInt(i + 3, coords[i]);
			ps.execute();

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT LAST_INSERT_"
					+ (url.toLowerCase().contains("sqlite") ? "ROW" : "")
					+ "ID();");
			if (rs.next())
				ret = rs.getInt(1);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return -2;
		}
		disconnect();
		return ret;
	}
	
	public boolean addList(int area, String name, HashSet<String> values) {
		boolean ret = removeList(area, name, values);
		connect();
		if (conn != null && ret) {
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO `"
						+ tablePrefix + "Lists` (AreaId, List, Value)" + "VALUES (?, ?, ?)");

				for (String value : values) {
					ps.setInt(1, area);
					ps.setString(2, name);
					ps.setString(3, value);
					ps.execute();
				}

				// Close events
				ps.close();

			} catch (SQLException e) {
				System.err.println("Faild to add Values: " + e.getMessage());
				ret = false;
			}
			disconnect();
		}
		return ret;
	}

	public void config(String driver, String url, String user, String password,
			String tablePrefix, boolean keepConn) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.tablePrefix = tablePrefix;
		this.keepConn = keepConn;
		instance = this;
	}

	public boolean connect() {
		try {
			if (conn == null || conn.isClosed()) {
				System.out.println("Connecting");
				Class.forName(driver);
				conn = DriverManager.getConnection(url, user, password);
			}
		} catch (java.lang.ClassNotFoundException e) {
			// Could not find driver
			System.err.print("AreaGuard: Could not find driver \"" + driver
					+ "\"\n");
		} catch (SQLException e) {
			// Could not connect to the database
			conn = null;
			System.err.print("AreaGuard: Can't Connect, " + e.getMessage()
					+ "\n");
		}
		return (conn != null);
	}

	public boolean createTables() {
		// Load the sql Data
		String[] lines = JarFile.toString(
				(url.toLowerCase().contains("sqlite") ? 
						"data/sqlight.sql" : "data/mysql.sql"))
				.replaceAll("<tablePrefix>", tablePrefix)
				.split(";");

		connect();
		if (conn == null)
			return false;
		
		// Execute the sql data
		for (String sql : lines) {
			if (sql.trim().isEmpty()) continue;
			sql += ";";
			//System.out.println(sql);
			try {
				Statement st = conn.createStatement();
				st.execute(sql);
				st.close();
			} catch (SQLException e) {
				System.err.println("Failed to Create Tables: " + e.getMessage());
				e.printStackTrace();
			}
		}
		disconnect();
		return true;
	}

	public void disconnect() {
		disconnect(false);
	}

	public void disconnect(boolean force) {
		try {
			if (conn != null && !conn.isClosed() && (!keepConn || force)) {
				System.out.println("Disconnecting");
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			// Could not disconnect from the database?
			conn = null;
			System.err.print("AreaGuard:  Can't Disconnect, " + e.getMessage());
		}
	}
	
	public List<Integer> getAreaIdsFromListValues(String list, String value) {
		List<Integer> ret = new ArrayList<Integer>();
		String sql = "SELECT AreaId FROM `" + tablePrefix + "Lists` WHERE List=? And Value=?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, list);
				ps.setString(2, value);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) ret.add(rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ret.add(-2);
			}

			disconnect();
		} else ret.add(-2);
		return ret;
		
	}

	public Area getArea(int id) {
		Area ret = null;
		
		// ErrorArea
		if (id == -2) return new ErrorArea();
		connect();

		try {
			if (conn == null || conn.isClosed())
				return new ErrorArea();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM `"
					+ tablePrefix + "Areas` WHERE Id=? LIMIT 1");
			ps.setInt(1, id);

			ResultSet rs = ps.executeQuery();
			if (rs.next())
				ret = new Area(id, rs.getInt("WorldId"), rs.getString("Name"), rs.getInt("Priority"), new int[] {
						rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"),
						rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2") });

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ErrorArea();
		}
		disconnect();
		return ret;
	}

	public int getAreaId(int world, int x, int y, int z) {
		int ret = -1;
		String sql = "SELECT Id FROM `" + tablePrefix + "Areas` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + tablePrefix
				+ "Areas`.Priority DESC LIMIT 1";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, world);
				ps.setInt(2, x);
				ps.setInt(3, x);
				ps.setInt(4, y);
				ps.setInt(5, y);
				ps.setInt(6, z);
				ps.setInt(7, z);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) ret = rs.getInt(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return -2;
			}

			disconnect();
		} else return -2;
		return ret;
	}

	public int getAreaId(int world, String name) {
		int ret = -1;
		String sql = "SELECT Id FROM `" + tablePrefix + "Areas` WHERE name = ? LIMIT 1";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) ret = rs.getInt(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				return -2;
			}

			disconnect();
		} else return -2;
		return ret;
	}

	public ArrayList<Integer> getAreaIds() {
		ArrayList<Integer> ret = new ArrayList<Integer>();

		String sql = "SELECT Id FROM `" + tablePrefix + "Areas`";

		connect();
		if (conn != null) {
			try {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql);

				// Put it into the Map
				while (rs.next())
					ret.add(rs.getInt(1));

				// Close events
				rs.close();
				st.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ret.add(-2);
			}

			disconnect();
		} else ret.add(-2);

		return ret;
	}
	
	public ArrayList<Integer> getAreaIds(int world, int x, int y, int z) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		String sql = "SELECT Id FROM `" + tablePrefix + "Area` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + tablePrefix
				+ "Area`.Id DESC";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, world);
				ps.setInt(2, x);
				ps.setInt(3, x);
				ps.setInt(4, y);
				ps.setInt(5, y);
				ps.setInt(6, z);
				ps.setInt(7, z);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next())
					ret.add(rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ret.add(-2);
			}

			disconnect();
		} else ret.add(-2);
		return ret;
	}

	public ArrayList<Integer> getAreaIds(int world, String name) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		String sql = "SELECT Id FROM `" + tablePrefix + "Areas` WHERE WorldId = ? AND Name = ?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, world);
				ps.setString(2, name);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next())
					ret.add(rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				ret.add(-2);
			}

			disconnect();
		} else ret.add(-2);
		return ret;
	}
	
	public ArrayList<String> getList(int area, String list) {
		ArrayList<String> ret = new ArrayList<String>();
		String sql = "SELECT Value FROM `" + tablePrefix + "Lists` WHERE AreaId=? AND List=?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, area);
				ps.setString(2, list);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next())
					ret.add(rs.getString(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			disconnect();
		}
		return ret;
	}
	
	public Set<String> getLists(int area) {
		Set<String> ret = new HashSet<String>();
		String sql = "SELECT List FROM `" + tablePrefix + "Lists` WHERE AreaId=?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, area);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) ret.add(rs.getString(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			disconnect();
		}
		return ret;
	}

	public String getMsg(int area, String name) {
		String ret = "";
		String sql = "SELECT Msg FROM `" + tablePrefix + "Msgs` WHERE AreaId=? AND Name=? LIMIT 1";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, area);
				ps.setString(2, name);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) ret = rs.getString(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			disconnect();
		}
		return ret;
	}
	
	public HashMap<String, String> getMsgs(int area) {
		HashMap<String, String> ret = new HashMap<String, String>();
		String sql = "SELECT Name, Msg FROM `" + tablePrefix + "Msgs` WHERE AreaId=?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, area);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next())
					ret.put(rs.getString(1), rs.getString(2));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			disconnect();
		}
		return ret;
	}

	public boolean listHas(int area, String list, String value) {
		boolean ret = false;
		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn
						.prepareStatement("SELECT AreaId FROM `" + tablePrefix + "Lists`" 
								+ "WHERE AreaId=? AND List=? AND Value=? LIMIT 1");
				ps.setInt(1, area);
				ps.setString(2, list);
				ps.setString(3, value);
				ResultSet rs = ps.executeQuery();

				ret = rs.next();

				// Close events
				ps.close();

			} catch (SQLException e) {
				System.err.println("Faild to check ListValue: "
						+ e.getMessage());
			}
			disconnect();
		}
		return ret;
	}

	public void removeAllAreas() {
		for (int id : getAreaIds()) {
			removeArea(id);
		}
	}

	public boolean removeArea(int id) {
		String sql = "DELETE FROM `" + tablePrefix + "Areas` WHERE Id=?";
		if (!removeMsgs(id)) return false;
		if (!removeLists(id)) return false;
		connect();
		if (conn == null)
			return false;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove area: " + e.getMessage());
		}
		disconnect();

		return true;
	}

	public boolean removeList(int area, String list) {
		String sql = "DELETE FROM `" + tablePrefix + "List` WHERE AreaId=? AND List=?";
		connect();
		if (conn == null)
			return false;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, area);
			ps.setString(2, list);
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove list: " + e.getMessage());
		}
		disconnect();
		return true;
	}
	
	public boolean removeList(int area, String list, HashSet<String> values) {
		String sql = "DELETE FROM `" + tablePrefix + "Lists` WHERE AreaId=? AND List=? AND Value=?";
		connect();
		if (conn == null)
			return false;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, area);
			ps.setString(2, list);
			for (String value : values) {
				ps.setString(3, value);
				ps.execute();
			}

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove list: " + e.getMessage());
		}
		disconnect();
		return true;
	}

	public boolean removeLists(int area) {
		String sql = "DELETE FROM `" + tablePrefix + "Lists` WHERE AreaId=?";
		connect();
		if (conn == null)
			return false;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, area);
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove list: " + e.getMessage());
		}
		disconnect();
		return true;
	}
	
	public boolean removeMsgs(int area) {
		String sql = "DELETE FROM `" + tablePrefix + "Msgs` WHERE AreaId=?";
		connect();
		if (conn == null)
			return false;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, area);
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove list: " + e.getMessage());
		}
		disconnect();
		return true;
	}

	public boolean setMsg(int area, String name, String msg) {
		String delete = "DELETE FROM `" + tablePrefix + "Msgs` WHERE AreaId=? AND Name=?";
		String insert = "INSERT INTO `" + tablePrefix + "Msgs` (AreaId, Name, Msg)"
				+ "VALUES (?, ?, ?);";
		connect();
		if (conn == null) return false;
		try {
			// Erase the current Msg
			PreparedStatement ps = conn.prepareStatement(delete);
			ps.setInt(1, area);
			ps.setString(2, name);
			ps.execute();
			ps.close();
			
			// Add the msg if its not empty
			if (!msg.isEmpty()) {
				ps = conn.prepareStatement(insert);
				ps.setInt(1, area);
				ps.setString(2, name);
				ps.setString(3, msg);
				ps.execute();
				ps.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		disconnect();
		return true;
	}

	public boolean updateArea(Area area) {
		String update = "UPDATE `" + tablePrefix + "Areas` "
				+ "SET Name=?, Priority=?, x1=?, y1=?, z1=?, x2=?, y2=?, z2=? "
				+ "WHERE Id=?;";
		connect();
		if (conn == null)
			return false;
		try {
			PreparedStatement ps = conn.prepareStatement(update);
			ps.setString(1, area.getName());
			ps.setInt(2, area.getPriority());
			for (int i = 0; i < 6; i++)
				ps.setInt(i + 3, area.getCoords()[i]);
			ps.setInt(9, area.getId());

			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}

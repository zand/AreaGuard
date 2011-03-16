package com.zand.areaguard.sql.area;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import com.zand.areaguard.JarFile;
import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.area.Storage;
import com.zand.areaguard.area.World;
import com.zand.areaguard.error.area.ErrorArea;
import com.zand.areaguard.error.area.ErrorCubiod;
import com.zand.areaguard.error.area.ErrorWorld;

public class SqlStorage implements Storage {
	private String driver;
	private String url;
	private String user;
	private String password;
	protected String tablePrefix;
	private boolean keepConn = false;
	protected Connection conn = null;
	
	public SqlStorage(String filename) {
		loadConfig(filename);
	}
	
	public SqlStorage(String driver, String url, String user, String password,
			String tablePrefix, boolean keepConn) {
		config(driver, url, user, password, tablePrefix, keepConn);
	}
	
	public boolean loadConfig(String filename) {
		Properties props = new Properties();
		
		try {
			props.load(new FileInputStream(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Configure Connection
		String url = props.getProperty("url");
		
		// Figure out what driver to use
		String driver = props.getProperty("driver");
		if (driver == null || driver.isEmpty() || driver.equalsIgnoreCase("auto")) {
			String lower = url.toLowerCase().replaceAll("\\\\", "");
			if 		(lower.startsWith("jdbc:sqlite:")) 	driver = "org.sqlite.JDBC";
			else if (lower.startsWith("jdbc:mysql:"))	driver = "com.mysql.jdbc.Driver";
			else System.err.println("Coulden't figuer out driver from url");
		}
		
		
		
		config(driver, url, 
					props.getProperty("user"), 
					props.getProperty("password"), 
					props.getProperty("table-prefix"),  
					Boolean.valueOf(props.getProperty("keep-connection")));
		
		return false;
		
	}
	
	public void config(String driver, String url, String user, String password,
			String tablePrefix, boolean keepConn) {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		this.tablePrefix = tablePrefix;
		this.keepConn = keepConn;
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

	@Override
	public Area getArea(int areaId) {
		return new SqlArea(this, areaId);
	}

	@Override
	public ArrayList<Area> getAreas() {
		ArrayList<Area> areas = new ArrayList<Area>();
		String sql = "SELECT Id FROM `" + tablePrefix + "Areas`";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.execute();
				
				
				
				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) areas.add(new SqlArea(this, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			disconnect();
		}
		return areas;
	}

	@Override
	public ArrayList<Area> getAreas(String name) {
		ArrayList<Area> areas = new ArrayList<Area>();
		String sql = "SELECT Id FROM `" + tablePrefix + "Areas` WHERE Name=?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.execute();
				
				
				
				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) areas.add(new SqlArea(this, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			disconnect();
		}
		return areas;
	}

	@Override
	public ArrayList<Area> getAreas(String name, String owner) {
		ArrayList<Area> areas = new ArrayList<Area>();
		for (Area area : getAreas(name)) 
			if (area.isOwner(owner))
				areas.add(area);
		return areas;
	}

	@Override
	public ArrayList<Area> getAreasOwned(String owner) {
		ArrayList<Area> areas = new ArrayList<Area>();
		String sql = "SELECT AreaId FROM `" + tablePrefix + "Lists` WHERE Name=? AND Values=?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, "owners");
				ps.setString(2, owner);
				ps.execute();
				
				
				
				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) areas.add(new SqlArea(this, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			disconnect();
		}
		return areas;
	}

	@Override
	public World getWorld(int worldId) {
		return new SqlWorld(this, worldId);
	}

	@Override
	public World getWorld(String name) {
		World world = null;
		String sql = "SELECT Id FROM `" + tablePrefix + "Worlds` WHERE Name = ?";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.execute();
				
				
				
				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) world = new SqlWorld(this, rs.getInt(1));
				else world = newWorld(name);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				world = new ErrorWorld(name);
			}

			disconnect();
		} else world = new ErrorWorld(name);
		return world;
	}
	
	public World newWorld(String name) {
		World world = null;

		String insert = "INSERT INTO `" + tablePrefix + "Worlds`"
				+ "(Name)"
				+ "VALUES (?);";
		connect();
		if (conn == null)
			return new ErrorWorld(name);
		try {
			PreparedStatement ps = conn.prepareStatement(insert);
			ps.setString(1, name);
			ps.execute();

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT LAST_INSERT_"
					+ (url.toLowerCase().contains("sqlite") ? "ROW" : "")
					+ "ID();");
			if (rs.next()) world = new SqlWorld(this, rs.getInt(1));
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			world = new ErrorWorld(name);
		}
		disconnect();
		return world;
	}

	@Override
	public ArrayList<World> getWorlds() {
		ArrayList<World> worlds = new ArrayList<World>();
		String sql = "SELECT Id FROM `" + tablePrefix + "Worlds`";

		connect();
		if (conn != null) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.execute();
				
				
				
				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) worlds.add(new SqlWorld(this, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			disconnect();
		}
		return worlds;
	}

	@Override
	public Area newArea(String creator, String name) {
		Area area = null;

		String insert = "INSERT INTO `" + tablePrefix + "Areas`"
				+ "(Creator, Name)"
				+ "VALUES (?, ?);";
		connect();
		
		if (conn == null)
			return new ErrorArea("Faild to connect to Sql Database");
		try {
			PreparedStatement ps = conn.prepareStatement(insert);
			ps.setString(1, creator);
			ps.setString(2, name);
			ps.execute();

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT LAST_INSERT_"
					+ (url.toLowerCase().contains("sqlite") ? "ROW" : "")
					+ "ID();");
			if (rs.next()) area = new SqlArea(this, rs.getInt(1));
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			area = new ErrorArea("Faild to add area to Sql Database");
		}
		disconnect();
		return area;
	}

	@Override
	public Cubiod newCubiod(Area area, World world, long[] coords) {
		Cubiod cubiod = null;

		String insert = "INSERT INTO `" + tablePrefix + "Areas`"
				+ "(AreaId, WorldId, x1, y1, z1, x2, y2, z2)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		connect();
		
		if (conn == null)
			return new ErrorCubiod();
		try {
			PreparedStatement ps = conn.prepareStatement(insert);
			ps.setInt(1, area.getId());
			ps.setInt(2, world.getId());
			for (int i = 0; i < 6; i++)
				if (coords != null && i < coords.length)
					ps.setLong(3 + i, coords[i]);
			ps.execute();

			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT LAST_INSERT_"
					+ (url.toLowerCase().contains("sqlite") ? "ROW" : "")
					+ "ID();");
			if (rs.next()) cubiod = new SqlCubiod(this, rs.getInt(1));
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			cubiod = new ErrorCubiod();
		}
		disconnect();
		return cubiod;
	}
	
	
}

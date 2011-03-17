package com.zand.areaguard.sql.area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Msg;

public class SqlMsg implements Msg {
	final private SqlStorage storage;
	final private int areaId;
	final private String name;
	
	protected SqlMsg(SqlStorage storage, int areaId, String name) {
		this.storage = storage;
		this.areaId = areaId;
		this.name = name;
	}

	@Override
	public Area getArea() {
		return storage.getArea(areaId);
	}

	@Override
	public String getMsg() {
		String msg = "";
		String sql = "SELECT Msg FROM `" + storage.tablePrefix + "Msgs` WHERE AreaId = ? AND Name=? LIMIT 1";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, areaId);
				ps.setString(2, name);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next())
					msg = rs.getString(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				msg = "ERROR: SQL ERROR";
			}

			storage.disconnect();
		} else msg = "ERROR: Faild to connect to SQL";
		return msg;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean setMsg(String creator, String msg) {
		boolean success = false;
		if (exsists()) {
			if (msg.isEmpty()) { // Delete it
				String sql = "DELETE FROM `" + storage.tablePrefix + "Msgs` WHERE AreaId=? AND Name=?";
				if (storage.connect())
					return false;
				try {
					PreparedStatement ps = storage.conn.prepareStatement(sql);
					ps.setInt(1, areaId);
					ps.setString(2, name);
					ps.execute();

					// Close events
					ps.close();

				} catch (SQLException e) {
					System.err.println("Faild to remove msg: " + e.getMessage());
					storage.disconnect();
					return false;
				}
				storage.disconnect();
				return true;
			
			} else { // Update it
				String update = "UPDATE `" + storage.tablePrefix + "Msgs` "
				+ "SET Creator=?, Msg=? "
				+ "WHERE AreaId=? AND Name=?;";
				if (!storage.connect())
					return false;
				try {
					PreparedStatement ps = storage.conn.prepareStatement(update);
					ps.setString(1, creator);
					ps.setString(2, msg);
					ps.setInt(3, areaId);
					ps.setString(4, name);
					
				
					ps.execute();
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
					storage.disconnect();
					return false;
				}
				
				storage.disconnect();
				return true;
			}
		} else {
			
			if (msg.isEmpty()) return true; // Do nothing
			
			else { // Insert it
				String insert = "INSERT INTO `" + storage.tablePrefix + "Msgs`"
						+ "(AreaId, Creator, Name, Msg)"
						+ "VALUES (?,?,?,?);";
				
				if (!storage.connect())
					return false;
				try {
					PreparedStatement ps = storage.conn.prepareStatement(insert);
					ps.setInt(1, areaId);
					ps.setString(2, creator);
					ps.setString(3, name);
					ps.setString(4, msg);
					ps.execute();
					ps.close();
					success = true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				storage.disconnect();
				return success;
			}
		}
	}

	@Override
	public boolean exsists() {
		boolean ret = false;
		String sql = "SELECT COUNT(*) FROM `" + storage.tablePrefix + "Msgs` WHERE AreaId = ? AND Name = ? LIMIT 1";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, areaId);
				ps.setString(2, getName());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) ret = (rs.getInt(1) > 0);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return ret;
	}

}

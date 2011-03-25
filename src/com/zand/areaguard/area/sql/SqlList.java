package com.zand.areaguard.area.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.List;

public class SqlList extends List {
	final private SqlStorage storage;

	public SqlList(SqlStorage storage, Area area, String name) {
		super(area, name.toLowerCase());
		this.storage = storage;
	}
	
	public String toString() {
		String ret = "";
		for (String value : getValues())
			ret += ", " + value;
		if (!ret.isEmpty())
			ret = ret.substring(2);
		return ret;
	}

	@Override
	public boolean addValue(String creator, String value) {
		if (hasValue(value)) return true;
		boolean success = false;

		String insert = "INSERT INTO `" + storage.tablePrefix + "Lists`"
				+ "(AreaId, Creator, Name, Value)"
				+ "VALUES (?,?,?,?);";
		
		if (!storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(insert);
			ps.setInt(1, getArea().getId());
			ps.setString(2, creator.toLowerCase());
			ps.setString(3, getName());
			ps.setString(4, value.toLowerCase());
			ps.execute();
			ps.close();
			success = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		storage.disconnect();
		return success;
	}

	@Override
	public boolean clear() {
		String sql = "DELETE FROM `" + storage.tablePrefix + "Lists` WHERE AreaId=? AND Name=?";
		if (storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(sql);
			ps.setInt(1, getArea().getId());
			ps.setString(2, getName());
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to clear list: " + e.getMessage());
			storage.disconnect();
			return false;
		}
		storage.disconnect();
		return true;
	}

	@Override
	public ArrayList<String> getValues() {
		ArrayList<String> values = new ArrayList<String>();
		String sql = "SELECT Value FROM `" + storage.tablePrefix + "Lists` WHERE AreaId = ? AND Name=?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getArea().getId());
				ps.setString(2, getName());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) values.add(rs.getString(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return values;
	}

	@Override
	public boolean hasValue(String value) {
		boolean found = false;
		String sql = "SELECT Value FROM `" + storage.tablePrefix + "Lists` WHERE AreaId = ? AND Name=? AND Value=?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getArea().getId());
				ps.setString(2, getName());
				ps.setString(3, value.toLowerCase());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next())
					found = true;

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return found;
	}

	@Override
	public boolean removeValue(String value) {
		String sql = "DELETE FROM `" + storage.tablePrefix + "Lists` WHERE AreaId=? AND Name=? AND Value=?";
		if (!storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(sql);
			ps.setInt(1, getArea().getId());
			ps.setString(2, getName());
			ps.setString(3, value.toLowerCase());
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove value: " + e.getMessage());
			storage.disconnect();
			return false;
		}
		storage.disconnect();
		return true;
	}

	@Override
	public boolean exsists() {
		boolean ret = false;
		String sql = "SELECT COUNT(*) FROM `" + storage.tablePrefix + "Lists` WHERE AreaId = ? AND Name = ? LIMIT 1";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getArea().getId());
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

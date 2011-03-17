package com.zand.areaguard.sql.area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.List;

public class SqlList implements List {
	final private SqlStorage storage;
	final private int areaId;
	final private String name;

	public SqlList(SqlStorage storage, int areaId, String name) {
		this.storage = storage;
		this.areaId = areaId;
		this.name = name;
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
			ps.setInt(1, areaId);
			ps.setString(2, creator);
			ps.setString(3, name);
			ps.setString(4, value);
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
	public boolean addValues(String creator, String[] values) {
		for (String value : values) 
			if (!addValue(creator, value))
				return false;
		return true;
	}

	@Override
	public boolean clear() {
		String sql = "DELETE FROM `" + storage.tablePrefix + "Lists` WHERE AreaId=? AND Name=?";
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
			System.err.println("Faild to clear list: " + e.getMessage());
			storage.disconnect();
			return false;
		}
		storage.disconnect();
		return true;
	}

	@Override
	public Area getArea() {
		return storage.getArea(areaId);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ArrayList<String> getValues() {
		ArrayList<String> values = new ArrayList<String>();
		String sql = "SELECT Value FROM `" + storage.tablePrefix + "Lists` WHERE AreaId = ? AND Name=?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, areaId);
				ps.setString(2, name);
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
				ps.setInt(1, areaId);
				ps.setString(2, name);
				ps.setString(3, value);
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
			ps.setInt(1, areaId);
			ps.setString(2, name);
			ps.setString(3, value);
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
	public boolean removeValues(String[] values) {
		for (String value : values) 
			if (!removeValue(value))
				return false;
		return true;
	}

	@Override
	public boolean exsists() {
		boolean ret = false;
		String sql = "SELECT COUNT(*) FROM `" + storage.tablePrefix + "Lists` WHERE AreaId = ? AND Name = ? LIMIT 1";

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

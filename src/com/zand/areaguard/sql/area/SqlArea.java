package com.zand.areaguard.sql.area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.area.List;
import com.zand.areaguard.area.Msg;
import com.zand.areaguard.area.World;

public class SqlArea implements Area {
	final private SqlStorage storage;
	final private int id;
	
	protected SqlArea(SqlStorage storage, int id) {
		this.storage = storage;
		this.id = id;
	}

	@Override
	public ArrayList<Cubiod> getCubiods() {
		ArrayList<Cubiod> cubiods = new ArrayList<Cubiod>();
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cubiods` WHERE AreaId = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) 
					cubiods.add(new SqlCubiod(storage, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return cubiods;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public List getList(String name) {
		return new SqlList(storage, getId(), name);
	}

	@Override
	public ArrayList<List> getLists() {
		ArrayList<List> lists = new ArrayList<List>();
		String sql = "SELECT DISTINCT Name FROM `" + storage.tablePrefix + "Lists` WHERE AreaId = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) lists.add(getList(rs.getString(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return lists;
	}

	@Override
	public Msg getMsg(String name) {
		return new SqlMsg(storage, getId(), name);
	}

	@Override
	public ArrayList<Msg> getMsgs() {
		ArrayList<Msg> msgs = new ArrayList<Msg>();
		String sql = "SELECT DISTINCT Name FROM `" + storage.tablePrefix + "Msgs` WHERE AreaId = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) msgs.add(getMsg(rs.getString(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return msgs;
	}

	@Override
	public String getName() {
		String name = "";
		String sql = "SELECT Name FROM `" + storage.tablePrefix + "Msgs` WHERE Id = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) name = rs.getString(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return name;
	}

	@Override
	public boolean isOwner(String player) {
		return getList("owners").hasValue(player);
	}

	@Override
	public boolean playerCan(String player, String[] lists) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setName(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasOwner(String player) {
		return getList("owners").hasValue(player);
	}

	@Override
	public boolean pointInside(World world, long x, long y, long z) {
		for (Cubiod cubiod : getCubiods())
			if (cubiod.pointInside(world, x, y, z))
				return true;
		return false;
	}

	@Override
	public boolean pointInside(String world, long x, long y, long z) {
		return pointInside(storage.getWorld(world), x, y, z);
	}
}

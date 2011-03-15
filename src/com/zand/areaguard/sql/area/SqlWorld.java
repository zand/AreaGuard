package com.zand.areaguard.sql.area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.error.area.ErrorCubiod;

public class SqlWorld implements com.zand.areaguard.area.World {
	final private SqlStorage storage;
	final private int id;
	
	protected SqlWorld(SqlStorage storage, int id) {
		this.storage = storage;
		this.id = id;
	}

	@Override
	public boolean deleteCubiods() {
		String sql = "DELETE FROM `" + storage.tablePrefix + "Cubiods` WHERE WorldId=?";
		if (storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(sql);
			ps.setInt(1, getId());
			ps.execute();

			// Close events
			ps.close();

		} catch (SQLException e) {
			System.err.println("Faild to remove list: " + e.getMessage());
			storage.disconnect();
			return false;
		}
		storage.disconnect();
		return true;
	}

	@Override
	public Cubiod getCubiod(long x, long y, long z) {
		Cubiod cubiod = null;
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cubiods` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + storage.tablePrefix
				+ "Cubiods`.Priority DESC LIMIT 1";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.setLong(2, x);
				ps.setLong(3, x);
				ps.setLong(4, y);
				ps.setLong(5, y);
				ps.setLong(6, z);
				ps.setLong(7, z);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) cubiod = new SqlCubiod(storage, rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				cubiod = new ErrorCubiod();
			}

			storage.disconnect();
		} else cubiod = new ErrorCubiod();
		return cubiod;
	}

	@Override
	public ArrayList<Cubiod> getCubiods() {
		ArrayList<Cubiod> cubiods = new ArrayList<Cubiod>();
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cubiods` WHERE WorldId = ? " 
				+ "ORDER BY `" + storage.tablePrefix + "Cubiods`.Priority DESC";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) cubiods.add(new SqlCubiod(storage, rs.getInt(1)));

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
	public ArrayList<Cubiod> getCubiods(long x, long y, long z) {
		ArrayList<Cubiod> cubiods = new ArrayList<Cubiod>();
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cubiods` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + storage.tablePrefix
				+ "Cubiods`.Priority DESC";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.setLong(2, x);
				ps.setLong(3, x);
				ps.setLong(4, y);
				ps.setLong(5, y);
				ps.setLong(6, z);
				ps.setLong(7, z);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) cubiods.add(new SqlCubiod(storage, rs.getInt(1)));

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
	public String getName() {
		// TODO
		return null;
	}

	@Override
	public boolean save() {
		// TODO Auto-generated method stub
		return false;
	}

}

package com.zand.areaguard.sql.area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.error.area.ErrorCuboid;

public class SqlWorld implements com.zand.areaguard.area.World {
	final private SqlStorage storage;
	final private int id;
	
	protected SqlWorld(SqlStorage storage, int id) {
		this.storage = storage;
		this.id = id;
	}

	@Override
	public boolean deleteCuboids() {
		String sql = "DELETE FROM `" + storage.tablePrefix + "Cuboids` WHERE WorldId=?";
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
	public Cuboid getCuboid(long x, long y, long z) {
		Cuboid cuboid = null;
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cuboids` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + storage.tablePrefix
				+ "Cuboids`.Priority DESC LIMIT 1";

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
				if (rs.next()) cuboid = new SqlCuboid(storage, rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				cuboid = new ErrorCuboid();
			}

			storage.disconnect();
		} else cuboid = new ErrorCuboid();
		return cuboid;
	}

	@Override
	public ArrayList<Cuboid> getCuboids() {
		ArrayList<Cuboid> cuboids = new ArrayList<Cuboid>();
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cuboids` WHERE WorldId = ? " 
				+ "ORDER BY `" + storage.tablePrefix + "Cuboids`.Priority DESC";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				while (rs.next()) cuboids.add(new SqlCuboid(storage, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return cuboids;
	}

	@Override
	public ArrayList<Cuboid> getCuboids(long x, long y, long z) {
		ArrayList<Cuboid> cuboids = new ArrayList<Cuboid>();
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cuboids` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + storage.tablePrefix
				+ "Cuboids`.Priority DESC";

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
				while (rs.next()) cuboids.add(new SqlCuboid(storage, rs.getInt(1)));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return cuboids;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		String name = "";
		String sql = "SELECT Name FROM `" + storage.tablePrefix + "Worlds` WHERE Id = ? ";

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

}

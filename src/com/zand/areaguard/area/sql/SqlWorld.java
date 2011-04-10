package com.zand.areaguard.area.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.zand.areaguard.area.Cuboid;
import com.zand.areaguard.area.error.ErrorWorld;

public class SqlWorld extends com.zand.areaguard.area.World {
	public static final ErrorWorld
		COULD_NOT_CONNECT = new ErrorWorld("COULD NOT CONNECT"),
		SQL_ERROR = new ErrorWorld("SQL ERROR");
	private final SqlStorage storage;
	
	protected SqlWorld(SqlStorage storage, int id) {
		super(id);
		this.storage = storage;
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
	public Cuboid getCuboid(int x, int y, int z) {
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
				cuboid = SqlCuboid.SQL_ERROR;
			}

			storage.disconnect();
		} else cuboid = SqlCuboid.COULD_NOT_CONNECT;
		return cuboid;
	}
	
	@Override
	public Cuboid getCuboid(boolean active, int x, int y, int z) {
		Cuboid cuboid = null;
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cuboids` WHERE WorldId = ? AND Active = ? "
				+ "AND x1 <= ? AND x2 >= ? AND y1 <= ? AND y2 >= ? "
				+ "AND z1 <= ? AND z2 >= ? ORDER BY `" + storage.tablePrefix
				+ "Cuboids`.Priority DESC LIMIT 1";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.setBoolean(2, active);
				ps.setLong(3, x);
				ps.setLong(4, x);
				ps.setLong(5, y);
				ps.setLong(6, y);
				ps.setLong(7, z);
				ps.setLong(8, z);
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) cuboid = new SqlCuboid(storage, rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				cuboid = SqlCuboid.SQL_ERROR;
			}

			storage.disconnect();
		} else cuboid = SqlCuboid.COULD_NOT_CONNECT;
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
	public ArrayList<Cuboid> getCuboids(int x, int y, int z) {
		ArrayList<Cuboid> cuboids = new ArrayList<Cuboid>();
		String sql = "SELECT Id FROM `" + storage.tablePrefix + "Cuboids` WHERE WorldId = ? AND x1 <= ? "
				+ "AND x2 >= ? " + "AND y1 <= ? " + "AND y2 >= ? "
				+ "AND z1 <= ? " + "AND z2 >= ? " + "ORDER BY `" + storage.tablePrefix
				+ "Cuboids`.Priority DESC";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.setInt(2, x);
				ps.setInt(3, x);
				ps.setInt(4, y);
				ps.setInt(5, y);
				ps.setInt(6, z);
				ps.setInt(7, z);
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
	public String getName() {
		String name = "";
		String sql = "SELECT Name FROM `" + storage.tablePrefix + "Worlds` WHERE Id = ? LIMIT 1";

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
	public boolean exsists() {
		boolean ret = false;
		String sql = "SELECT COUNT(*) FROM `" + storage.tablePrefix + "Worlds` WHERE Id = ? LIMIT 1";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
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

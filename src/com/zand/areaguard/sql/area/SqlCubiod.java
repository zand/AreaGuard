package com.zand.areaguard.sql.area;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zand.areaguard.area.Area;
import com.zand.areaguard.area.Cubiod;
import com.zand.areaguard.area.World;
import com.zand.areaguard.error.area.ErrorArea;
import com.zand.areaguard.error.area.WorldError;

public class SqlCubiod implements Cubiod {
	final private SqlStorage storage;
	final private int id;
	
	public SqlCubiod(SqlStorage storage, int id) {
		this.storage = storage;
		this.id = id;
	}

	@Override
	public Area getArea() {
		Area area = null;
		String sql = "SELECT AreaId FROM `" + storage.tablePrefix + "Cubiods` WHERE Id = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) area = new SqlArea(storage, rs.getInt(1));
				else area = new ErrorArea("Not Found");

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				area = new ErrorArea("Sql");
			}

			storage.disconnect();
		} else area = new ErrorArea("No Sql Connection");
		return area;
	}

	@Override
	public long[] getCoords() {
		long coords[] = new long[6];
		String sql = "SELECT x1,y1,z1,x2,y2,z2 FROM `" + storage.tablePrefix + "Cubiods` WHERE Id = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) 
					for (int i = 0; i < 6; i++)
						coords[i] = rs.getLong(1 + i);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return coords;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getPriority() {
		int priority = 100;
		String sql = "SELECT Priority FROM `" + storage.tablePrefix + "Cubiods` WHERE Id = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) priority = rs.getInt(1);

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}

			storage.disconnect();
		}
		return priority;
	}

	@Override
	public World getWorld() {
		World world = null;
		String sql = "SELECT WorldId FROM `" + storage.tablePrefix + "Cubiods` WHERE Id = ?";

		if (storage.connect()) {
			try {
				PreparedStatement ps = storage.conn.prepareStatement(sql);
				ps.setInt(1, getId());
				ps.execute();

				// Get the result
				ResultSet rs = ps.getResultSet();
				if (rs.next()) world = storage.getWorld(rs.getInt(1));

				// Close events
				rs.close();
				ps.close();

			} catch (SQLException e) {
				e.printStackTrace();
				world = new WorldError();
			}

			storage.disconnect();
		} else world = new WorldError();
		return world;
	}

	@Override
	public boolean pointInCubiod(long x, long y, long z) {
		long coords[] = getCoords();
		return ((coords[0] <= x && coords[3] >= x) &&
				(coords[1] <= x && coords[4] >= x) &&
				(coords[2] <= x && coords[5] >= x));
	}

	@Override
	public boolean setArea(Area area) {
		String update = "UPDATE `" + storage.tablePrefix + "Cubiods` "
		+ "SET AreaId=? "
		+ "WHERE Id=?;";
		if (storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(update);
			ps.setInt(1, area.getId());
			ps.setInt(2, getId());
		
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			storage.disconnect();
			return false;
		}
		
		storage.disconnect();
		return true;
	}

	@Override
	public boolean setPriority(int priority) {
		String update = "UPDATE `" + storage.tablePrefix + "Cubiods` "
		+ "SET Priority=? "
		+ "WHERE Id=?;";
		if (storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(update);
			ps.setInt(1, priority);
			ps.setInt(2, getId());
		
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

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public boolean setLocation(World world, long[] coords) {
		String update = "UPDATE `" + storage.tablePrefix + "Cubiods` "
		+ "SET WorldId=?, x1=?, y1=?, z1=?, x2=?, y2=?, z2=? "
		+ "WHERE Id=?;";
		if (storage.connect())
			return false;
		try {
			PreparedStatement ps = storage.conn.prepareStatement(update);
			ps.setInt(1, world.getId());
			for (int i = 0; i < 6; i++)
				ps.setLong(i + 2,coords[i]);
			ps.setInt(8, getId());
		
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

}



import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashSet;

import com.zand.areaguard.Area;

public class CuboidReader {
	private ArrayList<CuboidC> cuboids = new ArrayList<CuboidC>();
	private String file = "AreaGuard/cuboidAreas.dat";
	
	public CuboidReader() {
		load(file); add();
	}
	
	public boolean add() {
		if (cuboids.isEmpty()) return false;
		Area area = null;
		for (CuboidC c : cuboids) {
			area = new Area(-1, c.name, c.coords);
			if (area.getId() != -1) {
				HashSet<String> restrict = new HashSet<String>();
				if (c.protection) {
					restrict.add("build");
					restrict.add("open");
				}
				if (c.restricted) restrict.add("enter");
				if (c.PvP) restrict.add("pvp");
				if (!c.heal) restrict.add("heal");
				if (c.creeper) restrict.add("creeper");
				if (c.sanctuary) restrict.add("mobs");
				area.addList("restrict", restrict);
				
				if (c.welcomeMessage != null) area.setMsg("enter", c.welcomeMessage);
				if (c.warning != null) area.setMsg("no-enter", c.warning);
				if (c.farewellMessage != null) area.setMsg("leave", c.farewellMessage);
				HashSet<String> owners = new HashSet<String>();
				HashSet<String> allow = new HashSet<String>();
				HashSet<String> groups = new HashSet<String>();
				
				for (String s : c.allowedPlayers) {
					s = s.toLowerCase();
					if (s.length() > 2) {
						if (s.startsWith("o:")) owners.add(s.substring(2));
						//if (s.startsWith("g:")) groups.add(s.substring(2));
					} else allow.add(s);
				}
				
				area.addList("owners", owners);
				area.addList("groups", groups);
				area.addList("allow", allow);
				
				System.out.println("Added " + area);
			}
			else System.out.println("Faild to add " + area);
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean load(String file) {
		cuboids = new ArrayList<CuboidC>();
		File dataSource = new File(file);
		if ( !dataSource.exists() ){
				// could not find file
			return false;
		}
		try {
			ObjectInputStream ois =
				new ObjectInputStream(
					new BufferedInputStream(
						new FileInputStream(
							new File(file))));
		    cuboids = (ArrayList<CuboidC>)( ois.readObject() );
		    ois.close();
		} catch (IOException e) {
			System.err.println("CuboidReader: Error while reading \"" + file + "\", " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		catch (ClassNotFoundException e) {
			System.err.println("CuboidReader: Error while reading \"" + file + "\", " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}

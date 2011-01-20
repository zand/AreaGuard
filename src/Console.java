

import java.io.PrintStream;

import com.zand.areaguard.Area;
import com.zand.areaguard.AreaDatabase;
import com.zand.areaguard.Config;
import com.zand.areaguard.Session;

public class Console {
	private final Session session = new Session();
	public final Config manager;
	private PrintStream out = System.out;

	public Console(Config manager) {
		this.manager = manager;
	}

	public void command(String[] args) {

		if (args.length > 0) {
			if (args[0].equals("help") || args[0].equals("?")) {
				printHelp();
			} else if (args[0].startsWith("deb")) { // debug
				if (args.length > 1) {
					if (args[1].startsWith("s")) { // setup
						manager.setup();
					}
					if (args[1].startsWith("l")) { // debug load
						if (args.length > 2) {

						}
					} else if (args[1].startsWith("c")) { // debug connect
						if (AreaDatabase.getInstance().connect()) {
							out.println("Succesfuly Connected");
						}
					} else if (args[1].startsWith("d")) { // debug disconnect
						out.println("Force Disconnect");
						AreaDatabase.getInstance().disconnect(true);
					} else if (args[1].startsWith("b")) { // debug bench
							out.println("Force Disconnect");
							for (int i = 1; i < 1000; i++) {
								//out.println(
										new Area("bench_"+i, new int[6]);
										//);
							}
					} else if (args[1].equals("erase")) { // debug erase
						if (args.length > 2) {
							if (args.length == 4 && args[3].equals("yes")) {
								if (args[2].equals("config")) {
									manager.deleteConfig();
								} if (args[2].equals("areas")) {
									out.println("Eraseing all Areas, please wait...");
									AreaDatabase.getInstance().removeAllAreas();
								} else
									out.println("Faild: " + args[2]
											+ " is not an Option");
							} else
								out.println(args[0] + " " + args[1]
										+ " " + args[2] + " yes - To erase "
										+ args[2]
										+ ", warning this is permanate");
						}
					}
				} else
					printDebugHelp();
			} else if (args[0].startsWith("po")) { // point
				if (args.length == 4) {
					session.setPoint(Integer.valueOf(args[1]), Integer
							.valueOf(args[2]), Integer.valueOf(args[3]));
					out.println("Point set (" + args[1] + ", " + args[2]
							+ ", " + args[3] + ")");
				}
			}

			// select
			else if (args[0].startsWith("sel")) {
				if (args.length > 1) {
					if (args[1].startsWith("i")) { // select id
						if (args.length > 2) {
							Area area = Area.getArea(
									new Integer(args[2]));
							if (area != null) {
								session.selected = area.getId();
								System.out.println("Selected area id ["
										+ session.selected + "] from id");
							} else
								out.println("Did not find area with id "
										+ args[2]);
						}
					} else if (args[1].startsWith("n")) { // select name
						if (args.length > 2) {
							Area area = Area.getArea(args[2]);
							if (area != null) {
								session.selected = area.getId();
								out.println("Selected area id ["
										+ session.selected + "] from point");
							} else
								out.println("Did not find area named "
										+ args[2]);
						}
					} else if (args[1].startsWith("p")) { // select point
						int[] loc = session.getPoint();
						Area area = Area.getArea(loc[0], loc[1], loc[2]);
						if (area != null) {
							session.selected = area.getId();
							out.println("Selected area id ["
									+ session.selected + "] from point");
						} else
							out.println("Did not find area at point");
					}
				}
			}

			// set
			else if (args[0].equals("set")) {
				if (args.length > 1) {
					Area area = Area.getArea(session.selected);
					if (area == null)
						out.println("No area selected");
					if (args[1].startsWith("n")) { // set name
						if (args.length > 2) {
							out.println((area.setName(args[2]) 
									? "Renamed"
									: "Faild to rename")
									+ " \""
									+ area.getName()
									+ "\" to \"" + args[2] + "\"");
						}
					}
				}
			} else if (args[0].startsWith("a")) { // add
				if (args.length > 1) {
					out.println((new Area(args[1], session.getCoords())
							.getId() != -1 ? "Added" : "Faild to add")
							+ " \"" + args[1] + "\"");
				}
			} else if (args[0].startsWith("rem")) { // remove
				if (args.length > 1) {
					if (args[1].startsWith("s")) { // remove selected
						if (Area.remove(session.selected))
							System.out.println("Selected area removed");
					}
				}
			} else if (args[0].startsWith("pr")) { // print
				if (args.length > 1) {
					if (args[1].startsWith("a")) { // print all
						out.println("==Area List==");
						out.println("[Id] \tName \t@ Coords");
						for (int i : AreaDatabase.getInstance().getAreaIds()) {
							out.println(AreaDatabase.getInstance()
									.getArea(i));
						}
					}
				} else
					printAreaInfo(AreaDatabase.getInstance().getArea(
							session.selected));
			} else if (!args[0].isEmpty())
				out.println("Unknown option");

		}

	}

	public void printAreaInfo(Area area) {
		if (area == null) {
			out.println("Area is null");
		} else {
			out.println(area);
			for (String list : area.getLists()) {
				out.print("  " + list + ":");
				for (String value : area.getList(list))
					out.print(" " + value);
				out.print("\n");
			}
		}
	}

	public void printDebugHelp() {
		out.println("AreaGuard: Debug Help");
		out.println("debug setup - Redo the setup thats done at startup");
		out.println("debug connect - Starts the database connectoin");
		out.println("debug disconnect - Stops the database connectoin");
		out.println("debug erase config - Erases the config file");
	}

	public void printHelp() {
		out.println("AreaGuard: Help");
		out.println("help|? - Displays this");
		out.println("add [name] - Creates a area from last 2 selected points");
		out.println("debug -  Debug commands");
		out.println("point [x] [y] [z] - Sets the current point");
		out.println("print - Prints the info on a selected area");
		out.println("select name [name] - Sellects a area from name");
		out.println("select point - Sellects a area from the current point");
	}

}

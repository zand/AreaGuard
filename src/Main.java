import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import com.zand.areaguard.*;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Config man = new Config();
		Console con = new Console(man);
		Calendar time;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (args.length == 0 || !args[0].equals("quit")) {
				time = Calendar.getInstance();
				if (args.length == 2 && args[0].equals("cuboid") &&  args[1].equals("load"))
					new CuboidReader();
				else con.command(args);
				System.out.print(Calendar.getInstance().getTimeInMillis()-time.getTimeInMillis()+"ms> ");
				args = reader.readLine().split(" ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

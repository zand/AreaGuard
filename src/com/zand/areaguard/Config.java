package com.zand.areaguard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class Config {
	private static final String config = getConfigDir() + "/areaguard.properties";;
	public static int createTool;
	public static int checkTool;
	public static HashSet<String> creators = new HashSet<String>();
	public static HashSet<String> defaultRestict = new HashSet<String>();
	
	
	public static boolean  isCreator(String name) {
		return creators.contains(name.toLowerCase());
	}

	public Config() {
		setup();
	}

	public static void deleteConfig() {
		System.out.println("Deleating \"" + config + "\"");
		File f = new File(config);
		if (f.exists())
			f.delete();
	}

	public static boolean readConfig(String file) {
		System.out.println("Reading config \"" + config + "\"");
		Properties props = new Properties();

		try {
			props.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			System.err.println("AreaGuard: Config file not Found \"" + file
					+ "\"");
			return false;
		} catch (IOException e) {
			System.err.println("AreaGuard: IO Error while loading config \""
					+ file + "\", " + e.getMessage());
			return false;
		}
		for (String creator : props.getProperty("area-creators").split(" "))
			creators.add(creator.toLowerCase());
		createTool = Integer.valueOf(props.getProperty("create-tool"));
		checkTool = Integer.valueOf(props.getProperty("check-tool"));
		for (String name : props.getProperty("default-restrict").split(" "))
			defaultRestict.add(name);
		
		// Configure Connection
		String url = props.getProperty("url");
		
		// Figure out what driver to use
		String driver = props.getProperty("driver");
		if (driver == null || driver.isEmpty() || driver.equalsIgnoreCase("auto")) {
			String lower = url.toLowerCase().replaceAll("\\\\", "");
			if 		(lower.startsWith("jdbc:sqlite:")) 	driver = "org.sqlite.JDBC";
			else if (lower.startsWith("jdbc:mysql:"))	driver = "com.mysql.jdbc.Driver";
			else System.err.println("Coulden't figuer out driver from url");
		}
		
		
		
		return true;
	}
	
	public static String getConfigDir() {
		return "plugins/AreaGuard";
	}

	public static boolean setup() {
		System.out.println("AreaGuard: Setup");

		// Test the config file
		File f = new File(config);
		boolean found = f.exists();
		System.out.println("Config file \"" + config + "\" "
				+ (found ? "found" : "missing"));
		if (!found) {
			try {
				System.out.println("Creating \"" + config + "\"");
				List<String> data = JarFile.toList("data/areaguard.properties");
				
				(new File(getConfigDir())).mkdir();
				f.createNewFile();
				FileWriter w = new FileWriter(config);
				BufferedWriter out = new BufferedWriter(w);
				
				// Write the config data
				for (String line : data) {
					out.write(line); 
					out.newLine();
				}
				out.close();
			} catch (IOException e) {
				System.err.println("Error: " + e.getMessage());
			}
		}

		// read the config
		readConfig(config);

		return true;
	}
}

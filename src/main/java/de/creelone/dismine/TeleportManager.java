package de.creelone.dismine;

import java.io.File;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import de.creelone.dismine.util.TeleportLocation;

public class TeleportManager {
	
	private static File warpsFile = new File("plugins/Dismine/warps.yml");
	private static YamlConfiguration warps = YamlConfiguration.loadConfiguration(warpsFile);

	private static File homesFile = new File("plugins/Dismine/homes.yml");
	private static YamlConfiguration homes = YamlConfiguration.loadConfiguration(homesFile);

	public static void addWarp(TeleportLocation loc) {
		warps.set(loc.getName(), loc.getLocation());
		saveWarps();
	}

	public static void delWarp(String name) {
		warps.set(name, null);
		saveWarps();
	}

	public static Location getWarp(String name) {
		return warps.getLocation(name);
	}

	public static TeleportLocation[] getWarps() {
		TeleportLocation[] warps = new TeleportLocation[TeleportManager.warps.getKeys(false).size()];
		int i = 0;
		for (String key : TeleportManager.warps.getKeys(false)) {
			warps[i] = new TeleportLocation(TeleportManager.warps.getLocation(key), key);
			i++;
		}
		return warps;
	}

	public static void saveWarps() {
		try {
			warps.save(warpsFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addHome(UUID owner, TeleportLocation loc) {
		homes.set(owner.toString() + "." + loc.getName(), loc.getLocation());
		saveHomes();
	}

	public static void delHome(UUID owner, String name) {
		homes.set(owner.toString() + "." + name, null);
		saveHomes();
	}

	public static Location getHome(UUID owner, String name) {
		return homes.getLocation(owner.toString() + "." + name);
	}

	public static TeleportLocation[] getHomes(UUID owner) {
		if(TeleportManager.homes.getConfigurationSection(owner.toString()) == null) return new TeleportLocation[0];
		TeleportLocation[] homes = new TeleportLocation[TeleportManager.homes.getConfigurationSection(owner.toString()).getKeys(false).size()];
		int i = 0;
		for (String key : TeleportManager.homes.getConfigurationSection(owner.toString()).getKeys(false)) {
			homes[i] = new TeleportLocation(TeleportManager.homes.getLocation(owner.toString() + "." + key), key);
			i++;
		}
		return homes;
	}

	public static void saveHomes() {
		try {
			homes.save(homesFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

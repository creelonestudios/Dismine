package de.creelone.dismine;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import de.creelone.dismine.util.TeleportLocation;
import it.unimi.dsi.fastutil.Arrays;

public class TeleportManager {

	public static void initTables() {
		Dismine.instance.getLogger().info("Initializing warp and home tables...");
		Dismine.instance.sql.update("CREATE TABLE IF NOT EXISTS warps (id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, PRIMARY KEY (id))");
		Dismine.instance.sql.update("CREATE TABLE IF NOT EXISTS homes (id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, owner VARCHAR(255), name VARCHAR(255), world VARCHAR(255), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT)");
	}

	public static void addWarp(TeleportLocation loc) {
		// warps.set(loc.getName(), loc.getLocation());
		// saveWarps();
		PreparedStatement ps = Dismine.instance.sql.prepare("INSERT INTO warps (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)");
		try {
			ps.setString(1, loc.getName());
			ps.setString(2, loc.getLocation().getWorld().getName());
			ps.setDouble(3, loc.getLocation().getX());
			ps.setDouble(4, loc.getLocation().getY());
			ps.setDouble(5, loc.getLocation().getZ());
			ps.setFloat(6, loc.getLocation().getYaw());
			ps.setFloat(7, loc.getLocation().getPitch());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delWarp(String name) {
		// warps.set(name, null);
		// saveWarps();
		PreparedStatement ps = Dismine.instance.sql.prepare("DELETE FROM warps WHERE name = ?");
		try {
			ps.setString(1, name);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Location getWarp(String name) {
		// return warps.getLocation(name);
		PreparedStatement ps = Dismine.instance.sql.prepare("SELECT * FROM warps WHERE name = ?");
		try {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				World world = Dismine.instance.getServer().getWorld(rs.getString("world"));
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");
				return new Location(world, x, y, z, yaw, pitch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static TeleportLocation[] getWarps() {
		// TeleportLocation[] warps = new TeleportLocation[TeleportManager.warps.getKeys(false).size()];
		// int i = 0;
		// for (String key : TeleportManager.warps.getKeys(false)) {
		// 	warps[i] = new TeleportLocation(TeleportManager.warps.getLocation(key), key);
		// 	i++;
		// }
		// return warps;
		PreparedStatement ps = Dismine.instance.sql.prepare("SELECT * FROM warps");
		try {
			ResultSet rs = ps.executeQuery();
			List<TeleportLocation> warpList = new ArrayList<>();
			int i = 0;
			while(rs.next()) {
				World world = Dismine.instance.getServer().getWorld(rs.getString("world"));
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				Location loc = new Location(world, x, y, z, yaw, pitch);
				warpList.add(new TeleportLocation(loc, rs.getString("name")));
				i++;
			}
			TeleportLocation[] warps = new TeleportLocation[warpList.size()];
			for(int j = 0; j < warpList.size(); j++) {
				warps[j] = (TeleportLocation) warpList.get(j);
			}
			return warps;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveWarps() {
		// try {
		// 	warps.save(warpsFile);
		// } catch (Exception e) {
		// 	e.printStackTrace();
		// }
	}

	public static void addHome(UUID owner, TeleportLocation loc) {
		// homes.set(owner.toString() + "." + loc.getName(), loc.getLocation());
		// saveHomes();
		PreparedStatement ps = Dismine.instance.sql.prepare("INSERT INTO homes (owner, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		try {
			ps.setString(1, owner.toString());
			ps.setString(2, loc.getName());
			ps.setString(3, loc.getLocation().getWorld().getName());
			ps.setDouble(4, loc.getLocation().getX());
			ps.setDouble(5, loc.getLocation().getY());
			ps.setDouble(6, loc.getLocation().getZ());
			ps.setFloat(7, loc.getLocation().getYaw());
			ps.setFloat(8, loc.getLocation().getPitch());
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void delHome(UUID owner, String name) {
		// homes.set(owner.toString() + "." + name, null);
		// saveHomes();
		PreparedStatement ps = Dismine.instance.sql.prepare("DELETE FROM homes WHERE owner = ? AND name = ?");
		try {
			ps.setString(1, owner.toString());
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Location getHome(UUID owner, String name) {
		// return homes.getLocation(owner.toString() + "." + name);
		PreparedStatement ps = Dismine.instance.sql.prepare("SELECT * FROM homes WHERE owner = ? AND name = ?");
		try {
			ps.setString(1, owner.toString());
			ps.setString(2, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				World world = Dismine.instance.getServer().getWorld(rs.getString("world"));
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				Location loc = new Location(world, x, y, z, yaw, pitch);
				return loc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static TeleportLocation[] getHomes(UUID owner) {
		// if(TeleportManager.homes.getConfigurationSection(owner.toString()) == null) return new TeleportLocation[0];
		// TeleportLocation[] homes = new TeleportLocation[TeleportManager.homes.getConfigurationSection(owner.toString()).getKeys(false).size()];
		// int i = 0;
		// for (String key : TeleportManager.homes.getConfigurationSection(owner.toString()).getKeys(false)) {
		// 	homes[i] = new TeleportLocation(TeleportManager.homes.getLocation(owner.toString() + "." + key), key);
		// 	i++;
		// }
		// return homes;
		PreparedStatement ps = Dismine.instance.sql.prepare("SELECT * FROM homes WHERE owner = ?");
		try {
			ps.setString(1, owner.toString());
			ResultSet rs = ps.executeQuery();
			List<TeleportLocation> homeList = new ArrayList<>();
			int i = 0;
			while(rs.next()) {
				World world = Dismine.instance.getServer().getWorld(rs.getString("world"));
				double x = rs.getDouble("x");
				double y = rs.getDouble("y");
				double z = rs.getDouble("z");
				float yaw = rs.getFloat("yaw");
				float pitch = rs.getFloat("pitch");

				Location loc = new Location(world, x, y, z, yaw, pitch);
				homeList.add(new TeleportLocation(loc, rs.getString("name")));
				i++;
			}
			TeleportLocation[] homes = new TeleportLocation[homeList.size()];
			for(int j = 0; j < homeList.size(); j++) {
				homes[j] = (TeleportLocation) homeList.get(j);
			}
			return homes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void saveHomes() {
		// try {
		// 	homes.save(homesFile);
		// } catch (Exception e) {
		// 	e.printStackTrace();
		// }
	}

	public static void migrateFileToSQL() {
		File warpsFile = new File("plugins/Dismine/warps.yml");
		File homesFile = new File("plugins/Dismine/homes.yml");
		if(!warpsFile.exists() || !homesFile.exists()) {
			return;
		}

		Dismine.instance.getLogger().info("Migrating warps.yml and homes.yml to SQL database, this may take a few seconds...");
		
		YamlConfiguration warps = YamlConfiguration.loadConfiguration(warpsFile);
		YamlConfiguration homes = YamlConfiguration.loadConfiguration(homesFile);

		for (String key : warps.getKeys(false)) {
			Location loc = warps.getLocation(key);
			PreparedStatement ps = Dismine.instance.sql.prepare("INSERT INTO warps (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)");
			try {
				ps.setString(1, key);
				ps.setString(2, loc.getWorld().getName());
				ps.setDouble(3, loc.getX());
				ps.setDouble(4, loc.getY());
				ps.setDouble(5, loc.getZ());
				ps.setFloat(6, loc.getYaw());
				ps.setFloat(7, loc.getPitch());

				ps.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (String owner : homes.getKeys(false)) {
			for (String home : homes.getConfigurationSection(owner).getKeys(false)) {
				Location loc = homes.getLocation(owner + "." + home);
				PreparedStatement ps = Dismine.instance.sql.prepare("INSERT INTO homes (owner, name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				try {
					ps.setString(1, owner);
					ps.setString(2, home);
					ps.setString(3, loc.getWorld().getName());
					ps.setDouble(4, loc.getX());
					ps.setDouble(5, loc.getY());
					ps.setDouble(6, loc.getZ());
					ps.setFloat(7, loc.getYaw());
					ps.setFloat(8, loc.getPitch());

					ps.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		warpsFile.delete();
		homesFile.delete();

		Dismine.instance.getLogger().info("Successfully migrated warps.yml and homes.yml to SQL");
	}

}

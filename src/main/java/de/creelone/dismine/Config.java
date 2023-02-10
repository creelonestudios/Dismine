package de.creelone.dismine;

import discord4j.common.util.Snowflake;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class Config {

	static String DC_TOKEN;
	static Snowflake DC_CHANNEL_CHAT;
	static Snowflake DC_CHANNEL_CONSOLE;
	static List<String> DC_OPERATORS;

	static String MYSQL_HOST;
	static int MYSQL_PORT;
	static String MYSQL_DB;
	static String MYSQL_USERNAME;
	static String MYSQL_PASSWORD;

	static void load(Dismine dismine) {
		// Config
		File cfgFile = new File(dismine.getDataFolder(), "config.yml");
		try {
			if(!cfgFile.exists()) {
				cfgFile.getParentFile().mkdirs();
				cfgFile.createNewFile();
				FileWriter writer = new FileWriter(cfgFile);
				writer.write("discord: \"\"\n");
				writer.write("  token: \"\"\n");
				writer.write("  channels:\n");
				writer.write("    chat: \"\"\n");
				writer.write("    console: \"\"\n");
				writer.write("mysql:\n");
				writer.write("  host: \"localhost\"\n");
				writer.write("  port: 3306\n");
				writer.write("  db: \"dismine\"\n");
				writer.write("  username: \"root\"\n");
				writer.write("  password: \"\"\n");
				writer.close();
			}
		} catch (IOException e) {
			// ¯\_(ツ)_/¯
			e.printStackTrace();
		}
		var cfg = YamlConfiguration.loadConfiguration(cfgFile);

		if(cfg.contains("sql")) {
			cfg.set("mysql.host", cfg.getString("sql.host"));
			cfg.set("mysql.port", cfg.getInt("sql.port"));
			cfg.set("mysql.db", cfg.getString("sql.db"));
			cfg.set("mysql.username", cfg.getString("sql.username"));
			cfg.set("mysql.password", cfg.getString("sql.password"));
			cfg.set("sql", null);
			try {
				cfg.save(cfgFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		DC_TOKEN = cfg.getString("discord.token");
		var dcch = cfg.getString("discord.channels.chat");
		var dcco = cfg.getString("discord.channels.console");
		if (dcch == null || dcco == null) return; // TODO: Log Error
		DC_CHANNEL_CHAT = Snowflake.of(dcch);
		DC_CHANNEL_CONSOLE = Snowflake.of(dcco);
		DC_OPERATORS = cfg.getStringList("discord.operators");

		MYSQL_HOST = cfg.getString("mysql.host");
		MYSQL_PORT = cfg.getInt("mysql.port");
		MYSQL_DB = cfg.getString("mysql.db");
		MYSQL_USERNAME = cfg.getString("mysql.username");
		MYSQL_PASSWORD = cfg.getString("mysql.password");

		dismine.getLogger().log(Level.INFO, "Config loaded!");
	}

}

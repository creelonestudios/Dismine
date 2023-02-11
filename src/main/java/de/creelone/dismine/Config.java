package de.creelone.dismine;

import discord4j.common.util.Snowflake;

import java.util.List;
import java.util.logging.Level;

public class Config {

	static boolean DC_ENABLED;
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
		dismine.saveDefaultConfig();
		var cfg = dismine.getConfig();

		DC_ENABLED = cfg.getBoolean("discord.enabled");
		if(DC_ENABLED) {
			DC_TOKEN = cfg.getString("discord.token");
			var dcch = cfg.getString("discord.channels.chat");
			var dcco = cfg.getString("discord.channels.console");
			if (dcch == null || dcco == null) return; // TODO: Log Error
			DC_CHANNEL_CHAT = Snowflake.of(dcch);
			DC_CHANNEL_CONSOLE = Snowflake.of(dcco);
			DC_OPERATORS = cfg.getStringList("discord.operators");
		}

		MYSQL_HOST = cfg.getString("mysql.host");
		MYSQL_PORT = cfg.getInt("mysql.port");
		MYSQL_DB = cfg.getString("mysql.db");
		MYSQL_USERNAME = cfg.getString("mysql.username");
		MYSQL_PASSWORD = cfg.getString("mysql.password");

		dismine.getLogger().log(Level.INFO, "Config loaded!");
	}

}

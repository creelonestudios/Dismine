package de.creelone.dismine;

import discord4j.common.util.Snowflake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public final class Dismine extends JavaPlugin {

	public static Dismine instance;
	public static final HashSet<Identity> identities = new HashSet<>();

	enum MessageSource {
		DISCORD("☺", 0x7289DA, "<:clyde:997587125727928383>"),
		MCJAVA("⛏", 0x558e43, "<:mcjava:997587410571501708>");

		public final String mcicon;
		public final int mciconColor;
		public final String dcicon;

		private MessageSource(String mcicon, int mciconColor, String dcicon) {
			this.mcicon = mcicon;
			this.mciconColor = mciconColor;
			this.dcicon = dcicon;
		}
	}

	public static Identity getIdentityByDcid(Snowflake dcid) {
		for (Identity i : identities) {
			if (i.getDcid().asLong() == dcid.asLong()) {
				return i;
			}
		}
		return new Identity(dcid, null);
	}

	public static Identity getIdentityByUuid(UUID uuid) {
		for (Identity i : identities) {
			if (i.getUuid().toString().equals(uuid.toString())) {
				return i;
			}
		}
		return new Identity(null, uuid);
	}

	YamlConfiguration cfg;
	String TOKEN;
	Snowflake CHANNEL_ID;
	DiscordStuff dc;
	MySQL sql;
	double lastTPS = 20;

	@Override
	@SuppressWarnings("ConstantConditions")
	public void onEnable() {
		instance = this;
		// Config
		File cfgFile = new File(getDataFolder(), "config.yml");
		try {
			if(!cfgFile.exists()) {
				cfgFile.mkdirs();
				cfgFile.createNewFile();
				FileWriter writer = new FileWriter(cfgFile);
				writer.write("token: \"\"\n");
				writer.write("channels:\n");
				writer.write("\tchat: \"\"\n");
				writer.write("\tconsole: \"\"\n");
				writer.write("mysql:\n");
				writer.write("\thost: \"localhost\"\n");
				writer.write("\tport: 3306\n");
				writer.write("\tdb: \"dismine\"\n");
				writer.write("\tusername: \"root\"\n");
				writer.write("\tpassword: \"\"\n");
				writer.close();
			}
		} catch (IOException e) {
			// ¯\_(ツ)_/¯
		}
		cfg = YamlConfiguration.loadConfiguration(cfgFile);
		TOKEN = cfg.getString("token");
		getServer().getLogger().log(Level.CONFIG, cfg.getString("channels.chat"));
		CHANNEL_ID = Snowflake.of(cfg.getString("channels.chat"));
		// Discord
		dc = DiscordStuff.getInstance();
		dc.login();
		dc.sendMessage(":white_check_mark: **Server started!**");
		// Commands
		getCommand("shrug").setExecutor(new EmoteCommand());
		getCommand("tableflip").setExecutor(new EmoteCommand());
		getCommand("unflip").setExecutor(new EmoteCommand());
		getCommand("doubleflip").setExecutor(new EmoteCommand());
		getCommand("owo").setExecutor(new EmoteCommand());
		getCommand("sync").setExecutor(new SyncCommand(dc));
		// Events
		getServer().getPluginManager().registerEvents(new Events(this, dc), this);
		// TPS
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				double tps = getServer().getTPS()[0];
				if(tps < 15 && lastTPS > 15) {
					dc.sendMessage(":chart_with_downwards_trend: TPS fell below 15");
				}
				if(tps < 10 && lastTPS > 10) {
					dc.sendMessage(":chart_with_downwards_trend: TPS fell below 10");
				}
				if(tps > 10 && lastTPS < 10) {
					dc.sendMessage(":chart_with_upwards_trend: TPS is now above 10 again");
				}
				if(tps == 20 && lastTPS < 19) {
					dc.sendMessage(":chart_with_upwards_trend: TPS is now at 20 again");
				}

				lastTPS = tps;
			}
		}, 0, 20);
		// MySQL
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
		sql = new MySQL(cfg.getString("mysql.host"), cfg.getInt("mysql.port"), cfg.getString("mysql.db"), cfg.getString("mysql.username"), cfg.getString("mysql.password"));
		sql.connect();
		// TODO: eat tables
		sql.update("CREATE TABLE IF NOT EXISTS identities (dcid varchar(100), uuid varchar(100))");
		ResultSet rs = sql.getResult("SELECT * FROM identities");
		while (true) {
			try {
				if (!rs.next()) break;
				identities.add(new Identity(Snowflake.of(rs.getString("dcid")), UUID.fromString(rs.getString("uuid"))));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void onDisable() {
		// Discord
		dc.sendMessage(":octagonal_sign: **Server shutting down...**");
		dc.logout();
	}

	public TextComponent createChatMsg(MessageSource src, Identity identity, Component name, String content) {
		getLogger().log(Level.INFO, identity.toString());
		Component text = MsgConverter.convertToMC(content);

		TextComponent hovercomp = Component.text("").append(name)
				.append(Component.newline())
				.append(Component.text("Username: ")
						.color(TextColor.color(0xffffff)))
				.append(Component.text(identity.getTag())
						.color(TextColor.color(identity.getAccentColor())))
				.append(Component.newline())
				.append(Component.text("MC Name:   ")
						.color(TextColor.color(0xffffff)))
				.append(identity.getPlayerDisplayName()
						.colorIfAbsent(TextColor.color(0xffffff)));

		var team = identity.getTeam();
		if (team != null) {
			hovercomp = hovercomp.append(Component.newline())
					.append(Component.text("Team:       ")
							.color(TextColor.color(0xffffff)))
					.append(identity.getTeamName()
							.color(TextColor.color(identity.getTeamColor())));
		}

		TextComponent comp = Component.text("")
				.append(name
						.hoverEvent(hovercomp))
				.append(Component.text(": ")
						.color(name.color()))
				.append(text);

		return comp;
	}
}

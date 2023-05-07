package de.creelone.dismine;

import de.creelone.dismine.cmds.*;
import discord4j.common.util.Snowflake;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public final class Dismine extends JavaPlugin {

	public static Dismine instance;
	public static final HashSet<Identity> identities = new HashSet<>();

	private static final ArrayList<String> cmdQueue = new ArrayList<>();

	enum MessageSource {
		DISCORD("☺", 0x7289DA, "<:clyde:997587125727928383>"),
		MCJAVA("⛏", 0x558e43, "<:mcjava:997587410571501708>");

		public final String mcicon;
		public final int mciconColor;
		public final String dcicon;

		MessageSource(String mcicon, int mciconColor, String dcicon) {
			this.mcicon = mcicon;
			this.mciconColor = mciconColor;
			this.dcicon = dcicon;
		}
	}

	static {
		// Discord
		DiscordStuff.sendMessage(":white_check_mark: **Server started!**");

		// JVM Shutdown Hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// Discord
			DiscordStuff.sendMessage(":octagonal_sign: **Server shutting down...**");
			DiscordStuff.logout();
		}));
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

	public static synchronized void queueCommand(String cmd) {
		cmdQueue.add(cmd);
	}

	public MySQL sql;
	double lastTPS = 20;

	public Dismine() {
		// Config
		Config.load(this);
		instance = this;

		var consneaker = new ConsoleSneaker();
		if(Config.DC_ENABLED) {
			DiscordStuff.login();
	
			// ConsoleSneaker
			((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(consneaker);
		}
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public void onEnable() {
		// Commands
		getCommand("shrug").setExecutor(new EmoteCommand());
		getCommand("tableflip").setExecutor(new EmoteCommand());
		getCommand("unflip").setExecutor(new EmoteCommand());
		getCommand("doubleflip").setExecutor(new EmoteCommand());
		getCommand("owo").setExecutor(new EmoteCommand());
		getCommand("sync").setExecutor(new SyncCommand());
		getCommand("nick").setExecutor(new NickCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("gm").setExecutor(new GmCommand());
		getCommand("speed").setExecutor(new SpeedCommand());
		getCommand("flyspeed").setExecutor(new SpeedCommand());
		getCommand("walkspeed").setExecutor(new SpeedCommand());
		getCommand("fly").setExecutor(new FlyCommand());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("warp").setExecutor(new WarpCommand());
		getCommand("setwarp").setExecutor(new WarpCommand());
		getCommand("delwarp").setExecutor(new WarpCommand());
		getCommand("home").setExecutor(new HomeCommand());
		getCommand("homes").setExecutor(new HomeCommand());
		getCommand("sethome").setExecutor(new HomeCommand());
		getCommand("delhome").setExecutor(new HomeCommand());
		getCommand("godmode").setExecutor(new GodCommand());
		// Events
		getServer().getPluginManager().registerEvents(new Events(), this);
		getServer().getPluginManager().registerEvents(new VanishCommand(), this);
		// TPS
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			double tps = getServer().getTPS()[0];
			if(tps < 15 && lastTPS > 15) {
				DiscordStuff.sendMessage(":chart_with_downwards_trend: TPS fell below 15");
			}
			if(tps < 10 && lastTPS > 10) {
				DiscordStuff.sendMessage(":chart_with_downwards_trend: TPS fell below 10");
			}
			if(tps > 10 && lastTPS < 10) {
				DiscordStuff.sendMessage(":chart_with_upwards_trend: TPS is now above 10 again");
			}
			if(tps == 20 && lastTPS < 19) {
				DiscordStuff.sendMessage(":chart_with_upwards_trend: TPS is now at 20 again");
			}

			lastTPS = tps;

			// commands (console input from discord)
			for (var cmd : cmdQueue) {
				try {
					Dismine.instance.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
				} catch (CommandException e) {
					Dismine.instance.getLogger().log(Level.INFO, "CommandException soos: " + e.getMessage());
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			cmdQueue.clear();
		}, 0, 20);
		// MySQL
		sql = new MySQL(Config.MYSQL_HOST, Config.MYSQL_PORT, Config.MYSQL_DB, Config.MYSQL_USERNAME, Config.MYSQL_PASSWORD);
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

		return Component.text("")
				.append(name
						.hoverEvent(hovercomp))
				.append(Component.text(": ")
						.color(name.color()))
				.append(text);
	}
}

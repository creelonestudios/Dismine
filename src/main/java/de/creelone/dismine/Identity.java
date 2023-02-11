package de.creelone.dismine;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.NoSuchElementException;
import java.util.UUID;

public class Identity {

	private final Snowflake dcid;
	private final UUID uuid;
	private final boolean synced;

	public Identity(Snowflake dcid, UUID uuid) {
		this.dcid = dcid;
		this.uuid = uuid;
		this.synced = dcid != null && uuid != null;
	}

	public Snowflake getDcid() {
		return dcid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public User getUser() {
		if (!DiscordStuff.isReady() || this.dcid == null) return null;
		var gateway = DiscordStuff.getGateway();
		return gateway.getUserById(dcid).block();
	}

	public String getUsername() {
		var user = this.getUser();
		if (user == null) return "Unknown";
		return user.getUsername();
	}

	public String getTag() {
		var user = this.getUser();
		if (user == null) return "Unknown#0000";
		return user.getTag();
	}

	public int getAccentColor() {
		var user = this.getUser();
		if (user == null) return 0xffffff;
		try {
			if (user.getAccentColor().isEmpty()) return 0xffffff;
			return user.getAccentColor().get().getRGB();
		} catch (NoSuchElementException e) {
			return 0xffffff;
		}
	}

	public OfflinePlayer getPlayer() {
		if (this.uuid == null) return null;
		return Bukkit.getOfflinePlayer(uuid);
	}

	public String getPlayerName() {
		var player = getPlayer();
		if (player == null) return getRandomPlaceholderName();
		if(player.isOnline()) return PlainTextComponentSerializer.plainText().serialize(player.getPlayer().displayName());
		return player.getName();
	}

	public String getRealPlayerName() {
		var player = getPlayer();
		if (player == null) return getRandomPlaceholderName();
		return player.getName();
	}

	public TextComponent getPlayerDisplayName() {
		return Component.text("").color(getTeamColor()).append(getTeamPrefix()).append(Component.text(getPlayerName()).color(getTeamColor())).append(getTeamSuffix());
	}

	public TextComponent getRealPlayerDisplayName() {
		return Component.text("").color(getTeamColor()).append(getTeamPrefix()).append(Component.text(getRealPlayerName()).color(getTeamColor())).append(getTeamSuffix());
	}

	public Team getTeam() {
		var player = getPlayer();
		if (player == null) return null;
		Scoreboard board = Dismine.instance.getServer().getScoreboardManager().getMainScoreboard();
		return board.getPlayerTeam(player);
	}

	public Component getTeamPrefix() {
		var team = getTeam();
		if (team == null) return Component.empty();
		var prefix = team.prefix();
		prefix = prefix.colorIfAbsent(getTeamColor());
		return prefix;
	}

	public Component getTeamSuffix() {
		var team = getTeam();
		if (team == null) return Component.empty();
		var suffix = team.suffix();
		suffix = suffix.colorIfAbsent(getTeamColor());
		return suffix;
	}

	public Component getTeamName() {
		var team = getTeam();
		if (team == null) return Component.empty();
		return team.displayName();
	}

	public TextColor getTeamColor() {
		var team = getTeam();
		if (team == null) return TextColor.color(0xffffff);
		return team.color();
	}

	public String getTeamPrefixString() {
		var team = getTeam();
		if (team == null) return "";
		return team.getPrefix();
	}

	public String getTeamSuffixString() {
		var team = getTeam();
		if (team == null) return "";
		return team.getSuffix();
	}

	public String toString() {
		var team = getTeam();
		var teamname = team != null ? team.getName() : "";
		return String.format("Identity{dcid:%s,uuid:%s,dcname:%s,mcname:%s,team:%s}", dcid != null ? dcid.asString() : "null", uuid != null ? uuid.toString() : "null", getTag(), getPlayerName(), teamname);
	}

	static String getRandomPlaceholderName() {
		String[] placeholders = new String[] {"¯\\_(ツ)_/¯","\\(OwO)/","(╯°□°）╯︵ ┻━┻","┬─┬ ノ( ゜-゜ノ)","┻━┻彡 ヽ(ಠ益ಠ)ノ彡┻━┻"};
		int random = (int) Math.floor(Math.random() * placeholders.length);
		return placeholders[random];
	}

}

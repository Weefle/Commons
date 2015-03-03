package com.caved_in.commons.command.commands;

import com.caved_in.commons.Messages;
import com.caved_in.commons.bans.Punishment;
import com.caved_in.commons.bans.PunishmentBuilder;
import com.caved_in.commons.bans.PunishmentType;
import com.caved_in.commons.chat.Chat;
import com.caved_in.commons.command.*;
import com.caved_in.commons.permission.Perms;
import com.caved_in.commons.player.Players;
import com.caved_in.commons.time.DateUtils;
import com.mysql.jdbc.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand {
	@Command(identifier = "ban", permissions = {Perms.COMMAND_BAN})
	@Flags(identifier = {"t"}, description = {"Time to ban the player for"})
	public void banCommand(CommandSender sender, @Arg(name = "player") String target, @FlagArg("t") boolean timed, @Arg(name = "time", def = "0") String timeDuration, @Wildcard @Arg(name = "reason") String reason) {
		final UUID senderId = (sender instanceof Player) ? ((Player) sender).getUniqueId() : UUID.randomUUID();

		long time = 0;
		boolean permanent = false;

		if (StringUtils.isNullOrEmpty(reason.trim())) {
			Chat.message(sender, Messages.invalidCommandUsage("reason"));
			return;
		}

		if (timeDuration.equalsIgnoreCase("0") || !timed) {
			permanent = true;
		}

		if (!permanent) {
			try {
				time = DateUtils.parseDateDiff(timeDuration, true);
			} catch (Exception e) {
				Chat.message(sender, "&eThe time you entered has an invalid format, try again &aplease&e!");
				return;
			}
		}

		if (time == 0 && !permanent) {
			Chat.message(sender, "You need to include the time duration.\n&a--> &e/ban " + target + " -t 10y1m2w Being a griefer.");
			return;
		}

		Punishment punishment = new PunishmentBuilder()
				.withType(PunishmentType.BAN)
				.withIssuer(senderId)
				.withReason(reason)
				.permanent(permanent)
				.issuedOn(System.currentTimeMillis())
				.expiresOn(Long.sum(System.currentTimeMillis(), time))
				.build();

		if (Players.isOnline(target)) {
			Players.ban(Players.getPlayer(target), punishment);
		} else {
			Players.ban(target, punishment);
		}
	}

}

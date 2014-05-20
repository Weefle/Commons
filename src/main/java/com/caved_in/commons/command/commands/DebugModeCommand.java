package com.caved_in.commons.command.commands;

import com.caved_in.commons.Messages;
import com.caved_in.commons.command.Command;
import com.caved_in.commons.debug.Debugger;
import com.caved_in.commons.player.PlayerWrapper;
import com.caved_in.commons.player.Players;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class DebugModeCommand {

	@Command(name = "debug", permission = "commons.debugmode")
	public void onDebugModeCommand(Player player, String[] args) {
		PlayerWrapper playerWrapper = Players.getData(player);
		if (args.length == 0) {
			playerWrapper.setInDebugMode(!playerWrapper.isInDebugMode());
			Players.sendMessage(player, Messages.playerDebugModeChange(playerWrapper));
			Players.updateData(playerWrapper);
			return;
		}

		String action = args[0];
		if (Debugger.isDebugAction(action)) {
			Debugger.getDebugAction(action).doAction(player);
			return;
		}

		switch (action) {
			case "on":
			case "true":
				playerWrapper.setInDebugMode(true);
				Players.sendMessage(player, Messages.playerDebugModeChange(playerWrapper));
				break;
			case "off":
			case "false":
				playerWrapper.setInDebugMode(false);
				Players.sendMessage(player, Messages.playerDebugModeChange(playerWrapper));
				break;
			case "list":
			case "?":
				int page = 1;
				if (args.length > 1) {
					if (!StringUtils.isNumeric(args[1])) {
						Players.sendMessage(player, Messages.invalidCommandUsage("list", "[Page Number]"));
						return;
					}
					page = Integer.parseInt(args[1]);
				}
				Debugger.getDebugMenu().sendTo(player, page, 6);
				break;
			default:
				break;
		}

	}
}
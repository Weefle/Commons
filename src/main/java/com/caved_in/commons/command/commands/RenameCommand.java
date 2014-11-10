package com.caved_in.commons.command.commands;

import com.caved_in.commons.command.Arg;
import com.caved_in.commons.command.Command;
import com.caved_in.commons.command.Wildcard;
import com.caved_in.commons.item.Items;
import com.caved_in.commons.player.Players;
import org.bukkit.entity.Player;

public class RenameCommand {
	@Command(identifier = "rename", permissions = "commons.command.rename")
	public void onRenameCommand(Player player, @Wildcard @Arg(name = "name") String itemName) {
		if (!Players.hasItemInHand(player)) {
			Players.sendMessage(player, "&eYou need an item in your hand.");
			return;
		}

		Items.setName(player.getItemInHand(), itemName);
		Players.sendMessage(player, String.format("&aItem Re-Named to %s", itemName));
	}
}
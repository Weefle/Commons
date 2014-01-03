package com.caved_in.commons.commands.utility;

import com.caved_in.commons.Commons;
import com.caved_in.commons.Messages;
import com.caved_in.commons.commands.CommandController.CommandHandler;
import com.caved_in.commons.data.disguises.Disguise;
import com.caved_in.commons.data.menu.HelpScreen;
import com.caved_in.commons.entity.EntityUtility;
import com.caved_in.commons.items.ItemHandler;
import com.caved_in.commons.location.LocationHandler;
import com.caved_in.commons.player.PlayerHandler;
import com.caved_in.commons.utilities.StringUtil;
import com.caved_in.commons.world.WorldHandler;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class UtilityCommands {

	@CommandHandler(name = "gm", usage = "/gm <0/1/survival/creative> to switch gamemodes", permission = "tunnels.common.gamemode")
	public void GamemodeHandler(Player player, String[] commandArgs) {
		if (commandArgs.length >= 1 && commandArgs[0] != null) {
			String modeArgument = commandArgs[0];
			switch (modeArgument.toLowerCase()) {
				case "0":
				case "s":
				case "survival":
					player.setGameMode(GameMode.SURVIVAL);
					sendGameModeMessage(player);
					break;
				case "1":
				case "creative":
				case "c":
					player.setGameMode(GameMode.CREATIVE);
					sendGameModeMessage(player);
					break;
				case "a":
				case "adventure":
				case "2":
					player.setGameMode(GameMode.ADVENTURE);
					sendGameModeMessage(player);
					break;
				default:
					player.sendMessage("Please enter a valid gamemode; Acceptable inputs are: 0/1/2/survival/creative/adventure/c/s/a");
					break;
			}

		} else {
			switch (player.getGameMode()) {
				case SURVIVAL:
					player.setGameMode(GameMode.CREATIVE);
					break;
				case CREATIVE:
					player.setGameMode(GameMode.ADVENTURE);
					break;
				case ADVENTURE:
					player.setGameMode(GameMode.SURVIVAL);
					break;
				default:
					player.setGameMode(GameMode.SURVIVAL);
					break;
			}
			sendGameModeMessage(player);
		}
	}

	@CommandHandler(name = "xp", usage = "/xp")
	public void playerXPCommand(Player player, String[] args) {
		player.sendMessage(StringUtil.formatColorCodes("&aYou have " + ((int) PlayerHandler.getData(player.getName()).getCurrency()) + " Tunnels XP"));
	}

	private static HelpScreen getNickHelpScreen() {
		HelpScreen nicknameHelpsScreen = new HelpScreen("Nickname Command Help");
		nicknameHelpsScreen.setHeader(ChatColor.YELLOW + "<name> Page <page> of <maxpage>");
		nicknameHelpsScreen.setFormat("<name> -- <desc>");
		nicknameHelpsScreen.setFlipColor(ChatColor.GREEN, ChatColor.DARK_GREEN);

		nicknameHelpsScreen.setEntry("/nick help", "Shows the help menu");
		nicknameHelpsScreen.setEntry("/nick off [player]", "Turns the nickname off for yourself, or another player");
		nicknameHelpsScreen.setEntry("/nick <Name>", "Disguise yourself as another player");
		nicknameHelpsScreen.setEntry("/nick <player> <Name>", "Disguise another player");
		return nicknameHelpsScreen;
	}

	@CommandHandler(name = "nicklist", permission = "tunnels.common.nicklist")
	public void getNickListCommand(Player player, String[] commandArgs) {
		HelpScreen HelpScreen = new HelpScreen("Currently Disguised Players");
		HelpScreen.setHeader(ChatColor.YELLOW + "<name> Page <page> of <maxpage>");
		HelpScreen.setFormat("<name> is disguised as <desc>");
		HelpScreen.setFlipColor(ChatColor.GREEN, ChatColor.DARK_GREEN);
		for (Disguise disguisedPlayer : Commons.disguiseDatabase.getDisguises()) {
			HelpScreen.setEntry(disguisedPlayer.getPlayerDisguised(), disguisedPlayer.getDisguisedAs() + " on the server '" + disguisedPlayer.getServerOn() + "'");
		}

		if (commandArgs.length > 0) {
			int Page = Integer.parseInt(commandArgs[0]);
			HelpScreen.sendTo(player, Page, 7);
		} else {
			HelpScreen.sendTo(player, 1, 7);
		}
	}

	public void sendGameModeMessage(Player Player) {
		Player.sendMessage(ChatColor.GREEN + "You've switched your gamemode to " + WordUtils.capitalize(Player.getGameMode().name().toLowerCase()));
	}

	@CommandHandler(name = "skull", usage = "/skull <Name>", permission = "tunnels.common.skull")
	public void getPlayerSkullCommand(Player player, String[] commandArgs) {
		if (commandArgs.length > 0 && !commandArgs[0].isEmpty()) {
			String playerName = commandArgs[0];
			ItemStack playerSkull = ItemHandler.getSkull(playerName);
			ItemHandler.setItemName(playerSkull, playerName + "'s Head");
			player.getInventory().addItem(playerSkull);
		} else {
			PlayerHandler.sendMessage(player, Messages.INVALID_COMMAND_USAGE("player"));
		}
	}

	@CommandHandler(name = "setspawn", usage = "/setspawn", permission = "tunnels.common.setspawn")
	public void setSpawnCommand(Player player, String[] commandArgs) {
		if (WorldHandler.setSpawn(player.getWorld(), player.getLocation())) {
			PlayerHandler.sendMessage(player, "&aSpawn location for the world &7" + player.getWorld().getName() + "&a has been set!");
		} else {
			PlayerHandler.sendMessage(player,"&eThere was an error changing the spawn location for world &7" + player.getWorld().getName() + "&e; please check the console.");
		}
	}

	@CommandHandler(name = "more", usage = "/more", permission = "tunnels.common.more")
	public void onMoreCommand(Player player, String[] commandArgs) {
		ItemStack playerHandItem = player.getItemInHand();
		playerHandItem.setAmount(playerHandItem.getMaxStackSize());
		player.setItemInHand(playerHandItem);
	}

	@CommandHandler(name = "heal", usage = "/heal", permission = "tunnels.common.heal")
	public void onHealCommand(Player player, String[] commandArgs) {
		PlayerHandler.removePotionEffects(player);
		EntityUtility.setCurrentHealth(player,EntityUtility.getMaxHealth(player));
		player.sendMessage("&eYou've been healed!");
	}

	@CommandHandler(name = "ci", usage = "/ci [player]", permission = "tunnels.common.clearinventory", aliases = {"clearinventory", "clearinv"})
	public void onClearInventoryCommand(CommandSender commandSender, String[] args) {
		//Check if we've got a player using this command
		if (args.length > 0) {
			String playerName = args[1];
			//Check if there's a player online with the name in our argument
			if (PlayerHandler.isOnlineFuzzy(playerName)) {
				//Get the player and clear their inventory + armor
				Player player = PlayerHandler.getPlayer(playerName);
				PlayerHandler.clearInventory(player,true);
				player.sendMessage(Messages.INVENTORY_CLEARED);
			} else {
				PlayerHandler.sendMessage(commandSender, Messages.PLAYER_OFFLINE(playerName));
			}
		} else {
			if (commandSender instanceof Player) {
				Player player = (Player)commandSender;
				PlayerHandler.clearInventory(player);
				PlayerHandler.sendMessage(player,Messages.INVENTORY_CLEARED);
			} else {
				PlayerHandler.sendMessage(commandSender, Messages.INVALID_COMMAND_USAGE("player"));
			}
		}
	}

	@CommandHandler(name = "tphere", permission = "tunnels.common.tphere", aliases = {"bring"})
	public void onTeleportHereCommand(Player player, String[] args) {
		if (args.length > 0) {
			String playerName = args[0];
			if (PlayerHandler.isOnlineFuzzy(playerName)) {
				Player playerToTeleport = PlayerHandler.getPlayer(playerName);
				playerToTeleport.teleport(player, PlayerTeleportEvent.TeleportCause.COMMAND);
			} else {
				PlayerHandler.sendMessage(player,Messages.PLAYER_OFFLINE(playerName));
			}
		} else {
			PlayerHandler.sendMessage(player,Messages.INVALID_COMMAND_USAGE("player"));
		}
	}

	//TODO Make this command able to teleport one player to another via arguments; CommandSender instead of player
	@CommandHandler(name = "tp", permission = "tunnels.common.teleport", aliases = {"teleport"})
	public void onTeleportCommand(Player player, String[] args) {
		if (args.length > 0) {
			String playerName = args[0];
			if (PlayerHandler.isOnlineFuzzy(playerName)) {
				Player playerTeleportingTo = PlayerHandler.getPlayer(playerName);
				player.teleport(playerTeleportingTo, PlayerTeleportEvent.TeleportCause.COMMAND);
			} else {
				PlayerHandler.sendMessage(player,Messages.PLAYER_OFFLINE(playerName));
			}
		} else {
			PlayerHandler.sendMessage(player,Messages.INVALID_COMMAND_USAGE("player"));
		}
	}

}
package me.peb.npcst.commands;

import me.peb.npcst.NPCST;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright 2019-2020 (c) Exodius Studios. All Rights Reserved.
 *
 * @author Peb
 */
public class NPCCommand implements CommandExecutor {

	private final NPCST plugin;

	public NPCCommand(NPCST npcs) {
		plugin = npcs;
		npcs.getCommand("npc").setExecutor(this);
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("You need to specify a npc name!");
			return true;
		}
		if (! (sender instanceof Player)) return true;
		String name = args[0];
		sender.sendMessage("Creating npc " + name);
		plugin.getManager().createNPC(plugin.getPosition(), (Player)sender, name);

		return true;
	}
}

package me.peb.npcst;

import me.peb.npcst.commands.NPCCommand;
import me.peb.npcst.nms.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright 2019-2020 (c) Exodius Studios. All Rights Reserved.
 *
 * @author Peb
 */
public class NPCST extends JavaPlugin {

	private NMS nms;

	public void onEnable() {

		getConfig().options().copyDefaults(true);
		saveDefaultConfig();

		System.out.println("THIS IS NOT A FINAL PLUGIN, IT HAS BEEN BUILT PURELY FOR ST APPLICATION");

		nms = getNMSBridge();
		if (nms == null) {
			this.getPluginLoader().disablePlugin(this);
			return;
		}

		new NPCCommand(this);

	}

	public NMS getManager() {
		return nms;
	}

	public Location getPosition() {
		return getManager().serializeLocation(getConfig().getString("position"));
	}

	public NMS getNMSBridge() {

		String version = Bukkit.getServer().getClass().getPackage().getName();
		String nmsVersion = version.substring(version.lastIndexOf('.') + 1);

		System.out.println("Using " + nmsVersion);

		switch (nmsVersion) {
			case "v1_8_R3":
				return new V1_8(this);
			case "v1_9_R2":
				return new V1_9(this);
			case "v1_10_R1":
				return new V1_10(this);
			case "v1_11_R1":
				return new V1_11(this);
			case "v1_12_R1":
				return new V1_12(this);
			case "v1_13_R2":
				return new V1_13(this);
			case "v1_14_R1":
				return new V1_14(this);
			case "v1_15_R1":
				return new V1_15(this);
			case "v1_16_R1":
				return new V1_16(this);
			default:
				System.out.println("Version not supported");
				return null;
		}

	}

}

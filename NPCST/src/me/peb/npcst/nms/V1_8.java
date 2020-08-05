package me.peb.npcst.nms;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.peb.npcst.NPCST;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.AbstractMap;
import java.util.UUID;

/**
 * Copyright 2019-2020 (c) Exodius Studios. All Rights Reserved.
 *
 * @author Peb
 */
public class V1_8 extends NMS {

	public V1_8(NPCST plugin) {
		super(plugin);
	}

	@Override
	public void createNPC(Location location, Player player, String skin) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), skin);
		AbstractMap.SimpleEntry<String, String> profileSkin = findSkin(skin);
		if (profileSkin == null) {
			player.sendMessage("Incorrect name");
			return;
		}

		player.teleport(location);

		profile.getProperties().put("textures", new Property("textures", profileSkin.getKey(), profileSkin.getValue()));
		EntityPlayer npc = new EntityPlayer(MinecraftServer.getServer(), ((CraftWorld) location.getWorld()).getHandle(), profile, new PlayerInteractManager(((CraftWorld) location.getWorld()).getHandle()));

		npc.setLocation(location.getX(), location.getY(), location.getZ(),
			location.getYaw(), location.getPitch());

		sendPackets(npc.getBukkitEntity().getPlayer());

		npc.getBukkitEntity().setFlying(false);



		new BukkitRunnable() {
			int counter = 0;

			@Override
			public void run() {
				switch(counter) {
					case 0:
					case 1:
						animate(npc.getBukkitEntity().getPlayer(), 4);
						break;
					case 2:
					case 4:
						animate(npc.getBukkitEntity().getPlayer(), 1);
						break;
					case 3:
						animate(npc.getBukkitEntity().getPlayer(), 2);
						break;
					case 6:
						animate(npc.getBukkitEntity().getPlayer(), 2);
						counter = -1;
						break;
					case 5:
						animate(npc.getBukkitEntity().getPlayer(), 3);
						break;
				}
				counter++;
			}
		}.runTaskTimer(plugin, 0, 20);
	}

	@Override
	public void sendPackets(Player npc) {
		EntityPlayer entity = ((CraftPlayer) npc).getHandle();
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entity));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entity));
			connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entity.getId(), (byte) ((entity.yaw * 256 / 360)), (byte) ((entity.pitch * 256 / 360)), false));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(entity, (byte) (entity.yaw * 256 / 360)));
		}
	}

	@Override
	public void animate(Player player, int state) {
		EntityPlayer p = ((CraftPlayer) player).getHandle();
		switch(state) {
			case 1:
				p.getBukkitEntity().setSneaking(true);
				break;
			case 2:
				p.getBukkitEntity().setSneaking(false);
				break;
			case 3:
				sendPacket(new PacketPlayOutAnimation(p, 0));
				break;
			case 4:
				sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(p.getId(), (byte)0, (byte)20, (byte)0, true));
				new BukkitRunnable() {
					@Override
					public void run() {
						sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(p.getId(), (byte)0, (byte)-20, (byte)0, true));
					}
				}.runTaskLater(plugin, 4);
				break;
		}
		sendPacket(new PacketPlayOutEntityMetadata(p.getId(), p.getDataWatcher(), true));
	}

	private void sendPacket(final Packet p) {

		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(p);
		}

	}
}

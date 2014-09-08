package org.mlp.plugins.wgitemsrestrict.listeners;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.mlp.plugins.wgitemsrestrict.WGItemsRestrict;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ItemsRestrictListener implements Listener {
	private WGItemsRestrict myPlugin;
	public ItemsRestrictListener(WGItemsRestrict plugin) {
		super();
		myPlugin = plugin;
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		WorldGuardPlugin worldGuard = myPlugin.getWorldGuard();
		
		if (myPlugin.ignoreGroups != null) {
	        for (String group : worldGuard.getGroups(player)) {
	            if (myPlugin.ignoreGroups.contains(group.toLowerCase())) {
	                return;
	            }
	        }
		}
		
		ItemStack itemStack;
		if ( (itemStack = player.getItemInHand()) == null) {
			return;
		}
		int id = itemStack.getTypeId();
		MaterialData mdata = itemStack.getData();
		String itemID = String.valueOf(id);
		String fullItemID = itemID;
		if (mdata != null && mdata.getData() != 0) {
			fullItemID = itemID + ":" + String.valueOf(mdata.getData());
		}
		
		Block block = event.getClickedBlock();
		boolean disabled = false;
		for (String disabledItem : myPlugin.disabledItems) {
			String[] disabledPar = disabledItem.split("\\s*:\\s*", 2);
			if (disabledItem.equals(fullItemID)) {
				disabled = true;
				break;
			}
			if (disabledPar.length == 2) {
				if (disabledPar[1].equalsIgnoreCase("all") && disabledPar[0].equals(itemID)) {
					disabled = true;
					break;
				}
			}
		}
		
		if (disabled) {
			boolean canBuild = worldGuard.canBuild(player, player.getLocation().getBlock().getRelative(0, -1, 0));
			boolean canBuild2 = true;
			if (block != null) {
				canBuild2 = worldGuard.canBuild(player, block.getLocation());
			}
			
			if (!canBuild || !canBuild2) {
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You are not permitted to use #" + fullItemID);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				event.setCancelled(true);
				return;
			}
		}
	}
}

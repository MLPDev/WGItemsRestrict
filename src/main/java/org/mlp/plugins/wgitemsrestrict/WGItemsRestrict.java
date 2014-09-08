package org.mlp.plugins.wgitemsrestrict;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mlp.plugins.wgitemsrestrict.listeners.ItemsRestrictListener;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public final class WGItemsRestrict extends JavaPlugin {
	public List<String> disabledItems;
	public List<String> ignoreGroups;
	@Override
	public void onEnable() {
		Logger logger = getLogger();
		logger.info("WGItemsRestrict has been enabled");
		
		initializeConfig(true);
		loadData();
			
		getServer().getPluginManager().registerEvents(new ItemsRestrictListener(this), this);
	}
	
	public WorldGuardPlugin getWorldGuard() {	
		return WGBukkit.getPlugin();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("wgitems")) {
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				reloadConfig();
				loadData();
				if (!(sender instanceof Player)) {
					getLogger().info("Config reloaded");
				} else {
					Player player = (Player)sender;
					player.sendMessage("Config was reloaded");
				}
				return true;
			}
		}
		return false; 
	}
	
	private void initializeConfig(boolean copyDefaults) {
		FileConfiguration config = getConfig();
		
		config.addDefault("disabled-items", "259,327");
		config.addDefault("ignore-groups", "admin");
		
		config.options().copyDefaults(copyDefaults);
		saveConfig();
	}
	private void loadData() {
		FileConfiguration config = getConfig();
		String pattern = "\\s*,\\s*";

		String strDisabled = config.getString("disabled-items");
		disabledItems = Arrays.asList(strDisabled.split(pattern));
		
		String strIgnore = config.getString("ignore-groups");
		ignoreGroups = Arrays.asList(strIgnore.split(pattern));
		getLogger().info(strDisabled);
	}
}

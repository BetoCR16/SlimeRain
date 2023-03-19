package com.edadimperial.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SlimeRain implements CommandExecutor {
    Plugin instance;
    public SlimeRain(Plugin plugin){
        instance = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = instance.getConfig();

        int duration = config.getInt("duration"); // Duration in seconds
        int interval = config.getInt("frequency"); // Interval in seconds

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.start")));
        List<Player> players = new ArrayList<Player>(Bukkit.getServer().getOnlinePlayers());
        World world = instance.getServer().getWorld("world");

        new BukkitRunnable() {
            int count = 0;
            public void run() {
                if (world != null) {
                    world.setStorm(true);
                }

                count++;
                if (count >= duration / interval) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.stop")));
                    world.setStorm(false);
                    cancel();
                }

                for (Player player : players) {
                    Location loc = player.getLocation();
                    World world = loc.getWorld();

                    int numPlayers = 0;
                    for (Player otherPlayer : players) {
                        if (otherPlayer.getLocation().distance(loc) < 10) {
                            numPlayers++;
                        }
                    }

                    // Spawn a group of slime mobs based on the number of players in the area
                    int numSlimes = Math.min(numPlayers * 2, 50); // Limit the number of slimes to 50
                    for (int i = 0; i < numSlimes; i++) {
                        int x = (int) (loc.getX() + (Math.random() * 20) - 10);
                        int y = (int) (loc.getY() + 100);
                        int z = (int) (loc.getZ() + (Math.random() * 20) - 10);
                        Location spawnLoc = new Location(world, x, y, z);
                        Entity slime = world.spawnEntity(spawnLoc, EntityType.SLIME);
                        slime.setPersistent(true);
                        slime.setCustomName("Slime");


                        // Schedule the entity to be removed after a certain amount of time
                        Bukkit.getScheduler().runTaskLater(instance, new Runnable() {
                            public void run() {
                                slime.remove();
                            }
                        }, 20 * 30); // Remove the entity after 30 seconds
                    }
                }
            }
        }.runTaskTimer(instance, 0,interval * 20);

        return true;

    }
}

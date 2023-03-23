package com.edadimperial.commands;

import org.bukkit.*;
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
import java.util.Collections;
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


        List<Player> players = new ArrayList<Player>(Bukkit.getServer().getOnlinePlayers());
        for (Player player : players) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.start")));
        }

        World world = instance.getServer().getWorld("world");
        World worldCon = instance.getServer().getWorld("imperial_build");

        new BukkitRunnable() {
            int count = 0;
            public void run() {
                List<Player> playerList = new ArrayList<Player>(Bukkit.getServer().getOnlinePlayers());
                System.out.println(playerList.size());
                if (world != null && worldCon != null) {
                    world.setStorm(true);
                    worldCon.setStorm(true);
                }

                count++;
                if (count >= duration / interval) {
                    for (Player player : playerList) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("messages.stop")));
                    }
                    if (world != null && worldCon != null) {
                        world.setStorm(false);
                        worldCon.setStorm(false);
                    }
                    cancel();
                }

                Collections.shuffle(playerList);

                int numPlayers = playerList.size();


                for (int i = 0; i <= numPlayers; i++){
                    System.out.println(i);
                    Location loc = playerList.get(i).getLocation();
                    World pWorld = playerList.get(i).getWorld();

                    // Spawn a group of slime mobs based on the number of players in the area
                    int numSlimes = Math.min(numPlayers * 2, 50); // Limit the number of slimes to 50
                    for (int j = 0; j < numSlimes; j++) {
                        if (pWorld.getName().equals("world") || pWorld.getName().equals("imperial_build")) {
                            int x = (int) (loc.getX() + (Math.random() * 20));
                            int y = (int) (loc.getY() + 100);
                            int z = (int) (loc.getZ() + (Math.random() * 20));
                            Location spawnLoc = new Location(pWorld, x, y, z);
                            Entity slime = pWorld.spawnEntity(spawnLoc, EntityType.SLIME);
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


            }
        }.runTaskTimer(instance, 0,interval * 20);

        return true;

    }
}

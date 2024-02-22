package org.info_0.advanceddeathmessages.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.info_0.advanceddeathmessages.AdvancedDeathMessages;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    private final AdvancedDeathMessages plugin;

    public PlayerJoinListener(AdvancedDeathMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) throws SQLException {
        if(plugin.getDatabase().playerExists(event.getPlayer())) return;
        plugin.getDatabase().addPlayer(event.getPlayer());
        Bukkit.getLogger().info(event.getPlayer().getName() + " successfully added AdvancedDeathMessage Database.");
    }

}

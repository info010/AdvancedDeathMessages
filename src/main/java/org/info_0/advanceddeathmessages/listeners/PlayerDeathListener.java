package org.info_0.advanceddeathmessages.listeners;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.info_0.advanceddeathmessages.AdvancedDeathMessages;
import org.info_0.advanceddeathmessages.chat.HoverTransforms;
import org.info_0.advanceddeathmessages.util.Database;
import org.info_0.advanceddeathmessages.util.MessagesUtil;

import java.sql.SQLException;

public class PlayerDeathListener implements Listener {

    public PlayerDeathListener(Database database){
        this.database = database;
    }

    private final Database database;
    private final String allowDeathMessages = "adm.allow-messages";

    @EventHandler
    public void deathPlayer(PlayerDeathEvent event) throws SQLException {
        if(!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        Player victim = event.getEntity();
        if(victim.getKiller() == null) return;
        if(!victim.getKiller().getType().equals(EntityType.PLAYER)) return;
        Player killer = victim.getKiller();
        if(killer == victim) return;
        String messPath = database.getDeathMessage(killer);
        String message = MessagesUtil.getMessage(messPath);
        if(killer.getInventory().getItemInMainHand().getType() == null || killer.getInventory().getItemInMainHand().getType().isAir())
            sendNormalMessage(message,victim,killer);
        else
            sendComponentMessage(message,victim,killer,killer.getInventory().getItemInMainHand());
    }

    private void sendComponentMessage(String message, Player victim, Player killer, ItemStack item){
        HoverTransforms transforms = new HoverTransforms(message,item);
        BaseComponent[] components = MessagesUtil.refactorMessage(message,victim,killer,transforms);
        for(Player player : Bukkit.getOnlinePlayers()){
            if(!player.hasPermission(allowDeathMessages)) continue;
            player.spigot().sendMessage(components);
        }
    }

    private void sendNormalMessage(String message, Player victim, Player killer){
        if(message.contains("%weapon%")) message = message.replaceAll("%weapon%","§eYUMRUK");
        String string = MessagesUtil.refactorMessage(message,victim,killer);
        for(Player player : Bukkit.getOnlinePlayers()){
            if(!player.hasPermission(allowDeathMessages)) continue;
            player.sendMessage(string);
        }
    }
}

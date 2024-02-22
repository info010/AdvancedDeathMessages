package org.info_0.advanceddeathmessages;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.info_0.advanceddeathmessages.commands.MainCommand;
import org.info_0.advanceddeathmessages.listeners.PlayerDeathListener;
import org.info_0.advanceddeathmessages.listeners.PlayerJoinListener;
import org.info_0.advanceddeathmessages.util.Database;
import org.info_0.advanceddeathmessages.util.MessagesUtil;

import java.sql.SQLException;

public final class AdvancedDeathMessages extends JavaPlugin {

    private static AdvancedDeathMessages instance;
    private Database database;
    private static Permission perms;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        MessagesUtil.loadMessages();
        if(setupPermissions())
            Bukkit.getLogger().info("Enable permissions.");
        else {
            Bukkit.getLogger().severe("Failed to connect to the permissions.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        try {
            if(!getDataFolder().exists()) getDataFolder().mkdirs();
            database = new Database(getDataFolder().getAbsolutePath() + "/settings.db");
            Bukkit.getLogger().info("Database successfully loaded.");
        }catch (SQLException e){
            e.printStackTrace();
            Bukkit.getLogger().severe("Failed to connect to the database!" + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        registerEvents();
        registerCommands();
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
            Bukkit.getLogger().info("Database successfully closed.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Database getDatabase(){
        return AdvancedDeathMessages.getInstance().database;
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(database),this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this),this);
    }

    private void registerCommands(){
        getCommand("adm").setExecutor(new MainCommand(database));
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Permission getPermissionApi(){
        return perms;
    }

    public static AdvancedDeathMessages getInstance(){
        return instance;
    }

}

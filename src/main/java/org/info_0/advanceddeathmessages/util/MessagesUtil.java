//package org.info_0.advanceddeathmessages.util;
//
//import net.md_5.bungee.api.ChatColor;
//import net.md_5.bungee.api.chat.BaseComponent;
//import org.bukkit.Bukkit;
//import org.bukkit.configuration.file.FileConfiguration;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.entity.Player;
//import org.info_0.advanceddeathmessages.AdvancedDeathMessages;
//import org.info_0.advanceddeathmessages.chat.HoverTransforms;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.util.*;
//
//public class MessagesUtil {
//
//    private final static Map<String,String> messages = new HashMap<>();
//    private static File messageFile;
//
//    public static String getMessage(String messName){
//        return messages.get(messName);
//    }
//
//    public static void setMessage(String path, String message) {
//        try {
//            FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(messageFile);
//            yamlConfig.set(path, String.format("%s",message));
//            yamlConfig.save(messageFile);
//            String newMessage = ChatColor.translateAlternateColorCodes('&', yamlConfig.getString(path));
//            messages.put(path, newMessage);
//            Bukkit.getLogger().info("Yeni ölüm mesajı başarıyla eklendi.");
//        } catch (IOException e) {
//            Bukkit.getLogger().severe("Yeni ölüm mesajı eklenirken bir hata oluştu.");
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static List<String> getPaths(){
//        return new ArrayList<>(messages.keySet());
//    }
//
//    public static void loadMessages(){
//        messageFile = new File(AdvancedDeathMessages.getInstance().getDataFolder(),"messages.yml");
//        if(!messageFile.exists()){
//            try {
//                InputStream in = AdvancedDeathMessages.getInstance().getResource("messages.yml");
//                Files.copy(in, messageFile.toPath());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        FileConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(messageFile);
//        for(String messName: yamlConfiguration.getKeys(false)){
//            String message = ChatColor.translateAlternateColorCodes('&',yamlConfiguration.getString(messName));
//            Bukkit.getLogger().info(String.format("[%s] başarıyla kaydedildi.",messName));
//            messages.put(messName, message);
//        }
//
//        Bukkit.getLogger().info(messageFile.getName() + " loaded!");
//    }
//
//    public static String refactorMessage(String string, Player victim, Player killer){
//        if(string.contains("%victim%")) string = string.replaceAll("%victim%",victim.getName());
//        if(string.contains("%killer%")) string = string.replaceAll("%killer%",killer.getName());
//        return string;
//    }
//
//    public static BaseComponent[] refactorMessage(String string, Player victim, Player killer, HoverTransforms transforms){
//        string = refactorMessage(string,victim,killer);
//        return transforms.transform(string);
//    }
//
//    public static void debugMessage(String string){
//        if(!AdvancedDeathMessages.getInstance().getConfig().getBoolean("debug-logger")) return;
//        Bukkit.getLogger().info(string);
//    }
//
//}

package org.info_0.advanceddeathmessages.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.info_0.advanceddeathmessages.AdvancedDeathMessages;
import org.info_0.advanceddeathmessages.chat.HoverTransforms;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class MessagesUtil {

    private static final Map<String, String> messages = new HashMap<>();
    private static File messageFile;

    public static String getMessage(String messName) {
        return messages.get(messName);
    }

    public static void setMessage(String path, String message) {
        try {
            FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(messageFile);
            yamlConfig.set(path, message);
            yamlConfig.save(messageFile);
            String newMessage = ChatColor.translateAlternateColorCodes('&', message);
            messages.put(path, newMessage);
            Bukkit.getLogger().info("New death message successfully added.");
        } catch (IOException e) {
            Bukkit.getLogger().severe("An error occurred while adding a new death message.");
            e.printStackTrace();
        }
    }

    public static List<String> getPaths() {
        return new ArrayList<>(messages.keySet());
    }

    public static synchronized void loadMessages() {
        messageFile = new File(AdvancedDeathMessages.getInstance().getDataFolder(), "messages.yml");
        if (!messageFile.exists()) {
            try {
                InputStream in = AdvancedDeathMessages.getInstance().getResource("messages.yml");
                if (in != null) {
                    Files.copy(in, messageFile.toPath());
                    in.close();
                } else {
                    Bukkit.getLogger().severe("Unable to find default messages file.");
                }
            } catch (IOException e) {
                Bukkit.getLogger().severe("An error occurred while copying default messages file.");
                e.printStackTrace();
            }
        }

        FileConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(messageFile);
        for(String key: yamlConfiguration.getKeys(false)){
            for(String messName: yamlConfiguration.getConfigurationSection(key).getKeys(false)){
                String message = ChatColor.translateAlternateColorCodes('&',yamlConfiguration.getString(key+'.'+messName));
                Bukkit.getLogger().info(String.format("[%s] successfully load.",messName));
                messages.put(messName, message);
            }
        }

        Bukkit.getLogger().info(messageFile.getName() + " loaded!");
    }

    public static String refactorMessage(String string, Player victim, Player killer) {
        if (string != null) {
            if (string.contains("%victim%")) {
                string = string.replaceAll("%victim%", victim != null ? victim.getName() : "null");
            }
            if (string.contains("%killer%")) {
                string = string.replaceAll("%killer%", killer != null ? killer.getName() : "null");
            }
        }
        return string;
    }

    public static BaseComponent[] refactorMessage(String string, Player victim, Player killer, HoverTransforms transforms) {
        string = refactorMessage(string, victim, killer);
        return transforms.transform(string);
    }

    public static void debugMessage(String string) {
        if (AdvancedDeathMessages.getInstance().getConfig().getBoolean("debug-logger")) {
            Bukkit.getLogger().info(string);
        }
    }
}

package org.info_0.advanceddeathmessages.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.info_0.advanceddeathmessages.AdvancedDeathMessages;
import org.info_0.advanceddeathmessages.chat.HoverTransforms;
import org.info_0.advanceddeathmessages.util.Database;
import org.info_0.advanceddeathmessages.util.MessagesUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    public MainCommand(Database database){
        this.database = database;
    }

    private final Database database;

    private final Permission permApi = AdvancedDeathMessages.getPermissionApi();

    private final List<String> paths = MessagesUtil.getPaths();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length < 1) return true;
        if(strings[0].equalsIgnoreCase("reload")){
            if(commandSender.isOp()) reload();
            return true;
        } else if (strings[0].equalsIgnoreCase("set")) {
            if (commandSender.isOp()) {
                if (strings.length < 2 || strings[1].isEmpty()) return true;
                List<String> txt = Arrays.asList(strings).subList(1, strings.length - 1);
                String text = String.join(" ", txt);
                MessagesUtil.setMessage(strings[1], text);
            }
        } else if (strings[0].equalsIgnoreCase("user")) {
            if(commandSender.isOp()){
                if(strings.length < 3
                        || Bukkit.getPlayer(strings[1]) == null
                        || strings[1].isEmpty() || strings[2].isEmpty()) return true;
                Player user = Bukkit.getPlayer(strings[1]);
                String path = strings[2];
                try {
                    database.updatePlayerDeathMessage(user, path);
                    Bukkit.getLogger().info(String.format("%s adlı oyuncunun ölüm mesaj duyurusu artık '%s' olarak ayarlandı.",user.getName(),path));
                } catch (SQLException e) {
                    Bukkit.getLogger().warning("İşlem yapılırken bir hata ile karşılaşıldı.");
                    throw new RuntimeException(e);
                }
                return true;
            }

        }
        if(!(commandSender instanceof Player)) return true;
        Player player = (Player) commandSender;
        if(strings[0].equalsIgnoreCase("toggle")){
            String allowDeathMessages = "adm.allow-messages";
            if(!player.hasPermission(allowDeathMessages)) {
                player.sendMessage("§6§l[ADM] > §eÖlüm mesajları açık.");
                permApi.playerAdd(player,allowDeathMessages);
            } else {
                player.sendMessage("§6§l[ADM] > §eÖlüm mesajları kapalı.");
                permApi.playerRemove(player,allowDeathMessages);
            }
            return true;
        } else if (strings[0].equalsIgnoreCase("test")) {
            if (strings.length < 2 || strings[1] == null || strings[1].isEmpty()) return true;
            sendTestMessage(strings[1],player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> suggestions = new ArrayList<>();

        if (strings.length == 1) {
            if (commandSender.isOp()) {
                suggestions.addAll(Arrays.asList("reload", "toggle", "set", "test","user"));
            } else {
                suggestions.addAll(Arrays.asList("toggle", "test"));
            }
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("set")) {
                if (commandSender.isOp()) {
                    suggestions.addAll(paths);
                }
            } else if (strings[0].equalsIgnoreCase("test")) {
                suggestions.addAll(paths);
            } else if(strings[0].equalsIgnoreCase("user")) {
                for (Player player : Bukkit.getOnlinePlayers()) suggestions.add(player.getName());
            }
        } else if (strings.length == 3) {
            if(strings[0].equalsIgnoreCase("user")) {
                suggestions.addAll(paths);
            }
        }

        return suggestions;
    }

    private void sendTestMessage(String path,Player player){
        if(MessagesUtil.getMessage(path).isEmpty() || MessagesUtil.getMessage(path) == null) {
            player.sendMessage("Test etmek istediğin mesaj bu sunucuda bulunmuyor.");
            return;
        }
        String message = MessagesUtil.getMessage(path);
        if(message.contains("%weapon%")) {
            if(player.getInventory().getItemInMainHand().getType() != null && !player.getInventory().getItemInMainHand().getType().isAir()) {
                message = MessagesUtil.refactorMessage(message.replaceAll("%weapon%", "§eYUMRUK"), player, player);
                player.sendMessage(message);
            }else{
                HoverTransforms transforms = new HoverTransforms(message,player.getInventory().getItemInMainHand());
                BaseComponent[] components = MessagesUtil.refactorMessage(message,player,player,transforms);
                player.spigot().sendMessage(components);
            }
        }else{
            message = MessagesUtil.refactorMessage(message,player,player);
            player.sendMessage(message);
        }

        player.sendMessage("Bu mesajı sadece sen görebilirsin.");
    }

    private void reload(){
        Plugin plugin = AdvancedDeathMessages.getInstance();
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getPluginManager().enablePlugin(plugin);
    }
}

package me.adelemphii.entitytoss.commands;

import me.adelemphii.entitytoss.EntityToss;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ReloadCommand implements CommandExecutor {

    EntityToss plugin;
    public ReloadCommand(EntityToss plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(!sender.hasPermission("playertoss.reload")) {
            if(plugin.getConfig().getString("reload-permission-message") == null) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        placeholders(Objects.requireNonNull(plugin.getConfig().getString("reload-permission-message")), sender.getName())));
            }
            return false;
        }

        if(cmd.getName().equalsIgnoreCase("tossreload")) {

            if(plugin.getConfig().getString("reload-message") == null) {
                sender.sendMessage(ChatColor.RED + "[EntityToss]: " + ChatColor.GREEN + "Config Reloaded!");
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        placeholders(Objects.requireNonNull(plugin.getConfig().getString("reload-message")), sender.getName())));
            }
            plugin.reloadConfig();
        }

        return false;
    }

    public String placeholders(String msg, String name) {
        return msg.replace("%player%", name);
    }
}

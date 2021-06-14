package me.adelemphii.entitytoss.events;

import me.adelemphii.entitytoss.EntityToss;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public class PickUpEvent implements Listener {

    EntityToss plugin;
    public PickUpEvent(EntityToss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent event) {

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            return;
        }

        if(event.getRightClicked().hasMetadata("NPC")) {
            return;
        }

        if (!(event.getRightClicked() instanceof Player)) {
            Player player = event.getPlayer();
            Entity target = event.getRightClicked();

            player.addPassenger(target);
            plugin.setPlayerToss(target.getUniqueId(), true);

            if(plugin.getConfig().getString("player-tossing-message") == null) {
                player.sendMessage("You are now tossing " + target.getName());
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossing-message")), player.getName(), target.getName())));
            }
            return;
        }

        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();

        player.addPassenger(target);

        if(plugin.getConfig().getString("player-tossing-message") == null) {
            player.sendMessage("You are now tossing " + target.getName());
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossing-message")), player.getName(), target.getDisplayName())));
        }

        if(plugin.getConfig().getString("target-tossing-message") == null) {
            target.sendMessage("You are being tossed by " + player.getName());
        } else {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("target-tossing-message")), player.getName(), target.getDisplayName())));
        }

        plugin.setPlayerToss(target.getUniqueId(), true);

    }

}

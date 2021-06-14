package me.adelemphii.entitytoss.events;

import me.adelemphii.entitytoss.EntityToss;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Objects;

public class TossingEvent implements Listener {

    EntityToss plugin;
    public TossingEvent(EntityToss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Player target;

        if (player.getPassengers().isEmpty()) {
            return;
        }

        List<Entity> passengers = player.getPassengers();

        for (Entity entity : passengers) {
            if (entity instanceof Player) {
                target = (Player) entity;

                if (plugin.getPlayerToss().get(target.getUniqueId())) {
                    player.eject();

                    target.setVelocity(player.getLocation().getDirection().multiply(2.45f).setY(1.2f));

                    if(plugin.getConfig().getString("player-tossed-message") == null) {
                        player.sendMessage("You are now tossing " + target.getName());
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin. placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossed-message")), player.getName(), target.getDisplayName())));
                    }

                    if(plugin.getConfig().getString("target-tossed-message") == null) {
                        target.sendMessage("You are being tossed by " + player.getName());
                    } else {
                        target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("target-tossed-message")), player.getName(), target.getDisplayName())));
                    }
                }
            } else {
                if (plugin.getPlayerToss().get(entity.getUniqueId())) {
                    player.eject();

                    entity.setVelocity(player.getLocation().getDirection().multiply(2.45f).setY(1.2f));

                    if (plugin.getConfig().getString("player-tossed-message") == null) {
                        player.sendMessage("You are now tossing " + entity.getName());
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossed-message")), player.getName(), entity.getName())));
                    }
                }
            }
        }
    }

}

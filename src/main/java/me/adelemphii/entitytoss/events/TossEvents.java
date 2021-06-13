package me.adelemphii.entitytoss.events;

import me.adelemphii.entitytoss.EntityToss;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.List;
import java.util.Objects;

public class TossEvents implements Listener {

    EntityToss plugin;
    public TossEvents(EntityToss plugin) {
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

        if (!(event.getRightClicked() instanceof Player)) {
            Player player = event.getPlayer();
            Entity target = event.getRightClicked();

            player.addPassenger(target);
            plugin.setPlayerToss(target.getUniqueId(), true);

            if(plugin.getConfig().getString("player-tossing-message") == null) {
                player.sendMessage("You are now tossing " + target.getName());
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossing-message")), player.getName(), target.getName())));
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
                    placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossing-message")), player.getName(), target.getDisplayName())));
        }

        if(plugin.getConfig().getString("target-tossing-message") == null) {
            target.sendMessage("You are being tossed by " + player.getName());
        } else {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    placeholders(Objects.requireNonNull(plugin.getConfig().getString("target-tossing-message")), player.getName(), target.getDisplayName())));
        }

        plugin.setPlayerToss(target.getUniqueId(), true);

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
                                placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossed-message")), player.getName(), target.getDisplayName())));
                    }

                    if(plugin.getConfig().getString("target-tossed-message") == null) {
                        target.sendMessage("You are being tossed by " + player.getName());
                    } else {
                        target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                placeholders(Objects.requireNonNull(plugin.getConfig().getString("target-tossed-message")), player.getName(), target.getDisplayName())));
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
                                placeholders(Objects.requireNonNull(plugin.getConfig().getString("player-tossed-message")), player.getName(), entity.getName())));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if(!plugin.getConfig().getBoolean("mob-landing-event")) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }

            Player player = (Player) event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {

                if (!plugin.getPlayerToss().containsKey(player.getUniqueId())) {
                    return;
                }

                if (plugin.getPlayerToss().get(player.getUniqueId())) {
                    event.setCancelled(true);
                    plugin.setPlayerToss(player.getUniqueId(), false);

                    Location landing = player.getLocation();
                    World world = landing.getWorld();

                    assert world != null;
                    world.playSound(landing, Sound.BLOCK_ANVIL_LAND, 1f, .5f);

                    for (Entity entity : player.getNearbyEntities(5, 5, 5)) {

                        Vector vector = entity.getLocation().toVector().subtract(landing.toVector()).setY(1).normalize();

                        entity.setVelocity(vector);

                        if (entity instanceof Player) {
                            Player nearby = (Player) entity;

                            if (plugin.getConfig().getString("player-tossed-message") == null) {
                                nearby.sendMessage(player.getDisplayName() + " has been tossed into the fray!");
                            } else {
                                nearby.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        placeholders(Objects.requireNonNull(plugin.getConfig().getString("landing-slam-message")), player.getName())));
                            }
                        }
                    }
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (plugin.getPlayerToss().containsKey(player.getUniqueId())) {

                    if (plugin.getPlayerToss().get(player.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        } else {

            Entity entity = event.getEntity();

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {

                if (!plugin.getPlayerToss().containsKey(entity.getUniqueId())) {
                    return;
                }

                if (plugin.getPlayerToss().get(entity.getUniqueId())) {
                    event.setCancelled(true);
                    plugin.setPlayerToss(entity.getUniqueId(), false);

                    Location landing = entity.getLocation();
                    World world = landing.getWorld();

                    if(plugin.getConfig().getBoolean("creeper-explode-on-land")) {
                        if (entity instanceof Creeper) {
                            Creeper creeper = (Creeper) entity;
                            creeper.explode();
                        }
                    }

                    assert world != null;
                    world.playSound(landing, Sound.BLOCK_ANVIL_LAND, 1f, .5f);

                    for (Entity nearbyEntity : entity.getNearbyEntities(5, 5, 5)) {

                        Vector vector = entity.getLocation().toVector().subtract(landing.toVector()).setY(1).normalize();

                        nearbyEntity.setVelocity(vector);

                        if (nearbyEntity instanceof Player) {
                            Player nearby = (Player) nearbyEntity;

                            if (plugin.getConfig().getString("player-tossed-message") == null) {
                                nearby.sendMessage(entity.getName() + " has been tossed into the fray!");
                            } else {
                                nearby.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        placeholders(Objects.requireNonNull(plugin.getConfig().getString("landing-slam-message")), entity.getName())));
                            }
                        }
                    }
                }
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (plugin.getPlayerToss().containsKey(entity.getUniqueId())) {
                    if (plugin.getPlayerToss().get(entity.getUniqueId())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public String placeholders(String msg, String name) {
        return msg.replace("%player%", name);
    }

    public String placeholders(String msg, String playerName, String targetName) {
        return msg.replace("%player%", playerName).replace("%target%", targetName);
    }
}

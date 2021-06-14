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
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

public class LandingDamageEvent implements Listener {

    EntityToss plugin;
    public LandingDamageEvent(EntityToss plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if(!plugin.getConfig().getBoolean("mob-landing-event")) {
            if (!(event.getEntity() instanceof Player)) {
                return;
            }

            // Player Version
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

                        if(!entity.hasMetadata("NPC")) {

                            entity.setVelocity(vector);

                            if (entity instanceof Player) {
                                Player nearby = (Player) entity;

                                if (plugin.getConfig().getString("player-tossed-message") == null) {
                                    nearby.sendMessage(player.getDisplayName() + " has been tossed into the fray!");
                                } else {
                                    nearby.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("landing-slam-message")), player.getName())));
                                }
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

            // Entity Version
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

                        if (!nearbyEntity.hasMetadata("NPC")) {

                            nearbyEntity.setVelocity(vector);

                            if (nearbyEntity instanceof Player) {
                                Player nearby = (Player) nearbyEntity;

                                if (plugin.getConfig().getString("player-tossed-message") == null) {
                                    nearby.sendMessage(entity.getName() + " has been tossed into the fray!");
                                } else {
                                    nearby.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            plugin.placeholders(Objects.requireNonNull(plugin.getConfig().getString("landing-slam-message")), entity.getName())));
                                }
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

}

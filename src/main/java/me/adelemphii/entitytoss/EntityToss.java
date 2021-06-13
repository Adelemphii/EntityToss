package me.adelemphii.entitytoss;

import me.adelemphii.entitytoss.commands.ReloadCommand;
import me.adelemphii.entitytoss.events.TossEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EntityToss extends JavaPlugin {

    Map<UUID, Boolean> playerToss = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        registerCE();

    }

    @Override
    public void onDisable() {
    }

    public void registerCE() {

        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new TossEvents(this), this);

        getCommand("tossreload").setExecutor(new ReloadCommand(this));

    }

    public Map<UUID, Boolean> getPlayerToss() {
        return playerToss;
    }

    public void setPlayerToss(UUID uuid, boolean state) {
        playerToss.put(uuid, state);
    }

    public void removePlayerToss(UUID uuid) {
        playerToss.remove(uuid);
    }
}

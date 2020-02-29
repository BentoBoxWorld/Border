package world.bentobox.border;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.database.Database;
import world.bentobox.border.commands.IslandBorderCommand;
import world.bentobox.border.listeners.PlayerListener;

public final class Border extends Addon {

    private WorldBorderApi worldBorderApi;
    private Database<BorderData> handler;
    private Map<UUID, BorderData> borderCache;

    public void getLevelsData(@NonNull UUID targetPlayer, Consumer<BorderData> callback) {
        BorderData borderData = borderCache.get(targetPlayer);
        if (borderData != null) {
            callback.accept(borderData);
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            String uniqueId = targetPlayer.toString();
            // Check if data exists before trying to load it
            BorderData data = handler.objectExists(uniqueId) ? Optional.ofNullable(handler.loadObject(uniqueId)).orElse(new BorderData(uniqueId)): new BorderData(uniqueId);
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                borderCache.put(targetPlayer, data);
                callback.accept(data);
            });
        });
    }

    public void uncachePlayer(@Nullable UUID uniqueId) {
        BorderData data = borderCache.remove(uniqueId);
        if (data == null) {
            return;
        }
        // NOTE: saveObject is NOT a blocking operation
        handler.saveObject(data);
    }

    public void updateBorder(Player player, Location location) {
        removeBorder(player); // Remove current
        getLevelsData(player.getUniqueId(), data -> {
            if (!data.isEnabled()) {
                return;
            }
            getPlugin().getIslands().getIslandAt(location)
            .ifPresent(island -> worldBorderApi.setBorder(player, island.getProtectionRange() * 2, island.getCenter()));
        });
    }

    public void removeBorder(Player player) {
        worldBorderApi.resetWorldBorderToGlobal(player);
    }

    @Override
    public void onEnable() {
        handler = new Database<>(this, BorderData.class);
        borderCache = new HashMap<>();

        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorderAPI");
        if (plugin == null || !plugin.isEnabled()) {
            getLogger().warning("WorldBorderAPI not found. Download from https://github.com/yannicklamprecht/WorldBorderAPI/releases");
            this.setState(State.DISABLED);
            return;
        }
        worldBorderApi = BorderAPI.getApi();
        // Register listeners
        registerListener(new PlayerListener(this));

        // Register commands
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
            log("Border hooking into " + gameModeAddon.getDescription().getName());
            gameModeAddon.getPlayerCommand().ifPresent(c -> new IslandBorderCommand(this, c, "border"));
        });
    }

    @Override
    public void onDisable() {
        if (borderCache != null) {
            borderCache.values().forEach(handler::saveObject);
        }
    }

    @Nullable
    public Database<BorderData> getHandler() {
        return handler;
    }

}

package world.bentobox.border;

import com.github.yannicklamprecht.worldborder.api.BorderAPI;
import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.database.Database;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.commands.IslandBorderCommand;
import world.bentobox.border.listeners.PlayerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

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
        new BukkitRunnable() {
            @Override
            public void run() {
                String uniqueId = targetPlayer.toString();
                BorderData data = Optional.ofNullable(handler.loadObject(uniqueId))
                        .orElseGet(() -> new BorderData(uniqueId));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        borderCache.put(targetPlayer, data);
                        callback.accept(data);
                    }
                }.runTask(getPlugin());
            }
        }.runTaskAsynchronously(getPlugin());
    }

    public void uncachePlayer(@Nullable UUID uniqueId) {
        BorderData data = borderCache.remove(uniqueId);
        if (data == null) {
            return;
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                handler.saveObject(data);
            }
        }.runTaskAsynchronously(getPlugin());
    }

    public void updateBorder(Player player, Location location) {
        removeBorder(player); // Remove current
        getLevelsData(player.getUniqueId(), data -> {
            if (!data.isEnabled()) {
                return;
            }
            Island island = getPlugin().getIslands().getIslandAt(location).orElse(null);
            if (island == null) {
                return;
            }
            worldBorderApi.setBorder(player, island.getProtectionRange() * 2, island.getCenter());
        });
    }

    public void removeBorder(Player player) {
        worldBorderApi.resetWorldBorderToGlobal(player);
    }

    @Override
    public void onEnable() {
        worldBorderApi = BorderAPI.getApi();
        handler = new Database<>(this, BorderData.class);
        borderCache = new HashMap<>();

        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldBorderAPI");
        if (plugin == null || !plugin.isEnabled()) {
            getLogger().warning("WorldBorderAPI not found. Download from https://github.com/yannicklamprecht/WorldBorderAPI/releases");
            this.setState(State.DISABLED);
            return;
        }

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

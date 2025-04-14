package world.bentobox.border.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import com.google.common.base.Enums;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;

/**
 *
 * Shows a border using barrier blocks and/or particles
 *
 * @author tastybento
 *
 */
public class ShowBarrier implements BorderShower {

    private final Border addon;
    private static final Particle PARTICLE = Enums.getIfPresent(Particle.class, "DUST")
            .or(Enums.getIfPresent(Particle.class, "REDSTONE").or(Particle.FLAME));
    private static final Particle MAX_PARTICLE = Enums.getIfPresent(Particle.class, "BARRIER_BLOCK")
            .or(Enums.getIfPresent(Particle.class, "BARRIER").or(Particle.FLAME));
    private static final Particle.DustOptions PARTICLE_DUST_RED = new Particle.DustOptions(Color.RED, 1.0F);
    private static final Particle.DustOptions PARTICLE_DUST_BLUE = new Particle.DustOptions(Color.BLUE, 1.0F);
    private static final int BARRIER_RADIUS = 5;
    private final Map<UUID, Set<BarrierBlock>> barrierBlocks = new HashMap<>();


    /**
     * @param addon - addon
     */
    public ShowBarrier(Border addon) {
        this.addon = addon;
    }

    /**
     * Show the barrier to the player on an island
     * @param player - player to show
     * @param island - island
     */
    @Override
    public void showBorder(Player player, Island island) {

        if (addon.getSettings().getDisabledGameModes().contains(island.getGameMode())
                || !Objects.requireNonNull(User.getInstance(player)).getMetaData(BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault())) {
            return;
        }
        int offset = addon.getSettings().getBarrierOffset();
        // Get the locations to show
        Location loc = player.getLocation();
        showWalls(player, loc,
                Math.max(island.getMinX(), island.getMinProtectedX() - offset),
                Math.min(island.getMaxX(), island.getMaxProtectedX() + offset),
                Math.max(island.getMinZ(),island.getMinProtectedZ() - offset),
                Math.min(island.getMaxZ(), island.getMaxProtectedZ() + offset), false);
        // If the max border needs to be shown, show it as well
        if (addon.getSettings().isShowMaxBorder()) {

            showWalls(player, loc,
                    island.getMinX(),
                    island.getMaxX(),
                    island.getMinZ(),
                    island.getMaxZ(), true);
        }

    }

    private void showWalls(Player player, Location loc, int xMin, int xMax, int zMin, int zMax, boolean max) {
        if (loc.getBlockX() - xMin < BARRIER_RADIUS) {
            
            // Close to min x
            for (int z = Math.max(loc.getBlockZ() - BARRIER_RADIUS, zMin); z < loc.getBlockZ() + BARRIER_RADIUS && z < zMax; z++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, xMin-1, loc.getBlockY() + y, z, max);
                }
            }
        }
        if (loc.getBlockZ() - zMin < BARRIER_RADIUS) {

            // Close to min z
            for (int x = Math.max(loc.getBlockX() - BARRIER_RADIUS, xMin); x < loc.getBlockX() + BARRIER_RADIUS && x < xMax; x++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, x, loc.getBlockY() + y, zMin-1, max);
                }
            }
        }
        if (xMax - loc.getBlockX() < BARRIER_RADIUS) {

            // Close to max x
            for (int z = Math.max(loc.getBlockZ() - BARRIER_RADIUS, zMin); z < loc.getBlockZ() + BARRIER_RADIUS && z < zMax; z++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, xMax, loc.getBlockY() + y, z, max); // not xMax+1, that's outside the region
                }
            }
        }
        if (zMax - loc.getBlockZ() < BARRIER_RADIUS) {

            // Close to max z
            for (int x = Math.max(loc.getBlockX() - BARRIER_RADIUS, xMin); x < loc.getBlockX() + BARRIER_RADIUS && x < xMax; x++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, x, loc.getBlockY() + y, zMax, max); // not zMax+1, that's outside the region
                }
            }
        }

    }

    /**
     * @param player player
     * @param i 
     * @param j
     * @param k
     * @param max
     */
    private void showPlayer(Player player, int i, int j, int k, boolean max) {
        // Get if on or in border
        if (addon.getSettings().isUseBarrierBlocks()
                && player.getLocation().getBlockX() == i
                && player.getLocation().getBlockZ() == k) {
            teleportPlayer(player);
        }
        
        Location l = new Location(player.getWorld(), i, j, k);
        Util.getChunkAtAsync(l).thenAccept(c -> {
            if (addon.getSettings().isShowParticles()) {
                if (j < player.getWorld().getMinHeight() || j > player.getWorld().getMaxHeight()) {
                    User.getInstance(player).spawnParticle(max ? MAX_PARTICLE : PARTICLE, PARTICLE_DUST_RED, i + 0.5D, j + 0.0D, k + 0.5D);
                } else {
                    User.getInstance(player).spawnParticle(max ? MAX_PARTICLE : PARTICLE, PARTICLE_DUST_BLUE, i + 0.5D, j + 0.0D, k + 0.5D);
                }
            }
            if (addon.getSettings().isUseBarrierBlocks() && (l.getBlock().isEmpty() || l.getBlock().isLiquid())) {
                player.sendBlockChange(l, Material.BARRIER.createBlockData());
                barrierBlocks.computeIfAbsent(player.getUniqueId(), u -> new HashSet<>()).add(new BarrierBlock(l, l.getBlock().getBlockData()));
            }
        });
    }

    /**
     * Teleport player back within the island space they are in
     * @param p player
     */
    public void teleportPlayer(Player p) {
        addon.getIslands().getIslandAt(p.getLocation()).ifPresent(i -> {
            Vector unitVector = i.getCenter().toVector().subtract(p.getLocation().toVector()).normalize()
                    .multiply(new Vector(1, 0, 1));
            // Get distance from border
            Location to = p.getLocation().toVector().add(unitVector).toLocation(p.getWorld());
            to.setPitch(p.getLocation().getPitch());
            to.setYaw(p.getLocation().getYaw());
            Util.teleportAsync(p, to, TeleportCause.PLUGIN);
        });
    }

    /**
     * Hide the barrier
     * @param user - user
     */
    @Override
    public void hideBorder(User user) {
        if (barrierBlocks.containsKey(user.getUniqueId())) {
            barrierBlocks.get(user.getUniqueId()).stream()
            .filter(v -> v.l.getWorld().equals(user.getWorld()))
            .forEach(v -> user.getPlayer().sendBlockChange(v.l, v.oldBlockData));
            // Clean up
            clearUser(user);
        }
    }

    /**
     * Removes any cached barrier blocks
     * @param user - user
     */
    @Override
    public void clearUser(User user) {
        barrierBlocks.remove(user.getUniqueId());
    }

    @Override
    public void refreshView(User user, Island island) {
        this.showBorder(user.getPlayer(), island);
    }

    private class BarrierBlock {
        Location l;
        BlockData oldBlockData;
        public BarrierBlock(Location l, BlockData oldBlockData) {
            super();
            this.l = l;
            this.oldBlockData = oldBlockData;
        }

    }
}

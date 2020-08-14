package world.bentobox.border.listeners;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;

/**
 * Displays a border to a player
 * @author tastybento
 *
 */
public class PlayerBorder implements Listener {

    private static final BlockData BLOCK = Material.BARRIER.createBlockData();
    private final Border addon;
    private static final Particle PARTICLE = Particle.REDSTONE;
    private static final Particle.DustOptions PARTICLE_DUST_OPTIONS = new Particle.DustOptions(Color.BLUE, 1.0F);
    private static final int BARRIER_RADIUS = 5;


    /**
     * @param addon
     */
    public PlayerBorder(Border addon) {
        super();
        this.addon = addon;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        // Only trigger if the player moves horizontally
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            addon.getIslands().getIslandAt(e.getPlayer().getLocation()).ifPresent(i -> showBarrier(e.getPlayer(), i));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVehicleMove(VehicleMoveEvent e) {
        // Only trigger if the vehicle moves horizontally
        if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
            e.getVehicle().getPassengers().stream().filter(en -> en instanceof Player).map(en -> (Player)en).forEach(p ->
            addon.getIslands().getIslandAt(p.getLocation()).ifPresent(i -> showBarrier(p, i)));
        }
    }

    /**
     * Show the barrier to the player on an island
     * @param player - player to show
     * @param island - island
     */
    public void showBarrier(Player player, Island island) {
        // Only show if the player has the permission
        String perm = addon.getPlugin().getIWM().getPermissionPrefix(player.getWorld()) + "border.off";
        if (player.getEffectivePermissions().stream().map(pa -> pa.getPermission()).anyMatch(perm::equalsIgnoreCase)) return;
        // Get the locations to show
        Location loc = player.getLocation();
        int xMin = island.getMinProtectedX();
        int xMax = island.getMaxProtectedX();
        int zMin = island.getMinProtectedZ();
        int zMax = island.getMaxProtectedZ();
        if (loc.getBlockX() - xMin < BARRIER_RADIUS) {
            // Close to min x
            for (int z = -BARRIER_RADIUS; z < BARRIER_RADIUS; z++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, xMin-1, loc.getBlockY() + y, loc.getBlockZ() + z);
                }
            }
        }
        if (loc.getBlockZ() - zMin < BARRIER_RADIUS) {
            // Close to min z
            for (int x = -BARRIER_RADIUS; x < BARRIER_RADIUS; x++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, loc.getBlockX() + x, loc.getBlockY() + y, zMin-1);
                }
            }
        }
        if (xMax - loc.getBlockX() < BARRIER_RADIUS) {
            // Close to max x
            for (int z = -BARRIER_RADIUS; z < BARRIER_RADIUS; z++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, xMax, loc.getBlockY() + y, loc.getBlockZ() + z); // not xMax+1, that's outside the region
                }
            }
        }
        if (zMax - loc.getBlockZ() < BARRIER_RADIUS) {
            // Close to max z
            for (int x = -BARRIER_RADIUS; x < BARRIER_RADIUS; x++) {
                for (int y = -BARRIER_RADIUS; y < BARRIER_RADIUS; y++) {
                    showPlayer(player, loc.getBlockX() + x, loc.getBlockY() + y, zMax); // not zMax+1, that's outside the region
                }
            }
        }
    }

    private void showPlayer(Player player, int i, int j, int k) {
        Location l = new Location(player.getWorld(), i, j, k);
        Util.getChunkAtAsync(l).thenAccept(c -> {
            if (l.getBlock().isEmpty() || l.getBlock().isLiquid()) {
                player.sendBlockChange(l, BLOCK);
            }
            User.getInstance(player).spawnParticle(PARTICLE, PARTICLE_DUST_OPTIONS, i + 0.5D, j + 0.0D, k + 0.5D);
        });

    }

}

package world.bentobox.border.listeners;

import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;

/**
 * Shows both borders at the same time
 */
public class BothShower implements BorderShower {
    
    private final BorderShower vanilla;
    private final BorderShower barrier;

    public BothShower(BorderShower vanilla, BorderShower barrier) {
        super();
        this.vanilla = vanilla;
        this.barrier = barrier;
    }

    @Override
    public void showBorder(Player player, Island island) {
       vanilla.showBorder(player, island);
       barrier.showBorder(player, island);
    }

    @Override
    public void hideBorder(User user) {
        vanilla.hideBorder(user);
        barrier.hideBorder(user);
    }

    @Override
    public void teleportPlayer(Player player) {
        vanilla.teleportPlayer(player);
        barrier.teleportPlayer(player);

    }

}

package world.bentobox.border.commands;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.border.Border;

import java.util.List;

public class IslandBorderCommand extends CompositeCommand {

    public IslandBorderCommand(Border addon, CompositeCommand parent, String label) {
        super(addon, parent, label);
    }

    @Override
    public void setup() {
        this.setPermission("border.toggle");
        this.setDescription("border.toggle.description");
        this.setOnlyPlayer(true);
        setConfigurableRankCommand();
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        return getIslands().getIsland(getWorld(), user) != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Border addon = (Border) this.getAddon();
        addon.getLevelsData(user.getUniqueId(), data -> {
            data.setEnabled(!data.isEnabled());
            if (data.isEnabled()) {
                user.sendMessage("border.toggle.border-on");
                addon.updateBorder(user.getPlayer(), user.getPlayer().getLocation());
            } else {
                user.sendMessage("border.toggle.border-off");
                addon.removeBorder(user.getPlayer());
            }
        });
        return true;
    }
}

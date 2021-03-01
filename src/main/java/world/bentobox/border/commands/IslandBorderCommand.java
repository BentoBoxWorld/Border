package world.bentobox.border.commands;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.Border;
import world.bentobox.border.listeners.BorderShower;

public class IslandBorderCommand extends CompositeCommand {

    private Border addon;
    private Island island;

    public IslandBorderCommand(Border addon, CompositeCommand parent, String label) {
        super(addon, parent, label);
        this.addon = addon;
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
        island = getIslands().getIsland(getWorld(), user);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        boolean on = user.getMetaData(BorderShower.BORDER_STATE_META_DATA).map(md -> md.asBoolean()).orElse(addon.getSettings().isShowByDefault());
        if (on) {
            user.sendMessage("border.toggle.border-off");
            user.putMetaData(BorderShower.BORDER_STATE_META_DATA, new MetaDataValue(false));
            addon.getPlayerBorder().getBorder().hideBorder(user);
        } else {
            user.sendMessage("border.toggle.border-on");
            user.putMetaData(BorderShower.BORDER_STATE_META_DATA, new MetaDataValue(true));
            addon.getPlayerBorder().getBorder().showBorder(user.getPlayer(), island);
        }
        return true;
    }

}

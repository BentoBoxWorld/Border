package world.bentobox.border.commands;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.border.Border;
import world.bentobox.border.listeners.PlayerBorder;

public class IslandBorderCommand extends CompositeCommand {

    private Border addon;

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
        return getIslands().getIsland(getWorld(), user) != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        boolean on = user.getMetaData("Border_state").map(md -> md.asBoolean()).orElse(false);
        if (on) {
            user.sendMessage("border.toggle.border-off");
            user.putMetaData(PlayerBorder.BORDER_STATE, new MetaDataValue(false));
            addon.getPlayerBorder().hideBarrier(user);
        } else {
            user.sendMessage("border.toggle.border-on");
            user.putMetaData(PlayerBorder.BORDER_STATE, new MetaDataValue(true));
        }
        return true;
    }

}

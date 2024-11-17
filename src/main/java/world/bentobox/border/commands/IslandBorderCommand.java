package world.bentobox.border.commands;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.listeners.BorderShower;

public class IslandBorderCommand extends CompositeCommand {

    public static final String BORDER_COMMAND_PERM = "border.toggle";
    private Border addon;
    private Island island;

    public IslandBorderCommand(Border addon, CompositeCommand parent, String label) {
        super(addon, parent, label);
        this.addon = addon;
    }

    @Override
    public void setup() {
        this.setPermission(BORDER_COMMAND_PERM);
        this.setDescription("border.toggle.description");
        this.setOnlyPlayer(true);
        setConfigurableRankCommand();

        new BorderTypeCommand(this.getAddon(), this, "type");
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        if (!this.getWorld().equals(Util.getWorld(user.getWorld())))
        {
            user.sendMessage("general.errors.wrong-world");
            return false;
        }
        island = addon.getIslands().getIslandAt(user.getLocation()).orElse(null);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        boolean on = user.getMetaData(BorderShower.BORDER_STATE_META_DATA).map(MetaDataValue::asBoolean).orElse(addon.getSettings().isShowByDefault());
        if (on) {
            user.sendMessage("border.toggle.border-off");
            user.putMetaData(BorderShower.BORDER_STATE_META_DATA, new MetaDataValue(false));
            addon.getBorderShower().hideBorder(user);
        } else {
            user.sendMessage("border.toggle.border-on");
            user.putMetaData(BorderShower.BORDER_STATE_META_DATA, new MetaDataValue(true));
            if (island != null) {
                addon.getBorderShower().showBorder(user.getPlayer(), island);
            }
        }
        return true;
    }

}

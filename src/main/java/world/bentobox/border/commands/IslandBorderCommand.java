package world.bentobox.border.commands;

import java.util.List;

import org.bukkit.permissions.PermissionAttachmentInfo;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.border.Border;

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
        String perm = this.getIWM().getPermissionPrefix(getWorld()) + "border.off";
        if (user.getEffectivePermissions().stream().map(PermissionAttachmentInfo::getPermission).anyMatch(perm::equalsIgnoreCase)) {
            user.sendMessage("border.toggle.border-on");
            user.removePerm(perm);
        } else {
            user.sendMessage("border.toggle.border-off");
            user.addPerm(perm);
        }
        return true;
    }

}

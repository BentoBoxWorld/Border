package world.bentobox.border.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.util.Util;
import world.bentobox.border.Border;
import world.bentobox.border.PerPlayerBorderProxy;
import world.bentobox.border.Settings.BorderColor;

/**
 * Command to allow players with the appropriate permission to set their own border color.
 * Permission required: [gamemode].border.color.[color] e.g. bskyblock.border.color.red
 */
public final class BorderColorCommand extends CompositeCommand {

    private static final List<String> COLOR_NAMES = Arrays.stream(BorderColor.values())
            .map(c -> c.name().toLowerCase())
            .toList();

    private final Border addon;
    private Island island;

    public BorderColorCommand(Border addon, CompositeCommand parent, String commandLabel) {
        super(addon, parent, commandLabel);
        this.addon = addon;
    }

    @Override
    public void setup() {
        this.setPermission("border.color");
        this.setDescription("border.set-color.description");
        this.setOnlyPlayer(true);
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        if (!this.getWorld().equals(Util.getWorld(user.getWorld()))) {
            user.sendMessage("general.errors.wrong-world");
            return false;
        }
        island = getIslands().getIsland(getWorld(), user);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (args.size() != 1) {
            this.showHelp(this, user);
            return false;
        }

        String colorArg = args.getFirst().toLowerCase();

        if (!COLOR_NAMES.contains(colorArg)) {
            user.sendMessage("border.set-color.error-invalid-color");
            return false;
        }

        String permPrefix = getPlugin().getIWM().getPermissionPrefix(getWorld());
        String colorPerm = permPrefix + "border.color." + colorArg;
        if (!user.hasPermission(colorPerm)) {
            user.sendMessage("general.errors.no-permission", "[permission]", colorPerm);
            return false;
        }

        BorderColor color = BorderColor.valueOf(colorArg.toUpperCase());
        addon.getBorderShower().hideBorder(user);
        user.putMetaData(PerPlayerBorderProxy.BORDER_COLOR_META_DATA, new MetaDataValue(color.name()));
        addon.getBorderShower().showBorder(user.getPlayer(), island);
        user.sendMessage("border.set-color.changed", "[color]", colorArg);
        return true;
    }

    @Override
    public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
        String permPrefix = getPlugin().getIWM().getPermissionPrefix(getWorld());
        return Optional.of(COLOR_NAMES.stream()
                .filter(c -> user.hasPermission(permPrefix + "border.color." + c))
                .toList());
    }
}

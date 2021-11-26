package world.bentobox.border.commands;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.Border;
import world.bentobox.border.BorderType;
import world.bentobox.border.PerPlayerBorderProxy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class BorderTypeCommand extends CompositeCommand {

    private final Border addon;
    private Island island;

    private static final String WorldBorderType = "wb";
    private static final String CustomBorderType = "custom";

    private static final List<String> AvailableTypes = Arrays.asList(WorldBorderType, CustomBorderType);
    // TODO: add wb type only if WB is enabled / only allow to have it set for that

    public BorderTypeCommand(Border addon, CompositeCommand parent) {
        super(addon, parent, "type");
        this.addon = addon;
    }

    @Override
    public void setup() {
        this.setPermission("border.settype");
        this.setDescription("border.settype.description");
        this.setOnlyPlayer(true);
        // setConfigurableRankCommand(); // should I use this? What is this?
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        island = getIslands().getIsland(getWorld(), user);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (args.isEmpty()) {
            this.showHelp(this, user);
            return false;
        }

        String newBorderType = args.get(0);
        if (AvailableTypes.stream().anyMatch(newBorderType::equalsIgnoreCase)) {
            changeBorderTypeTo(user, newBorderType);
            return true;
        }

        // TODO: say unknown border type
        return false;
    }

    private void changeBorderTypeTo(User user, String newBorderType) {
        BorderType borderType = BorderType.fromCommandLabel(newBorderType).get();
        byte newType = borderType.getId();

        addon.getBorderShower().hideBorder(user);
        user.putMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA, new MetaDataValue(newType));
        addon.getBorderShower().showBorder(user.getPlayer(), island);
    }

    @Override
    public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
        return Optional.of(AvailableTypes);
    }
}

package world.bentobox.border.commands;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.Border;
import world.bentobox.border.BorderType;
import world.bentobox.border.PerPlayerBorderProxy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class BorderTypeCommand extends CompositeCommand {

    private final Border addon;
    private Island island;
    private final List<String> availableTypes;

    public BorderTypeCommand(Border addon, CompositeCommand parent) {
        super(addon, parent, "type");
        this.addon = addon;
        this.availableTypes = addon.getAvailableBorderTypesView()
                .stream()
                .map(BorderType::getCommandLabel)
                .collect(Collectors.toList());
    }

    @Override
    public void setup() {
        this.setPermission("border.set-type");
        this.setDescription("border.set-type.description");
        this.setOnlyPlayer(true);
        // setConfigurableRankCommand(); // What is this? Should I use this? I guess not.
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        island = getIslands().getIsland(getWorld(), user);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (args.size() != 1) {
            this.showHelp(this, user);
            return false;
        }

        if (availableTypes.stream().anyMatch(args.get(0)::equalsIgnoreCase)) {
            changeBorderTypeTo(user, args.get(0));
            return true;
        }

        user.sendMessage("border.set-type.error-unavailable-type");
        return false;
    }

    private void changeBorderTypeTo(User user, String newBorderType) {
        byte newTypeId = BorderType.fromCommandLabel(newBorderType).get().getId();

        addon.getBorderShower().hideBorder(user);
        user.putMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA, new MetaDataValue(newTypeId));
        addon.getBorderShower().showBorder(user.getPlayer(), island);

        user.sendMessage("border.set-type.changed",
                "[type]", newBorderType);
    }

    @Override
    public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
        return Optional.of(availableTypes);
    }
}

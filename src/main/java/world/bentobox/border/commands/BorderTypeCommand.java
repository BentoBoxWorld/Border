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
import world.bentobox.border.BorderType;
import world.bentobox.border.PerPlayerBorderProxy;

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
                .toList();
    }

    @Override
    public void setup() {
        this.setPermission("border.type");
        this.setDescription("border.set-type.description");
        this.setOnlyPlayer(true);
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        if (!this.getWorld().equals(Util.getWorld(user.getWorld())))
        {
            user.sendMessage("general.errors.wrong-world");
            return false;
        }

        island = getIslands().getIsland(getWorld(), user);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        if (args.size() == 0) {
            this.toggleBorderType(user);
            return false;
        }

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


    /**
     * This method toggles from one island border type to another border type.
     * @param user User whos border must be changed.
     */
    private void toggleBorderType(User user)
    {
        MetaDataValue metaDataValue = user.getMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA).
                orElse(new MetaDataValue(BorderType.VANILLA.getId()));
        BorderType borderType = BorderType.fromId(metaDataValue.asByte()).orElse(BorderType.VANILLA);

        List<BorderType> borderTypes = Arrays.stream(BorderType.values()).toList();
        int index = borderTypes.indexOf(borderType);

        if (index + 1 >= borderTypes.size())
        {
            this.changeBorderTypeTo(user, borderTypes.get(0).getCommandLabel());
        }
        else
        {
            this.changeBorderTypeTo(user, borderTypes.get(index + 1).getCommandLabel());
        }
    }

    private void changeBorderTypeTo(User user, String newBorderType) {
        BorderType.fromCommandLabel(newBorderType).map(BorderType::getId).map(MetaDataValue::new)
        .ifPresentOrElse(newTypeId -> {
            addon.getBorderShower().hideBorder(user);
            user.putMetaData(PerPlayerBorderProxy.BORDER_BORDERTYPE_META_DATA, newTypeId);
            addon.getBorderShower().showBorder(user.getPlayer(), island);
            user.sendMessage("border.set-type.changed", "[type]", newBorderType);
        }, () -> addon.logError("Unknown newBorderType " + newBorderType));
    }

    @Override
    public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
        return Optional.of(availableTypes);
    }
}

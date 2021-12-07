package world.bentobox.border;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.listeners.BorderShower;

import java.util.Optional;

public final class PerPlayerBorderProxy implements BorderShower {

    public static final String BORDER_BORDERTYPE_META_DATA = "Border_bordertype";

    private final Border addon;
    private final BorderShower customBorder;
    private final BorderShower wbapiBorder;

    public PerPlayerBorderProxy(Border addon, BorderShower customBorder, BorderShower wbapiBorder) {
        this.addon = addon;
        this.customBorder = customBorder;
        this.wbapiBorder = wbapiBorder;
    }

    @Override
    public void showBorder(Player player, Island island) {
        var user = User.getInstance(player);
        var border = getBorder(user);
        border.showBorder(player, island);
    }

    @Override
    public void hideBorder(User user) {
        var border = getBorder(user);
        border.hideBorder(user);
    }

    @Override
    public void clearUser(User user) {
        var border = getBorder(user);
        border.clearUser(user);
    }

    @Override
    public void refreshView(User user, Island island) {
        var border = getBorder(user);
        border.refreshView(user, island);
    }

    private BorderShower getBorder(User user) {
        BorderType borderType = getBorderType(user);
        return switch (borderType) {
            case BARRIER -> customBorder;
            case VANILLA -> wbapiBorder;
        };
    }

    private BorderType getBorderType(User user) {
        Optional<Byte> userTypeId = user.getMetaData(BORDER_BORDERTYPE_META_DATA)
                .map(MetaDataValue::asByte);

        if (userTypeId.isEmpty()) {
            return getDefaultBorderType();
        }

        Optional<BorderType> borderType = BorderType.fromId(userTypeId.get());
        if (borderType.isEmpty() || !addon.getAvailableBorderTypesView().contains(borderType.get())) {
            return getDefaultBorderType();
        }

        return borderType.get();
    }

    private static BorderType getDefaultBorderType() {
        return BorderType.BARRIER;
    }
}

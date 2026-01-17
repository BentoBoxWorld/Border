package world.bentobox.border;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.listeners.BorderShower;
import world.bentobox.border.listeners.BothShower;
import world.bentobox.border.listeners.ShowBarrier;
import world.bentobox.border.listeners.ShowVirtualWorldBorder;

import java.util.Optional;

public final class PerPlayerBorderProxy implements BorderShower {

    public static final String BORDER_BORDERTYPE_META_DATA = "Border_bordertype";

    private final Border addon;
    private final BorderShower customBorder;
    private final BorderShower vanillaBorder;
    private BothShower bothBorder;

    public PerPlayerBorderProxy(Border addon) {
        this.addon = addon;
        this.customBorder = new ShowBarrier(addon);
        this.vanillaBorder = new ShowVirtualWorldBorder(addon);
        this.bothBorder = new BothShower(customBorder, vanillaBorder);
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
            case VANILLA -> vanillaBorder;
            case BOTH -> bothBorder;
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

    private BorderType getDefaultBorderType() {
        return addon.getSettings().getType();
    }

    @Override
    public void teleportPlayer(Player player) {
        if (getBorderType(User.getInstance(player)) == BorderType.BARRIER) {
            customBorder.teleportPlayer(player);
        } else {
            vanillaBorder.teleportPlayer(player);
        }

    }
}

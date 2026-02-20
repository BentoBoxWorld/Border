package world.bentobox.border;

import java.util.Optional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.listeners.BorderShower;

/**
 * Delegates border rendering to either the custom or vanilla implementation
 * based on the per-player border type metadata.
 * <p>
 * Selection rules:
 * <ul>
 * <li>If the player has no border type metadata, the add-on default is used.</li>
 * <li>If the metadata id is unknown or not enabled, the add-on default is used.</li>
 * <li>Otherwise, the stored border type is honored.</li>
 * </ul>
 */
public final class PerPlayerBorderProxy implements BorderShower {

    /**
     * Metadata key for a player's preferred border type id.
     */
    public static final String BORDER_BORDERTYPE_META_DATA = "Border_bordertype";
    /**
     * Metadata key for a player's preferred border color id.
     */
    public static final String BORDER_COLOR_META_DATA = "Border_color";

    private final Border addon;
    private final BorderShower customBorder;
    private final BorderShower vanillaBorder;

    /**
     * @param addon owning add-on providing settings and available types
     * @param customBorder custom border renderer (barrier-based)
     * @param vanillaBorder vanilla world border renderer
     */
    public PerPlayerBorderProxy(Border addon, BorderShower customBorder, BorderShower vanillaBorder) {
        this.addon = addon;
        this.customBorder = customBorder;
        this.vanillaBorder = vanillaBorder;
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
    public void teleportEntity(Border addon, Entity player) {
        if (getBorderType(User.getInstance(player)) == BorderType.BARRIER) {
            customBorder.teleportEntity(addon, player);
        } else {
            vanillaBorder.teleportEntity(addon, player);
        }

    }
}

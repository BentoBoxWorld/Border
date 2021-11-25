package world.bentobox.border;

import org.bukkit.entity.Player;
import world.bentobox.bentobox.api.metadata.MetaDataValue;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.border.listeners.BorderShower;

public final class PerPlayerBorderProxy implements BorderShower {

    private static final String BORDER_BORDERTYPE_META_DATA = "Border_bordertype";
    private static final byte BORDER_ID_CUSTOM = 1;
    private static final byte BORDER_ID_WBAPI = 2;

    private final BorderShower customBorder;
    private final BorderShower wbapiBorder;

    public PerPlayerBorderProxy(BorderShower customBorder, BorderShower wbapiBorder) {
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
        border.clearUser(user);
    }

    private BorderShower getBorder(User user) {
        byte borderType = getBorderSettingOrDefault(user);
        return switch (borderType) {
            case BORDER_ID_CUSTOM -> customBorder;
            case BORDER_ID_WBAPI -> wbapiBorder;
            default -> throw new IllegalStateException("Unexpected value: " + borderType);
        };
    }

    private byte getBorderSettingOrDefault(User user) {
        return user.getMetaData(BORDER_BORDERTYPE_META_DATA)
                .map(MetaDataValue::asByte)
                .orElse(getDefaultBorderTypeId());
    }

    private static byte getDefaultBorderTypeId(){
        return BORDER_ID_CUSTOM;
    }
}

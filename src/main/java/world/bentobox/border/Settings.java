package world.bentobox.border;

import java.util.HashSet;
import java.util.Set;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

@StoreAt(filename = "config.yml", path = "addons/Border")
public class Settings implements ConfigObject {

    @ConfigComment("Border addon configuration file")
    @ConfigComment("See the documentation at https://docs.bentobox.world/en/latest/addons/Border/")
    @ConfigComment("")
    @ConfigComment("This list stores GameModes in which Border addon should not work.")
    @ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
    @ConfigComment("disabled-gamemodes:")
    @ConfigComment(" - BSkyBlock")
    @ConfigEntry(path = "disabled-gamemodes")
    private Set<String> disabledGameModes = new HashSet<>();

    @ConfigComment("")
    @ConfigComment("Border type. Options are VANILLA, which uses the vanillia-style board or BARRIER,")
    @ConfigComment("which uses particles and barrier blocks. If players have permission to use the barrier type")
    @ConfigComment("they may override this option. If they do not have permission or lose the permission")
    @ConfigComment("then this setting will be used.")
    @ConfigEntry(path = "type")
    private BorderType type = BorderType.VANILLA;

    @ConfigComment("")
    @ConfigComment("Teleport players back inside the border if they somehow get outside.")
    @ConfigComment("This will teleport players back inside if they toggle the border with a command.")
    @ConfigEntry(path = "return-teleport")
    private boolean returnTeleport = true;

    @ConfigComment("")
    @ConfigComment("Barrier blocks on/off. Only applies if the border type is BARRIER.")
    @ConfigComment("If false, the border is indicated by particles only.")
    @ConfigEntry(path = "use-barrier-blocks")
    private boolean useBarrierBlocks = true;

    @ConfigComment("")
    @ConfigComment("Turn on barrier by default.")
    @ConfigEntry(path = "show-by-default")
    private boolean showByDefault = true;

    @ConfigComment("")
    @ConfigComment("Only applies if VANILLA type isn't used.")
    @ConfigComment("Show max-protection range border. This is a visual border only and not a barrier.")
    @ConfigComment("This setting is useful for game modes where the protection range can move around, like Boxed")
    @ConfigEntry(path = "show-max-border")
    private boolean showMaxBorder = true;

    @ConfigComment("")
    @ConfigComment("Only applies if VANILLA type isn't used.")
    @ConfigComment("Enables/disables all types of wall particles shown by the addon")
    @ConfigEntry(path = "show-particles")
    private boolean showParticles = true;

    @ConfigComment("")
    @ConfigComment("Barrier offset.")
    @ConfigComment("The barrier normally occurs at the protection range limit but this value extends it outwards.")
    @ConfigComment("This does not extend the protection range, but will enable players to go outside their protected area.")
    @ConfigComment("The barrier will not go further than the island distance. Minimum and default value is 0.")
    @ConfigEntry(path = "barrier-offset")
    private int barrierOffset = 0;
    
    /**
     * @param disabledGameModes new disabledGameModes value.
     */
    public void setDisabledGameModes(Set<String> disabledGameModes) {
        this.disabledGameModes = disabledGameModes;
    }

    /**
     * @return disabledGameModes value.
     */
    public Set<String> getDisabledGameModes() {
        return this.disabledGameModes;
    }

    /**
     * @return the useBarrierBlocks
     */
    public boolean isUseBarrierBlocks() {
        return useBarrierBlocks;
    }

    /**
     * @param useBarrierBlocks the useBarrierBlocks to set
     */
    public void setUseBarrierBlocks(boolean useBarrierBlocks) {
        this.useBarrierBlocks = useBarrierBlocks;
    }

    /**
     * @return the showByDefault
     */
    public boolean isShowByDefault() {
        return showByDefault;
    }

    /**
     * @param showByDefault the showByDefault to set
     */
    public void setShowByDefault(boolean showByDefault) {
        this.showByDefault = showByDefault;
    }

    /**
     * @return the showMaxBorder
     */
    public boolean isShowMaxBorder() {
        return showMaxBorder;
    }

    /**
     * @param showMaxBorder the showMaxBorder to set
     */
    public void setShowMaxBorder(boolean showMaxBorder) {
        this.showMaxBorder = showMaxBorder;
    }

    /**
     * @return the returnTeleport
     */
    public boolean isReturnTeleport() {
        return returnTeleport;
    }

    /**
     * @param returnTeleport the returnTeleport to set
     */
    public void setReturnTeleport(boolean returnTeleport) {
        this.returnTeleport = returnTeleport;
    }

    /**
     * @return the showParticles
     */
    public boolean isShowParticles() {
        return showParticles;
    }

    /**
     * @param showParticles the showParticles to set
     */
    public void setShowParticles(boolean showParticles) {
        this.showParticles = showParticles;
    }

    public BorderType getType() {
        if (type == null) {
            type = BorderType.VANILLA;
        }
        return type;
    }

    public void setType(BorderType type) {
        this.type = type;
    }

    public int getBarrierOffset() {
        if (barrierOffset < 0) {
            barrierOffset = 0;
        }
        return barrierOffset;
    }

    public void setBarrierOffset(int barrierOffset) {
        this.barrierOffset = barrierOffset;
        getBarrierOffset();
    }
}

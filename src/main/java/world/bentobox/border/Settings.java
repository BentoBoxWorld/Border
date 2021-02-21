package world.bentobox.border;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

import java.util.HashSet;
import java.util.Set;

@StoreAt(filename = "config.yml", path = "addons/Border")
public class Settings implements ConfigObject {

    @ConfigComment("")
    @ConfigComment("This list stores GameModes in which Border addon should not work.")
    @ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
    @ConfigComment("disabled-gamemodes:")
    @ConfigComment(" - BSkyBlock")
    @ConfigEntry(path = "disabled-gamemodes")
    private Set<String> disabledGameModes = new HashSet<>();

    @ConfigComment("")
    @ConfigComment("Use vanilla world border. Requires WorldBorderAPI plugin.")
    @ConfigComment("Download from https://github.com/yannicklamprecht/WorldBorderAPI/releases")
    @ConfigEntry(path = "use-wbapi")
    private boolean useWbapi = false;

    @ConfigComment("")
    @ConfigComment("Use barrier blocks. If false, the border is indicated by particles only.")
    @ConfigComment("Only applicable if vanilla world border is not used")
    @ConfigEntry(path = "use-barrier-blocks")
    private boolean useBarrierBlocks = true;

    @ConfigComment("")
    @ConfigComment("Default border behavior")
    @ConfigEntry(path = "show-by-default")
    private boolean showByDefault= true;

    @ConfigComment("")
    @ConfigComment("Show max-protection range border. This is a visual border only and not a barrier.")
    @ConfigEntry(path = "show-max-border")
    private boolean showMaxBorder= true;

    /**
     * @param disabledGameModes new disabledGameModes value.
     */
    public void setDisabledGameModes(Set<String> disabledGameModes)
    {
        this.disabledGameModes = disabledGameModes;
    }

    /**
     * @return disabledGameModes value.
     */
    public Set<String> getDisabledGameModes()
    {
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
     * @return the useWbapi
     */
    public boolean isUseWbapi() {
        return useWbapi;
    }

    /**
     * @param useWbapi the useWbapi to set
     */
    public void setUseWbapi(boolean useWbapi) {
        this.useWbapi = useWbapi;
    }
}

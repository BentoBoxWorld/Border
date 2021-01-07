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
    @ConfigComment("Use barrier blocks. If false, the border is indicated by particles only.")
    private boolean useBarrierBlocks = true;

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
}

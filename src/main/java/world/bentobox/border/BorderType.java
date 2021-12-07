package world.bentobox.border;

import java.util.Optional;

public enum BorderType {
    Barrier((byte) 1, "barrier"),
    Vanilla((byte) 2, "vanilla");
    // Virtual((byte)3, "virtual"); // not supported yet

    private final byte id;
    private final String commandLabel;

    BorderType(byte id, String commandLabel) {
        this.id = id;
        this.commandLabel = commandLabel;
    }

    public byte getId() {
        return id;
    }

    public String getCommandLabel() {
        return commandLabel;
    }

    /**
     * Gets the border type based on the label given
     * @param label label to check
     * @return BorderType or empty
     */
    public static Optional<BorderType> fromCommandLabel(String label) {
        for (var bt : BorderType.values())
            if (bt.commandLabel.equalsIgnoreCase(label))
                return Optional.of(bt);

        return Optional.empty();
    }

    /**
     * Gets the border type from the id byte
     * @param id byte indicating the BorderType
     * @return BorderType or empty if id is not recognized
     */
    public static Optional<BorderType> fromId(byte id) {
        for (var bt : BorderType.values())
            if (bt.getId() == id)
                return Optional.of(bt);

        return Optional.empty();
    }
}

package world.bentobox.border;

import java.util.Optional;

public enum BorderType {
    BARRIER((byte) 1, "barrier"),
    VANILLA((byte) 2, "vanilla"),
    BOTH((byte)3, "both"); // Vanilla and barrier at the same time

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

    public static Optional<BorderType> fromCommandLabel(String label) {
        for (var bt : BorderType.values())
            if (bt.commandLabel.equalsIgnoreCase(label))
                return Optional.of(bt);

        return Optional.empty();
    }

    public static Optional<BorderType> fromId(byte id) {
        for (var bt : BorderType.values())
            if (bt.getId() == id)
                return Optional.of(bt);

        return Optional.empty();
    }
}

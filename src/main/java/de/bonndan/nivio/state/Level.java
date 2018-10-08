package de.bonndan.nivio.state;

public enum Level {

    UNKNOWN(-1),
    OK(0),
    WARNING(1),
    ERROR(2);

    private final int level;

    Level(int i) {
        level = i;
    }

    public boolean isHigherThan(Level level) {
        return this.level > level.level;
    }
}

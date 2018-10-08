package de.bonndan.nivio.state;

public class ServiceState {

    private Level level;
    private String message;

    public ServiceState(final Level level, final String message) {
        this.level = level;
        this.message = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}

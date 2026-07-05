package io.atlas.kv.cli;

public record CommandResult(
        boolean success,
        boolean shouldExit,
        String message
) {

    public static CommandResult success(String message) {
        return new CommandResult(true, false, message);
    }

    public static CommandResult failure(String message) {
        return new CommandResult(false, false, message);
    }

    public static CommandResult exit(String message) {
        return new CommandResult(true, true, message);
    }
}
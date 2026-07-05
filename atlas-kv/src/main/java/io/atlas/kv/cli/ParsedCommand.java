package io.atlas.kv.cli;

import java.util.List;

public record ParsedCommand(
        CommandType type,
        List<String> arguments,
        String errorMessage
) {

    public boolean isValid() {
        return type != CommandType.INVALID;
    }

    public static ParsedCommand invalid(String message) {
        return new ParsedCommand(
                CommandType.INVALID,
                List.of(),
                message
        );
    }
}
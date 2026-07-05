package io.atlas.kv.cli;

import java.util.Arrays;
import java.util.List;

public class CommandParser {

    public ParsedCommand parse(String input) {

        if (input == null || input.isBlank()) {
            return ParsedCommand.invalid("Command cannot be empty.");
        }

        String[] tokens = input.trim().split("\\s+");

        String command = tokens[0].toLowerCase();

        return switch (command) {

            case "put" -> parsePut(tokens);

            case "get" -> parseSingleArg(CommandType.GET, tokens);

            case "delete" -> parseSingleArg(CommandType.DELETE, tokens);

            case "exists" -> parseSingleArg(CommandType.EXISTS, tokens);

            case "size" -> parseNoArgs(CommandType.SIZE, tokens);

            case "help" -> parseNoArgs(CommandType.HELP, tokens);

            case "exit" -> parseNoArgs(CommandType.EXIT, tokens);

            default -> ParsedCommand.invalid("Unknown command: " + command);
        };
    }

    private ParsedCommand parsePut(String[] tokens) {

        if (tokens.length != 3) {
            return ParsedCommand.invalid("Usage: put <key> <value>");
        }

        return new ParsedCommand(CommandType.PUT, Arrays.asList(tokens[1], tokens[2]), null);
    }

    private ParsedCommand parseSingleArg(CommandType type, String[] tokens) {

        if (tokens.length != 2) {
            return ParsedCommand.invalid("Usage: " + type.name().toLowerCase() + " <key>");
        }

        return new ParsedCommand(type, List.of(tokens[1]), null);
    }

    private ParsedCommand parseNoArgs(CommandType type, String[] tokens) {

        if (tokens.length != 1) {
            return ParsedCommand.invalid("Command takes no arguments.");
        }

        return new ParsedCommand(type, List.of(), null);
    }
}
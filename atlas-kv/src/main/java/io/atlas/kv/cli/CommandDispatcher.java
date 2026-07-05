package io.atlas.kv.cli;

import io.atlas.kv.engine.KeyValueEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CommandDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(CommandDispatcher.class);

    private final KeyValueEngine engine;

    public CommandDispatcher(KeyValueEngine engine) {
        this.engine = engine;
    }

    public CommandResult dispatch(ParsedCommand command) {

        if (!command.isValid()) {
            LOG.warn("Invalid command: {}", command.errorMessage());
            return CommandResult.failure(command.errorMessage());
        }

        return switch (command.type()) {

            case PUT -> put(command);

            case GET -> get(command);

            case DELETE -> delete(command);

            case EXISTS -> exists(command);

            case SIZE -> size();

            case HELP -> help();

            case EXIT -> exit();

            case INVALID -> CommandResult.failure(command.errorMessage());
        };
    }

    private byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private String string(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private CommandResult put(ParsedCommand command) {
        String key = command.arguments().get(0);
        String value = command.arguments().get(1);

        engine.put(key, value.getBytes(StandardCharsets.UTF_8));

        LOG.debug("Stored key '{}'", key);

        return CommandResult.success("OK");
    }

    private CommandResult get(ParsedCommand command) {
        String key = command.arguments().get(0);

        Optional<byte[]> value = engine.get(key);

        if (value.isEmpty()) {
            return CommandResult.failure("Key not found.");
        }

        return CommandResult.success(new String(value.get(), StandardCharsets.UTF_8));
    }

    private CommandResult delete(ParsedCommand command) {
        String key = command.arguments().get(0);

        boolean deleted = engine.delete(key);

        return deleted ? CommandResult.success("Deleted.") : CommandResult.failure("Key not found.");
    }

    private CommandResult exists(ParsedCommand command) {
        String key = command.arguments().get(0);

        return CommandResult.success(Boolean.toString(engine.exists(key)));
    }

    private CommandResult size() {
        return CommandResult.success(Long.toString(engine.size()));
    }

    private CommandResult help() {

        return CommandResult.success("""
                Available commands:
                
                put <key> <value>
                get <key>
                delete <key>
                exists <key>
                size
                help
                exit
                """);
    }

    private CommandResult exit() {

        LOG.info("Exit requested.");

        return CommandResult.exit("Goodbye!");
    }
}
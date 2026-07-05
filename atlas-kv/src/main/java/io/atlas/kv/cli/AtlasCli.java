package io.atlas.kv.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public class AtlasCli {
    private final CommandParser parser;
    private final CommandDispatcher dispatcher;
    private BufferedReader reader;
    private PrintStream out;

    public AtlasCli(BufferedReader reader, PrintStream out, CommandParser parser, CommandDispatcher dispatcher) {

        this.reader = reader;
        this.out = out;
        this.parser = parser;
        this.dispatcher = dispatcher;
    }


    public void run() throws IOException {

        printBanner();

        while (true) {
            try {
                out.print("atlas> ");

                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                ParsedCommand command = parser.parse(line);
                CommandResult result = dispatcher.dispatch(command);

                out.println(result.message());

                if (result.shouldExit()) {
                    break;
                }
            } catch (Exception ex) {
                out.println("Unexpected error. Check logs.");
            }
        }
    }

    private void printBanner() {
        out.println("""
            ==================================
             Atlas KV
             In-Memory Key Value Store
            ==================================
            
            Type 'help' for available commands.
            """
        );
    }
}

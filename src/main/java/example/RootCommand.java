package example;

import picocli.CommandLine.Command;

@Command(
    subcommands = {
        Hash.class,
        Zip.class
    }
)
public class RootCommand {
}

package example;

import picocli.CommandLine.Command;

@Command(
    subcommands = {
        Hash.class    
    }
)
public class RootCommand {
}

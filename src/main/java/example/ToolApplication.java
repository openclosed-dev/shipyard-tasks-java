package example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import picocli.CommandLine;
import picocli.CommandLine.IFactory;

@SpringBootApplication
public class ToolApplication implements CommandLineRunner, ExitCodeGenerator {

    private final IFactory factory;

    private int exitCode;

    public ToolApplication(IFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run(String... args) throws Exception {
        var rootCommand = new RootCommand();
        this.exitCode = new CommandLine(rootCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return this.exitCode;
    }

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class, args);
    }
}

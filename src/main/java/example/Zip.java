package example;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "zip"
)
public class Zip implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(Hash.class);

    @Option(
        names = { "-o", "--output" },
        defaultValue = "all.zip"
    )
    public Path output;

    @Option(
        names = { "--source-dir" },
        defaultValue = "."
    )
    public Path sourceDir;

    @Override
    public Integer call() throws Exception {

        var tempPath = Files.createTempFile(null, null);

        try (var outStream = Files.newOutputStream(tempPath);
             var zipStream = new ZipOutputStream(outStream)) {

            Files.walk(this.sourceDir)
                .filter(Files::isRegularFile)
                .forEach(source -> addFileToZip(zipStream, source));
        }

        Files.move(tempPath, this.output, StandardCopyOption.REPLACE_EXISTING);

        return 0;
    }

    private void addFileToZip(ZipOutputStream zipStream, Path source) {
        var entryName = this.sourceDir.relativize(source).toString();
        LOG.info("Adding zip entry: " + entryName);

        var entry = new ZipEntry(entryName);
        try {
            zipStream.putNextEntry(entry);
            try (var inputStream = Files.newInputStream(source)) {
                inputStream.transferTo(zipStream);
            }
            zipStream.closeEntry();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

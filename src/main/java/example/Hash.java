package example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "hash"
)
public class Hash implements Callable<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(Hash.class);

    private static final int BUFFER_SIZE = 128 * 1024;

    @Option(
        names = { "--algorithm" },
        description = "Algorithm used for calculating hash",
        defaultValue = "MD5"
    )
    public String algorithm;

    private byte[] buffer;
    private MessageDigest digest;
    private String hashFileExtension;

    @Override
    public Integer call() throws Exception {
        this.digest = MessageDigest.getInstance(this.algorithm);
        this.hashFileExtension = getHashFileExtension(this.algorithm);
        this.buffer = new byte[BUFFER_SIZE];

        var currentDir = Path.of(".");
        var files = Files.walk(currentDir).filter(Files::isRegularFile).toList();
        for (var file : files) {
            generateHashFileFrom(file);
        }

        return 0;
    }

    private void generateHashFileFrom(Path path) throws IOException {
        LOG.info("Generating hash file for: " + path);
        var calculated = calculateFileDigestString(path);
        var hashFilePath = toHashFilePath(path);
        Files.writeString(hashFilePath, calculated);
        LOG.info("Generated hash file: " + hashFilePath);
    }

    private String calculateFileDigestString(Path path) throws IOException {
        byte[] digest = calculateFileDigest(path);
        return HexFormat.of().formatHex(digest);
    }

    private byte[] calculateFileDigest(Path path) throws IOException {
        byte[] result;
        try (var in = Files.newInputStream(path)) {
            int bytesRead;
            while ((bytesRead = in.read(this.buffer)) > 0) {
                digest.update(this.buffer, 0, bytesRead);
            }
        } finally {
            result = digest.digest();
        }
        return result;
    }

    private Path toHashFilePath(Path path) {
        return Path.of(path.toString() + this.hashFileExtension);
    }

    private static String getHashFileExtension(String algorithm) {
        return "." + algorithm.replace("-", "").toLowerCase();
    }
}

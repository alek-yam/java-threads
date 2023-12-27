package example_12_10.page;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ByteStreamReader implements PageReader {

    private final ExecutorService executor;

    public ByteStreamReader(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public String read(URL url) {
        return null;
    }

    @Override
    public CompletableFuture<String> readAsync(URL url) {
        return CompletableFuture.supplyAsync(() -> {
            try(var byteSteam = url.openStream()) {
                var bytes = byteSteam.readAllBytes();
                return new String(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }
}

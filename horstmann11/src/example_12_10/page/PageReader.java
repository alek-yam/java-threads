package example_12_10.page;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface PageReader {

    String read(URL url);

    CompletableFuture<String> readAsync(URL url);

}

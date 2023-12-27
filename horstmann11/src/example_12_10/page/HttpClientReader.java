package example_12_10.page;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpClientReader implements PageReader {

    private final HttpClient httpClient;

    public HttpClientReader(ExecutorService executor) {
        this.httpClient = HttpClient.newBuilder().executor(executor).build();
    }

    @Override
    public String read(URL url) {
        System.out.println("read()");
        HttpRequest httpRequest = buildHttpRequest(url);
        try {
            HttpResponse<String> resp = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("Got http response " + resp.statusCode());
            return resp.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<String> readAsync(URL url) {
        System.out.println("readAsync()");
        HttpRequest httpRequest = buildHttpRequest(url);
        System.out.println("Sending http request...");
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
            .thenApply(resp -> {
                System.out.println("Got http response " + resp.statusCode());
                return resp.body();
            })
            .orTimeout(3, TimeUnit.SECONDS);
    }

    private HttpRequest buildHttpRequest(URL url) {
        try {
            return HttpRequest.newBuilder(url.toURI())
                .GET()
                .timeout(Duration.ofSeconds(2))
                .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot generate HTTP request", e);
        }
    }
}

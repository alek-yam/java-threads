package example_12_10;

import example_12_10.page.HttpClientReader;
import example_12_10.page.PageReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureDemo {

    private static final String HORSTMANN_URL = "http://horstmann.com/index.html";
    private static final String MARTIN_URL = "https://ukmartin.ru";

    public static void main(String[] arg) throws MalformedURLException {
        ExecutorService executor = Executors.newCachedThreadPool();
        PageReader pageReader = new HttpClientReader(executor);
        ImageGrabber imageGrabber = new ImageGrabber(pageReader, executor);
        imageGrabber.fromPage(new URL(MARTIN_URL))
            .thenRun(executor::shutdown);
    }

}

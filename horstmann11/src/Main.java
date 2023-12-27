import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("Hello World!!!");
            return "XXX";
        }).thenAccept(result -> {
            System.out.println("Result: " + result);
        });
        // видимо потоки, используемые в CompletableFuture - демоны
        // т.е. они завершаются при завершении программы
        // поэтому приходится отложить завершение программы, чтобы параллельный поток-демон успел выполнить код из thenAccept
        sleep(100);
    }
}
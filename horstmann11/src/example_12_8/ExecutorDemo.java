package example_12_8;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExecutorDemo {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (var in = new Scanner(System.in)) {
            String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
            System.out.println("Current directory: " + currentDir);

            System.out.print("Enter base directory (current by default): ");
            String startDir = in.nextLine();

            if (startDir.isBlank()) {
                startDir = currentDir;
            }

            System.out.print("Enter key word: ");
            String keyWord = in.nextLine();

            Set<Path> files = getDescendants(startDir);
            System.out.println("Searching in files: ");
            files.forEach(System.out::println);

            var tasks = new ArrayList<Callable<Long>>();
            for (Path file : files) {
                Callable<Long> task = () -> findOccurrences(keyWord, file);
                tasks.add(task);
            }

            ExecutorService executor = Executors.newCachedThreadPool();

            Instant startTime = Instant.now();
            List<Future<Long>> results = executor.invokeAll(tasks);
            long totalCount = results.stream().mapToLong(ExecutorDemo::getResultWrapper).sum();
            Instant endTime = Instant.now();
            System.out.println("Occurrences of \"" + keyWord + "\" word: " + totalCount);
            System.out.println("Time elapsed: " + Duration.between(startTime, endTime).toMillis() + " ms");

            if (executor instanceof ThreadPoolExecutor) {
                int lgPoolSize = ((ThreadPoolExecutor)executor).getLargestPoolSize();
                System.out.println("Largest pool size: " + lgPoolSize);
            }

            executor.shutdown();
        }
    }

    /**
     * Возвращает все каталоги, порождённые заданным каталогом
     */
    private static Set<Path> getDescendants(String rootDir) throws IOException {
        Path rootPath = Path.of(rootDir);
        try (Stream<Path> paths = Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".java"))
                .collect(Collectors.toSet());
        }
    }

    /**
     * Подсчитывает количество вхождений указанного слова в заданном файле
     */
    private static long findOccurrences(String word, Path file) {
        try (var in = new Scanner(file)) {
            long count = 0;
            while (in.hasNext()) {
                if (in.next().equals(word)) {
                    count++;
                }
            }
            return count;
        } catch (IOException ex) {
            return 0;
        }
    }

    private static Long getResultWrapper(Future<Long> result) {
        try {
            return result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

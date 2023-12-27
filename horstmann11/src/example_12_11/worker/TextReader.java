package example_12_11.worker;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TextReader extends SwingWorker<StringBuilder, ProgressData> {
    private static final String END_OF_LINE = "\n";

    private final SwingWorkerFrame frame;
    private final File file;

    public TextReader(SwingWorkerFrame frame, File file) {
        this.frame = frame;
        this.file = file;
    }

    /**
     * Выполняется в рабочем потоке выполняющим чтение файла,
     * не затрагивающим компоненты Swing
     */
    @Override
    protected StringBuilder doInBackground() throws Exception {
        var text = new StringBuilder();
        int lineNumber = 0;
        try (var in = new Scanner(new FileInputStream(file))) {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                lineNumber++;
                text.append(line).append(END_OF_LINE);
                var progressData = new ProgressData(lineNumber, line);
                publish(progressData);    // направляет данные о ходе выполнения задачи в поток диспетчеризации Swing
                Thread.sleep(1);    // только для проверки отмены, не требуется в реальных программах
            }
        }
        return text;
    }

    /**
     * Выполняется в потоке диспетчеризации событий Swing
     */
    @Override
    protected void process(List<ProgressData> dataChunks) {
        if (isCancelled()) return;
        var lastChunk = dataChunks.get(dataChunks.size() - 1);
        var status = String.valueOf(lastChunk.getNumber());
        String text = dataChunks.stream()
            .map(ProgressData::getLine)
            .collect(Collectors.joining(END_OF_LINE));
        frame.updateProgress(text, status);
    }

    @Override
    protected void done() {
        try {
            StringBuilder result = get();
            frame.finishLoad(result.toString(), "Done");
        } catch (ExecutionException e) {
            frame.finishLoad("ExecutionException: " + e, "Error");
        } catch (CancellationException e) {
            frame.finishLoad("", "Canceled");
        } catch (InterruptedException e) {
            frame.finishLoad("InterruptedException: " + e, "Error");
        }
    }
}

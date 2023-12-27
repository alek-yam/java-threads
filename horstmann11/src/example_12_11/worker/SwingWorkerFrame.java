package example_12_11.worker;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Этот фрейм содержит текстовую область для отображения содержимого текстового файла,
 * меню для открытия файла и отмены его открытия,
 * а также строку состояния для отображения процесса загрузки файла
 */
public class SwingWorkerFrame extends JFrame {
    private static final int TEXT_ROWS = 20;
    private static final int TEXT_COLUMNS = 60;

    private final JTextArea textArea;
    private final JLabel statusLine;
    private final JMenuItem openItem;
    private final JMenuItem cancelItem;
    private SwingWorker<StringBuilder, ProgressData> textReader;

    public SwingWorkerFrame() throws HeadlessException {
        var fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));

        textArea = new JTextArea(TEXT_ROWS, TEXT_COLUMNS);
        add(new JScrollPane(textArea));

        statusLine = new JLabel(" ");
        add(statusLine, BorderLayout.SOUTH);

        // формируем меню File
        var menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        var menu = new JMenu("File");
        menuBar.add(menu);
        openItem = new JMenuItem("Open");
        menu.add(openItem);
        cancelItem = new JMenuItem("Cancel");
        cancelItem.setEnabled(false);
        menu.add(cancelItem);

        openItem.addActionListener(event -> {
            // показать диалоговое окно выбора файла
            int result = fileChooser.showOpenDialog(null);

            // если файл выбран, вывести его содержимое на экран
            if (result == JFileChooser.APPROVE_OPTION) {
                textArea.setText("");
                openItem.setEnabled(false);
                textReader = new TextReader(this, fileChooser.getSelectedFile());
                textReader.execute();
                cancelItem.setEnabled(true);
            }
        });

        cancelItem.addActionListener(event -> {
            if (textReader != null) {
                textReader.cancel(true);
            }
        });

        pack();
    }

    public void updateProgress(String text, String status) {
        textArea.append(text);
        statusLine.setText(status);
    }

    public void finishLoad(String text, String status) {
        textArea.setText(text);
        statusLine.setText(status);
        cancelItem.setEnabled(false);
        openItem.setEnabled(true);
    }

    public static void start() {
        EventQueue.invokeLater(() -> {
            var frame = new SwingWorkerFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

}

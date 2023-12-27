package example_12_11;

import example_12_11.worker.SwingWorkerFrame;

/**
 * В этой программе демонстрируется рабочий поток,
 * в котором выполняется потенциально продолжительная задача
 */
public class SwingWorkerTest {

    public static void main(String[] args) {
        SwingWorkerFrame.start();
    }

}

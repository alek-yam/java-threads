package example_12_11.worker;

public class ProgressData {
    private final int number;
    private final String line;

    public ProgressData(int number, String line) {
        this.number = number;
        this.line = line;
    }

    public int getNumber() {
        return number;
    }

    public String getLine() {
        return line;
    }
}

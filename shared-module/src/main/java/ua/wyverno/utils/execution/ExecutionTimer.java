package ua.wyverno.utils.execution;

public class ExecutionTimer {
    private long startTime;
    private long durationMs;

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void end() {
        long endTime = System.currentTimeMillis();

        this.durationMs = endTime - this.startTime;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public long getHours() {
        return this.durationMs / 3_600_000; // Переведення мс в години
    }

    public long getMinutes() {
        return (this.durationMs % 3_600_000) / 60000; // Останні хвилини
    }

    public long getSeconds() {
        return (this.durationMs % 60_000) / 1000; // Останні секунди
    }

    public String getFormattedDuration() {
        long hours = this.getHours();
        long minutes = this.getMinutes();
        long seconds = this.getSeconds();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public String getDetailFormattedDuration() {
        long hours = this.getHours();
        long minutes = this.getMinutes();
        long seconds = this.getSeconds();

        return String.format("%02d hours, %02d minutes, %02d seconds", hours, minutes, seconds);
    }
}

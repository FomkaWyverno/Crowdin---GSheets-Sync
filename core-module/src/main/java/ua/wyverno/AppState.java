package ua.wyverno;

import org.springframework.stereotype.Component;

@Component
public class AppState {
    private boolean isRunning = true;

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        this.isRunning = false;
    }
}

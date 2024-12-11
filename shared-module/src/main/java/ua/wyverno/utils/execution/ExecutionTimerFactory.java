package ua.wyverno.utils.execution;

import org.springframework.stereotype.Component;

@Component
public class ExecutionTimerFactory {
    public ExecutionTimer createTimer() {
        return new ExecutionTimer();
    }
}

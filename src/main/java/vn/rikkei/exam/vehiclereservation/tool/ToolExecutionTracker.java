package vn.rikkei.exam.vehiclereservation.tool;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ToolExecutionTracker {

    private final ThreadLocal<List<String>> executedTools =
            ThreadLocal.withInitial(ArrayList::new);

    public void start() {
        executedTools.get().clear();
    }

    public void record(String toolName) {
        executedTools.get().add(toolName);
    }

    public List<String> getToolsUsed() {
        return List.copyOf(executedTools.get());
    }

    public void clear() {
        executedTools.remove();
    }
}
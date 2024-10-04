import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final List<Task> history = new LinkedList<>();
    public static final int SIZE_HISTORY = 10;

    @Override
    public void add(Task task) {
        if (history.size() == SIZE_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}

import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    private final Set<Integer> subtaskIds = new HashSet<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id) {
        super(name, description, Status.IN_PROGRESS, id);
    }

    public Set<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }
}

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskManager {
    private int id;
    private final HashMap<Integer, Task> taskHashMap;
    private final HashMap<Integer, Subtask> subtaskHashMap;
    private final HashMap<Integer, Epic> epicHashMap;

    public TaskManager() {
        id = 0;
        taskHashMap = new HashMap<>();
        subtaskHashMap = new HashMap<>();
        epicHashMap = new HashMap<>();
    }

    // Получение списка всех задач
    public HashMap<Integer, Task> getTasks() {
        return taskHashMap;
    }

    // Получение списка всех подзадач
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtaskHashMap;
    }

    // Получение списка всех эпиков
    public HashMap<Integer, Epic> getEpics() {
        return epicHashMap;
    }

    // Удаление всех задач
    public void deleteAllTasks() {
        taskHashMap.clear();
    }

    // Удаление всех подзадач
    public void deleteAllSubtasks() {
        subtaskHashMap.clear();
    }

    // Удаление всех эпиков
    public void deleteAllEpics() {
        epicHashMap.clear();
        deleteAllSubtasks();
    }

    // Получение задачи по идентификатору
    public Task getTask(int id) {
        return taskHashMap.get(id);
    }

    // Получение подзадачи по идентификатору
    public Subtask getSubtask(int id) {
        return subtaskHashMap.get(id);
    }

    // Получение эпика по идентификатору
    public Epic getEpic(int id) {
        return epicHashMap.get(id);
    }

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    public void createTask(Task task) {
        task.setId(id);
        task.setStatus(Status.NEW);
        taskHashMap.put(id, task);
        id++;
    }

    // Создание подзадачи. Сам объект должен передаваться в качестве параметра.
    public void createSubtask(Subtask subtask) {
        subtask.setId(id);
        subtask.setStatus(Status.NEW);
        subtaskHashMap.put(id, subtask);
        // Передать id подзадачи в эпик
        epicHashMap.get(subtask.getEpicId()).addSubtaskId(id);
        id++;
    }

    // Создание эпика. Сам объект должен передаваться в качестве параметра.
    public void createEpic(Epic epic) {
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epicHashMap.put(id, epic);
        id++;
    }

    // Обновление задачи. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    // Обновление подзадачи. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateSubtask(Subtask subtask) {
        subtaskHashMap.put(subtask.getId(), subtask);
        // Обновить статус эпика
        int epicId = subtask.getEpicId();
        updateEpicStatus(epicId);
    }

    // Метод для обновления статуса эпика
    private void updateEpicStatus(int epicId) {
        Set<Integer> subtaskIds = getAllSubtaskIdsOfEpic(epicId);
        Set<Status> statuses = new HashSet<>();
        for (int id : subtaskIds) {
            statuses.add(subtaskHashMap.get(id).getStatus());
        }
        if (statuses.isEmpty()) {
            epicHashMap.get(epicId).setStatus(Status.NEW);
        } else if (!statuses.contains(Status.DONE) && !statuses.contains(Status.IN_PROGRESS)) {
            epicHashMap.get(epicId).setStatus(Status.NEW);
        } else if (!statuses.contains(Status.NEW) && !statuses.contains(Status.IN_PROGRESS)) {
            epicHashMap.get(epicId).setStatus(Status.DONE);
        } else {
            epicHashMap.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

    // Обновление эпика. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
        // Обновить статус эпика
        updateEpicStatus(epic.getId());
    }

    // Удаление задачи по идентификатору
    public void deleteTask(int id) {
        taskHashMap.remove(id);
    }

    // Удаление подзадачи по идентификатору
    public void deleteSubtask(int id) {
        // Удалить id подзадачи из эпика
        int epicId = subtaskHashMap.get(id).getEpicId();
        getAllSubtaskIdsOfEpic(epicId).remove(id);
        subtaskHashMap.remove(id);
        updateEpicStatus(epicId);
    }

    // Удаление эпика по идентификатору
    public void deleteEpic(int id) {
        Set<Integer> subtaskIds = getAllSubtaskIdsOfEpic(id);
        for (int subtaskId : subtaskIds) {
            subtaskHashMap.remove(subtaskId);
        }
        epicHashMap.remove(id);
    }

    // Получение списка всех подзадач определённого эпика
    public Set<Integer> getAllSubtaskIdsOfEpic(Epic epic) {
        return epic.getSubtaskIds();
    }

    // Получение списка всех подзадач определённого эпика
    public Set<Integer> getAllSubtaskIdsOfEpic(int epicId) {
        return epicHashMap.get(epicId).getSubtaskIds();
    }
}

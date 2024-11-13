import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final Map<Integer, Task> taskHashMap;
    private final Map<Integer, Subtask> subtaskHashMap;
    private final Map<Integer, Epic> epicHashMap;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        id = 0;
        taskHashMap = new HashMap<>();
        subtaskHashMap = new HashMap<>();
        epicHashMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    protected void loadTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    protected void loadSubtask(Subtask subtask) {
        subtaskHashMap.put(subtask.getId(), subtask);
    }

    protected void loadEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    // Получение списка всех задач
    @Override
    public List<Task> getTasks() {
        return taskHashMap.values().stream().toList();
    }

    // Получение списка всех подзадач
    @Override
    public List<Subtask> getSubtasks() {
        return subtaskHashMap.values().stream().toList();
    }

    // Получение списка всех эпиков
    @Override
    public List<Epic> getEpics() {
        return epicHashMap.values().stream().toList();
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        taskHashMap.keySet().forEach(historyManager::remove);
        taskHashMap.clear();
    }

    // Удаление всех подзадач
    @Override
    public void deleteAllSubtasks() {
        subtaskHashMap.keySet().forEach(historyManager::remove);
        subtaskHashMap.clear();
        for (int id : epicHashMap.keySet()) {
            epicHashMap.get(id).getSubtaskIds().clear();
        }
    }

    // Удаление всех эпиков
    @Override
    public void deleteAllEpics() {
        epicHashMap.keySet().forEach(historyManager::remove);
        epicHashMap.clear();
        deleteAllSubtasks();
    }

    // Получение задачи по идентификатору
    @Override
    public Task getTask(int id) {
        Task task = taskHashMap.get(id);
        historyManager.add(task);
        return task;
    }

    // Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    // Получение эпика по идентификатору
    @Override
    public Epic getEpic(int id) {
        Epic epic = epicHashMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createTask(Task task) {
        task.setId(id);
        task.setStatus(Status.NEW);
        taskHashMap.put(id, task);
        id++;
    }

    // Создание подзадачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(id);
        subtask.setStatus(Status.NEW);
        subtaskHashMap.put(id, subtask);
        // Передать id подзадачи в эпик
        epicHashMap.get(subtask.getEpicId()).addSubtaskId(id);
        id++;
    }

    // Создание эпика. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createEpic(Epic epic) {
        epic.setId(id);
        epic.setStatus(Status.NEW);
        epicHashMap.put(id, epic);
        id++;
    }

    // Обновление задачи. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    // Обновление подзадачи. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
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
        } else if (statuses.contains(Status.IN_PROGRESS)) {
            epicHashMap.get(epicId).setStatus(Status.IN_PROGRESS);
        } else if (!statuses.contains(Status.DONE)) {
            epicHashMap.get(epicId).setStatus(Status.NEW);
        } else {
            epicHashMap.get(epicId).setStatus(Status.DONE);
        }
    }

    // Обновление эпика. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    @Override
    public void updateEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
        // Обновить статус эпика
        updateEpicStatus(epic.getId());
    }

    // Удаление задачи по идентификатору
    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        taskHashMap.remove(id);
    }

    // Удаление подзадачи по идентификатору
    @Override
    public void deleteSubtask(int id) {
        // Удалить id подзадачи из эпика
        int epicId = subtaskHashMap.get(id).getEpicId();
        getAllSubtaskIdsOfEpic(epicId).remove(id);
        historyManager.remove(id);
        subtaskHashMap.remove(id);
        updateEpicStatus(epicId);
    }

    // Удаление эпика по идентификатору
    @Override
    public void deleteEpic(int id) {
        Set<Integer> subtaskIds = getAllSubtaskIdsOfEpic(id);
        for (int subtaskId : subtaskIds) {
            historyManager.remove(subtaskId);
            subtaskHashMap.remove(subtaskId);
        }
        historyManager.remove(id);
        epicHashMap.remove(id);
    }

    // Получение списка всех подзадач определённого эпика
    private Set<Integer> getAllSubtaskIdsOfEpic(int epicId) {
        return epicHashMap.get(epicId).getSubtaskIds();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}

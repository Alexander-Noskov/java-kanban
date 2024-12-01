package manager;

import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;
import exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final Map<Integer, Task> taskHashMap;
    private final Map<Integer, Subtask> subtaskHashMap;
    private final Map<Integer, Epic> epicHashMap;
    private final HistoryManager historyManager;
    private final TreeMap<LocalDateTime, Task> prioritizedTasks;

    public InMemoryTaskManager() {
        id = 1;
        taskHashMap = new HashMap<>();
        subtaskHashMap = new HashMap<>();
        epicHashMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeMap<>();
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks.values());
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
        taskHashMap.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(task -> prioritizedTasks.remove(task.getStartTime()));
        taskHashMap.keySet().forEach(historyManager::remove);
        taskHashMap.clear();
    }

    // Удаление всех подзадач
    @Override
    public void deleteAllSubtasks() {
        subtaskHashMap.values().stream()
                .filter(task -> task.getStartTime() != null)
                .forEach(task -> prioritizedTasks.remove(task.getStartTime()));
        subtaskHashMap.keySet().forEach(historyManager::remove);
        subtaskHashMap.clear();
        epicHashMap.keySet().forEach(id -> epicHashMap.get(id).getSubtaskIds().clear());
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
        if (task == null) {
            throw new NotFoundException("entity.Task with id " + id + " not found");
        }
        historyManager.add(task);
        return task;
    }

    // Получение подзадачи по идентификатору
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        if (subtask == null) {
            throw new NotFoundException("entity.Subtask with id " + id + " not found");
        }
        historyManager.add(subtask);
        return subtask;
    }

    // Получение эпика по идентификатору
    @Override
    public Epic getEpic(int id) {
        Epic epic = epicHashMap.get(id);
        if (epic == null) {
            throw new NotFoundException("entity.Epic with id " + id + " not found");
        }
        historyManager.add(epic);
        return epic;
    }

    private void addTaskToPrioritizedTask(Task task) throws IllegalArgumentException {
        if (task.getStartTime() != null) {
            boolean isValid = prioritizedTasks.values().stream()
                    .map(t -> isValidTime(t, task))
                    .reduce(true, Boolean::logicalAnd);

            if (isValid) {
                prioritizedTasks.put(task.getStartTime(), task);
            } else {
                throw new IllegalArgumentException("Время задачи пересекается с существующей");
            }
        }
    }

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createTask(Task task) {
        Task newTask = new Task(task.getName(), task.getDescription());
        newTask.setId(id);
        newTask.setStatus(Status.NEW);
        newTask.setStartTime(task.getStartTime());
        newTask.setDuration(task.getDuration());
        addTaskToPrioritizedTask(newTask);
        taskHashMap.put(id, newTask);
        id++;
    }

    // Создание подзадачи. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask.getEpicId() == 0) {
            throw new NotFoundException("entity.Epic not found");
        }
        Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        newSubtask.setId(id);
        newSubtask.setStatus(Status.NEW);
        newSubtask.setStartTime(subtask.getStartTime());
        newSubtask.setDuration(subtask.getDuration());
        addTaskToPrioritizedTask(newSubtask);
        subtaskHashMap.put(id, newSubtask);
        // Передать id подзадачи в эпик
        epicHashMap.get(newSubtask.getEpicId()).addSubtaskId(id);
        updateEpicTimes(newSubtask.getEpicId());
        id++;
    }

    // Создание эпика. Сам объект должен передаваться в качестве параметра.
    @Override
    public void createEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription());
        newEpic.setId(id);
        newEpic.setStatus(Status.NEW);
        epicHashMap.put(id, newEpic);
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
        updateEpicTimes(epicId);
    }

    private boolean isValidTime(Task task1, Task task2) {
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            if (task1.getStartTime().isBefore(task2.getStartTime())) {
                return task1.getEndTime().isBefore(task2.getStartTime());
            } else if (task2.getStartTime().isBefore(task1.getStartTime())) {
                return task2.getEndTime().isBefore(task1.getStartTime());
            }
        }
        return false;
    }

    // Метод для обновления продолжительности, времени старта и окончания эпика
    private void updateEpicTimes(int epicId) {
        Set<Integer> subtaskIds = getAllSubtaskIdsOfEpic(epicId);
        Set<Subtask> subtasks = new HashSet<>();
        Epic epic = epicHashMap.get(epicId);

        subtaskIds.forEach(id -> subtasks.add(subtaskHashMap.get(id)));

        subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .ifPresentOrElse(
                        epic::setStartTime,
                        () -> epic.setStartTime(null)
                )
        ;

        subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresentOrElse(
                        epic::setEndTime,
                        () -> epic.setEndTime(null)
                )
        ;

        long durations = subtasks.stream()
                .map(Task::getDuration)
                .reduce(0L, Long::sum);

        epic.setDuration(durations);
    }

    // Метод для обновления статуса эпика
    private void updateEpicStatus(int epicId) {
        Set<Integer> subtaskIds = getAllSubtaskIdsOfEpic(epicId);
        Set<Status> statuses = new HashSet<>();

        subtaskIds.forEach(id -> statuses.add(subtaskHashMap.get(id).getStatus()));

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
        Task task = taskHashMap.remove(id);
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task.getStartTime());
        }

    }

    // Удаление подзадачи по идентификатору
    @Override
    public void deleteSubtask(int id) {
        // Удалить id подзадачи из эпика
        int epicId = subtaskHashMap.get(id).getEpicId();
        getAllSubtaskIdsOfEpic(epicId).remove(id);
        historyManager.remove(id);
        Subtask subtask = subtaskHashMap.remove(id);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.remove(subtask.getStartTime());
        }
        updateEpicStatus(epicId);
        updateEpicTimes(epicId);
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

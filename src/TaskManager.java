import java.util.List;

public interface TaskManager {

    // Получение списка всех задач
    List<Task> getTasks();

    // Получение списка всех подзадач
    List<Subtask> getSubtasks();

    // Получение списка всех эпиков
    List<Epic> getEpics();

    // Удаление всех задач
    void deleteAllTasks();

    // Удаление всех подзадач
    void deleteAllSubtasks();

    // Удаление всех эпиков
    void deleteAllEpics();

    // Получение задачи по идентификатору
    Task getTask(int id);

    // Получение подзадачи по идентификатору
    Subtask getSubtask(int id);

    // Получение эпика по идентификатору
    Epic getEpic(int id);

    // Создание задачи. Сам объект должен передаваться в качестве параметра.
    void createTask(Task task);

    // Создание подзадачи. Сам объект должен передаваться в качестве параметра.
    void createSubtask(Subtask subtask);

    // Создание эпика. Сам объект должен передаваться в качестве параметра.
    void createEpic(Epic epic);

    // Обновление задачи. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);

    // Обновление подзадачи. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateSubtask(Subtask subtask);

    // Обновление эпика. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateEpic(Epic epic);

    // Удаление задачи по идентификатору
    void deleteTask(int id);

    // Удаление подзадачи по идентификатору
    void deleteSubtask(int id);

    // Удаление эпика по идентификатору
    void deleteEpic(int id);
}

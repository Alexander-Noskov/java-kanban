import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager{
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        StringBuilder resultString = new StringBuilder("id,type,name,status,description,epic" + System.lineSeparator());
        for (Task task : getTasks()) {
            resultString.append(task).append(System.lineSeparator());
        }
        for (Epic epic : getEpics()) {
            resultString.append(epic).append(System.lineSeparator());
        }
        for (Subtask subtask : getSubtasks()) {
            resultString.append(subtask).append(System.lineSeparator());
        }

        try (Writer writer = new FileWriter(file)) {
            writer.write(resultString.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        switch (TaskTypes.valueOf(parts[1])) {
            case TASK -> {
                return new Task(name, description, status, id);
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(name, description, status, id, epicId);
            }
            case EPIC -> {
                return new Epic(name, description, status, id);
            }
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = taskManager.fromString(line);
                if (task != null) {
                    if (task instanceof Subtask) {
                        taskManager.loadSubtask((Subtask) task);
                    } else if (task instanceof Epic) {
                        taskManager.loadEpic((Epic) task);
                    } else {
                        taskManager.loadTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException(e);
        }
        return taskManager;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }
}

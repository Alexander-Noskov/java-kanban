package manager;

import entity.*;
import exception.ManagerLoadException;
import exception.ManagerSaveException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        StringBuilder resultString = new StringBuilder("id,type,name,status,description,startTime,endTime,duration,epic" + System.lineSeparator());

        getTasks().forEach(task -> resultString.append(task).append(System.lineSeparator()));
        getEpics().forEach(epic -> resultString.append(epic).append(System.lineSeparator()));
        getSubtasks().forEach(subtask -> resultString.append(subtask).append(System.lineSeparator()));

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

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        try {
            startTime = LocalDateTime.parse(parts[5]);
            endTime = LocalDateTime.parse(parts[6]);
        } catch (DateTimeParseException e) {
            System.err.println(e.getMessage());
        }

        long duration = Long.parseLong(parts[7]);
        switch (TaskTypes.valueOf(parts[1])) {
            case TaskTypes.TASK -> {
                Task task = new Task(name, description, status, id);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
            }
            case TaskTypes.SUBTASK -> {
                int epicId = Integer.parseInt(parts[8]);
                Subtask subtask = new Subtask(name, description, status, id, epicId);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;
            }
            case TaskTypes.EPIC -> {
                Epic epic = new Epic(name, description, status, id);
                epic.setStartTime(startTime);
                epic.setEndTime(endTime);
                epic.setDuration(duration);
                return epic;
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

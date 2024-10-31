public class Main {
    public static void main(String[] args) {
        Task task1 = new Task("task1", "desc1");
        Task task2 = new Task("task2", "desc2");
        Epic epic1 = new Epic("epic1", "desc1");
        Epic epic2 = new Epic("epic2", "desc2");

        TaskManager manager = new InMemoryTaskManager();

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("subtask1", "desc1", 2);
        Subtask subtask2 = new Subtask("subtask2", "desc2", 2);
        Subtask subtask3 = new Subtask("subtask3", "desc3", 2);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        manager.getEpic(2);
        manager.getTask(0);
        manager.getTask(1);

        manager.getEpic(2);

        manager.getSubtask(4);
        manager.getSubtask(5);
        manager.getSubtask(6);

        manager.getHistory().forEach(System.out::println);

        System.out.println("После удаления:");

        manager.deleteTask(0);
//        manager.deleteEpic(2);
        manager.deleteSubtask(5);
        manager.getHistory().forEach(System.out::println);

    }
}

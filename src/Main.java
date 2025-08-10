public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask(new Task(0, "Task 1", TaskStatus.NEW, "Description 1"));
        Task task2 = manager.createTask(new Task(0, "Task 2", TaskStatus.NEW, "Description 2"));

        Epic epic1 = manager.createEpic(new Epic(0, "Epic 1", "Description Epic 1"));
        Epic epic2 = manager.createEpic(new Epic(0, "Epic 2", "Description Epic 2"));

        Subtask subtask1 = manager.createSubtask(
                new Subtask(0, "Subtask 1", TaskStatus.NEW, "Description Subtask 1", epic1.getId()));
        Subtask subtask2 = manager.createSubtask(
                new Subtask(0, "Subtask 2", TaskStatus.DONE, "Description Subtask 2", epic1.getId()));
        Subtask subtask3 = manager.createSubtask(
                new Subtask(0, "Subtask 3", TaskStatus.IN_PROGRESS, "Description Subtask 3", epic2.getId()));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
    }
}

import enums.TaskStatus;
import enums.TaskType;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");


        TaskManager manager = Managers.getDefault();
        //managers.HistoryManager history = managers.Managers.getDefaultHistory();
        LocalDateTime now = LocalDateTime.now();

        Task task1 = manager.createTask(new Task(
                0, TaskType.TASK,"tasks.Task 1", TaskStatus.NEW, "Description 1", Duration.ofMinutes(15), now));
        Task task2 = manager.createTask(new Task(
                0, TaskType.TASK,"tasks.Task 2", TaskStatus.NEW, "Description 2", Duration.ofMinutes(15), now.plusMinutes(15)));

        Epic epic1 = manager.createEpic(new Epic(
                0, TaskType.EPIC,"tasks.Epic 1", "Description tasks.Epic 1", Duration.ofMinutes(15), now.plusMinutes(30)));
        Epic epic2 = manager.createEpic(new Epic(
                0, TaskType.EPIC,"tasks.Epic 2", "Description tasks.Epic 2", Duration.ofMinutes(15), now.plusMinutes(45)));

        Subtask subtask1 = manager.createSubtask(
                new Subtask(0, TaskType.SUBTASK,"tasks.Subtask 1", TaskStatus.NEW,
                        "Description tasks.Subtask 1", Duration.ofMinutes(15), now.plusMinutes(60), epic1.getId()));
        Subtask subtask2 = manager.createSubtask(
                new Subtask(0, TaskType.SUBTASK,"tasks.Subtask 2", TaskStatus.DONE,
                        "Description tasks.Subtask 2", Duration.ofMinutes(15), now.plusMinutes(75), epic1.getId()));
        Subtask subtask3 = manager.createSubtask(
                new Subtask(0, TaskType.SUBTASK,"tasks.Subtask 3", TaskStatus.IN_PROGRESS,
                        "Description tasks.Subtask 3", Duration.ofMinutes(15), now.plusMinutes(90), epic2.getId()));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtasks());
        System.out.println(manager.getTaskById(1));
        System.out.println(manager.getEpicById(3));
        System.out.println(manager.getSubtaskById(2));
        System.out.println(manager.getSubtaskById(7));
        System.out.println(manager.getHistory());

        printAllTasks(manager);


    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }



        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

    }
}
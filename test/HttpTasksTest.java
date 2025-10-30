import com.google.gson.Gson;
import enums.TaskStatus;
import enums.TaskType;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTasksTest extends BaseHttpTest{

    LocalDateTime now = LocalDateTime.now();
    Task task = new Task(0, TaskType.TASK,"Test Task", TaskStatus.NEW,"Test Description",
            Duration.ofMinutes(15), now);


    @Test
    public void testGetAllTasksWhenEmpty() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void testCreateTask() throws Exception {

        Gson gson = taskServer.getGson();
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("\"id\""));

        // Проверяем, что задача добавилась в менеджер
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getName());
    }

    @Test
    public void testGetTaskById() throws Exception {

        int taskId = manager.createTask(task).getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test Task"));
    }

    @Test
    public void testGetTaskByIdNotFound() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws Exception {

        int taskId = manager.createTask(task).getId();

        // Обновляем задачу
        Task updatedTask = new Task(taskId, TaskType.TASK, "Updated Task", TaskStatus.IN_PROGRESS,"Updated Description",
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));

        Gson gson = taskServer.getGson();
        String taskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        // Проверяем, что задача обновилась
        Task taskFromManager = manager.getTaskById(taskId);
        assertEquals("Updated Task", taskFromManager.getName());
        assertEquals(TaskStatus.IN_PROGRESS, taskFromManager.getStatus());
    }

    @Test
    public void testDeleteTask() throws Exception {

        int taskId = manager.createTask(task).getId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        // Проверяем, что задача удалилась
        assertNull(manager.getTaskById(taskId));
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testDeleteAllTasks() throws Exception {
        // Сначала создаем несколько задач
        Task task1 = new Task(0, TaskType.TASK,"Task 1", TaskStatus.NEW,"Description 1",
                Duration.ofMinutes(30), now);
        Task task2 = new Task(0, TaskType.TASK, "Task 2", TaskStatus.NEW, "Description 2",
                Duration.ofMinutes(45), now.plusHours(1));

        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        // Проверяем, что все задачи удалились
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    public void testCreateTaskWithTimeConflict() throws Exception {
        // Сначала создаем задачу
        Task task1 = new Task(0, TaskType.TASK,"Task 1", TaskStatus.NEW, "Description 1",
                Duration.ofMinutes(60), now);
        manager.createTask(task1);

        // Пытаемся создать задачу с пересекающимся временем
        Task task2 = new Task(0, TaskType.TASK, "Task 2", TaskStatus.NEW,"Description 2",
                Duration.ofMinutes(30), now.plusMinutes(30));


        Gson gson = taskServer.getGson();
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());

    }
}

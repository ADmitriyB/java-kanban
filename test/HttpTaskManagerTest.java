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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerTest extends BaseHttpTest {


    @Test
    public void testGetHistory() throws Exception {
        // Сначала создаем и получаем задачу для создания истории
        Task task = new Task(0, TaskType.TASK, "Test Task", TaskStatus.NEW, "Test Description", Duration.ofMinutes(15), LocalDateTime.now());
        int taskId = manager.createTask(task).getId();
        manager.getTaskById(taskId); // Добавляем в историю

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test Task"));
    }

    @Test
    public void testGetPrioritizedTasks() throws Exception {
        // Создаем задачи с разным временем начала
        Task task1 = new Task(0, TaskType.TASK, "Task 1", TaskStatus.NEW, "Description 1", Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        Task task2 = new Task(0, TaskType.TASK, "Task 2", TaskStatus.NEW, "Description 2", Duration.ofMinutes(45), LocalDateTime.now().plusHours(1));

        manager.createTask(task1);
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        // Проверяем, что задачи возвращаются в правильном порядке
        Gson gson = taskServer.getGson();
        Task[] tasks = gson.fromJson(response.body(), Task[].class);

        assertEquals(2, tasks.length);
        assertEquals("Task 2", tasks[0].getName()); // Должна быть первой, так как начинается раньше
    }

    @Test
    public void testGetEmptyPrioritizedTasks() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}

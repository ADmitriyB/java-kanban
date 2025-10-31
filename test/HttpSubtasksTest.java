import com.google.gson.Gson;
import enums.TaskStatus;
import enums.TaskType;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpSubtasksTest extends BaseHttpTest {

    LocalDateTime now = LocalDateTime.now();

    @Test
    public void testCreateSubtask() throws Exception {

        Epic epic = new Epic(0, TaskType.EPIC, "Test Epic", "Epic Description",
                Duration.ofMinutes(15), now);
        int epicId = manager.createEpic(epic).getId();

        Subtask subtask = new Subtask(0, TaskType.SUBTASK, "Test Subtask", TaskStatus.NEW, "Test Description",
                Duration.ofMinutes(30), now, epicId);

        Gson gson = taskServer.getGson();
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        // Проверяем, что подзадача добавилась в менеджер
        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals("Test Subtask", subtasks.get(0).getName());
        assertEquals(epicId, subtasks.get(0).getEpicId());

    }

    @Test
    public void testCreateSubtaskWithInvalidEpic() throws Exception {
        // Пытаемся создать подзадачу с несуществующим эпиком
        Subtask subtask = new Subtask(0, TaskType.SUBTASK, "Test Subtask", TaskStatus.NEW, "Test Description",
                Duration.ofMinutes(30), now, 1);

        Gson gson = taskServer.getGson();
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

}
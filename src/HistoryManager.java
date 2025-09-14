import java.util.List;

public interface HistoryManager {
    void addTaskToHistory(Task task); //должен помечать задачи как просмотренные
    void remove(int id);
    List<Task> getHistory();


}

public class Manager {
    public static TaskManager getDefault() {


        return new InMemoryTaskManager();
    }
}

import java.util.Objects;

public class Task {
    // В ТЗ нет условий по изменениям полей, однако на ревью было сказано, что name можно обновлять
    // Предполагаю, что description тоже можно обновлять, поэтому добавил сеттеры на оба поля
    private String name;
    private String description;
    private Status status;
    private int id;

    // Конструктор для создания задачи
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = -1;
    }

    // Конструктор для обновления задачи
    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}

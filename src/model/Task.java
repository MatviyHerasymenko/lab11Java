package model;

public class Task {
    private final int taskId;
    private final String description;
    private final Integer employeeId;
    private final String employeeName;

    public Task(int taskId, String description, Integer employeeId, String employeeName) {
        this.taskId = taskId;
        this.description = description;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    @Override
    public String toString() {
        String emp = (employeeName != null && !employeeName.isBlank())
                ? employeeName + " (ID: " + employeeId + ")"
                : (employeeId != null ? "ID співробітника: " + employeeId : "Не призначено");
        return String.format("Задача #%-3d | %-38s | Виконавець: %s", taskId, description, emp);
    }
}

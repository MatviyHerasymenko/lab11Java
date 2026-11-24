package model;

public class Employee {
    private final int employeeId;
    private final String lastName;
    private final String firstName;
    private final String position;
    private final Integer departmentId;
    private final String departmentName;

    public Employee(int employeeId, String lastName, String firstName, String position, Integer departmentId, String departmentName) {
        this.employeeId = employeeId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPosition() {
        return position;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }

    @Override
    public String toString() {
        String dept = (departmentName != null) ? departmentName : ("Відділ #" + departmentId);
        return String.format("ID: %-4d | %-22s | Посада: %-16s | Відділ: %s",
                employeeId, getFullName(), position != null ? position : "-", dept);
    }
}

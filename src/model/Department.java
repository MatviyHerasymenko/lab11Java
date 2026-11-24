package model;

public class Department {
    private final int departmentId;
    private final String name;
    private final String phone;

    public Department(int departmentId, String name, String phone) {
        this.departmentId = departmentId;
        this.name = name;
        this.phone = phone;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-20s (тел. %s)", departmentId, name, phone != null ? phone : "-");
    }
}

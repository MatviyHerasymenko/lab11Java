package db;

import model.Department;
import model.Employee;
import model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS departments (" +
                    "department_id INTEGER PRIMARY KEY, " +
                    "name VARCHAR(30) NOT NULL, " +
                    "phone VARCHAR(15)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS employees (" +
                    "employee_id INTEGER PRIMARY KEY, " +
                    "last_name VARCHAR(20) NOT NULL, " +
                    "first_name VARCHAR(10) NOT NULL, " +
                    "position VARCHAR(20), " +
                    "department_id INTEGER, " +
                    "FOREIGN KEY (department_id) REFERENCES departments(department_id) ON DELETE SET NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS tasks (" +
                    "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "description VARCHAR(50) NOT NULL, " +
                    "employee_id INTEGER, " +
                    "FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE" +
                    ");");

            // Заповнення початковими даними, якщо таблиці порожні
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM departments")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    seedInitialData(conn);
                }
            }

        } catch (SQLException e) {
            System.err.println("Помилка ініціалізації бази даних: " + e.getMessage());
        }
    }

    private void seedInitialData(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO departments (department_id, name, phone) VALUES " +
                    "(1, 'Розробка', '+380441112233'), " +
                    "(2, 'Тестування (QA)', '+380442223344'), " +
                    "(3, 'Менеджмент', '+380443334455');");

            stmt.execute("INSERT INTO employees (employee_id, last_name, first_name, position, department_id) VALUES " +
                    "(101, 'Шевченко', 'Тарас', 'Tech Lead', 1), " +
                    "(102, 'Франко', 'Іван', 'Senior Dev', 1), " +
                    "(103, 'Українка', 'Леся', 'QA Lead', 2), " +
                    "(104, 'Коцюбинський', 'Михайло', 'Project Manager', 3), " +
                    "(105, 'Сковорода', 'Григорій', 'Junior Dev', 1);");

            stmt.execute("INSERT INTO tasks (description, employee_id) VALUES " +
                    "('Розробка архітектури модуля безпеки', 101), " +
                    "('Оптимізація SQL-запитів до бази даних', 102), " +
                    "('Написання інтеграційних тестів', 103), " +
                    "('Планування спринту та оцінка задач', 104), " +
                    "('Рефакторинг коду сервісного шару', 102), " +
                    "('Виправлення багів авторизації', 105);");
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.last_name, e.first_name, e.position, e.department_id, d.name AS department_name " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.department_id = d.department_id " +
                "ORDER BY e.employee_id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("position"),
                        (Integer) rs.getObject("department_id"),
                        rs.getString("department_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Помилка отримання співробітників: " + e.getMessage());
        }
        return list;
    }

    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.task_id, t.description, t.employee_id, " +
                "(e.last_name || ' ' || e.first_name) AS employee_name " +
                "FROM tasks t " +
                "LEFT JOIN employees e ON t.employee_id = e.employee_id " +
                "ORDER BY t.task_id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Task(
                        rs.getInt("task_id"),
                        rs.getString("description"),
                        (Integer) rs.getObject("employee_id"),
                        rs.getString("employee_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Помилка отримання завдань: " + e.getMessage());
        }
        return list;
    }

    public List<Employee> getEmployeesByDepartment(int departmentId) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.last_name, e.first_name, e.position, e.department_id, d.name AS department_name " +
                "FROM employees e " +
                "LEFT JOIN departments d ON e.department_id = d.department_id " +
                "WHERE e.department_id = ? " +
                "ORDER BY e.employee_id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Employee(
                            rs.getInt("employee_id"),
                            rs.getString("last_name"),
                            rs.getString("first_name"),
                            rs.getString("position"),
                            (Integer) rs.getObject("department_id"),
                            rs.getString("department_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Помилка фільтрації співробітників за відділом: " + e.getMessage());
        }
        return list;
    }

    public boolean addTaskForEmployee(int employeeId, String description) {
        if (!employeeExists(employeeId)) {
            System.out.println("Співробітника з ID " + employeeId + " не знайдено.");
            return false;
        }

        String sql = "INSERT INTO tasks (description, employee_id) VALUES (?, ?);";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, description);
            ps.setInt(2, employeeId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Помилка додавання завдання: " + e.getMessage());
            return false;
        }
    }

    public List<Task> getTasksForEmployee(int employeeId) {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT t.task_id, t.description, t.employee_id, " +
                "(e.last_name || ' ' || e.first_name) AS employee_name " +
                "FROM tasks t " +
                "LEFT JOIN employees e ON t.employee_id = e.employee_id " +
                "WHERE t.employee_id = ? " +
                "ORDER BY t.task_id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Task(
                            rs.getInt("task_id"),
                            rs.getString("description"),
                            (Integer) rs.getObject("employee_id"),
                            rs.getString("employee_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Помилка отримання завдань для співробітника: " + e.getMessage());
        }
        return list;
    }

    public boolean deleteEmployee(int employeeId) {
        if (!employeeExists(employeeId)) {
            System.out.println("Співробітника з ID " + employeeId + " не існує.");
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Видаляємо прив'язані завдання та самого співробітника
            try (PreparedStatement psTasks = conn.prepareStatement("DELETE FROM tasks WHERE employee_id = ?")) {
                psTasks.setInt(1, employeeId);
                psTasks.executeUpdate();
            }

            try (PreparedStatement psEmp = conn.prepareStatement("DELETE FROM employees WHERE employee_id = ?")) {
                psEmp.setInt(1, employeeId);
                return psEmp.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Помилка видалення співробітника: " + e.getMessage());
            return false;
        }
    }

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT department_id, name, phone FROM departments ORDER BY department_id;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("department_id"),
                        rs.getString("name"),
                        rs.getString("phone")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Помилка отримання відділів: " + e.getMessage());
        }
        return list;
    }

    public boolean employeeExists(int employeeId) {
        String sql = "SELECT 1 FROM employees WHERE employee_id = ?;";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}

import db.DatabaseManager;
import model.Department;
import model.Employee;
import model.Task;
import util.Input;

import java.util.List;

public class Main {
    private static final DatabaseManager db = new DatabaseManager();

    public static void main(String[] args) {
        db.initializeDatabase();

        if (args.length > 0 && "demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }

        boolean work = true;
        while (work) {
            printMenu();
            int choice = Input.readInt("Ваш вибір: ", 0, 7);
            switch (choice) {
                case 1:
                    showAllEmployees();
                    break;
                case 2:
                    showAllTasks();
                    break;
                case 3:
                    showEmployeesByDepartment();
                    break;
                case 4:
                    addTask();
                    break;
                case 5:
                    showTasksByEmployee();
                    break;
                case 6:
                    deleteEmployee();
                    break;
                case 7:
                    runDemo();
                    break;
                case 0:
                    work = false;
                    break;
            }
        }
        System.out.println("Роботу завершено.");
    }

    private static void printMenu() {
        System.out.println("\n===== Меню (База даних: Відділи, Співробітники, Завдання) =====");
        System.out.println("1 - Отримати список всіх співробітників");
        System.out.println("2 - Отримати список всіх завдань");
        System.out.println("3 - Отримати список співробітників зазначеного відділу");
        System.out.println("4 - Додати завдання для деякого співробітника");
        System.out.println("5 - Для зазначеного співробітника отримати список його завдань");
        System.out.println("6 - Видалити співробітника");
        System.out.println("7 - Демонстраційний автотест (виконання всіх запитів)");
        System.out.println("0 - Вихід");
    }

    private static void showAllEmployees() {
        System.out.println("\n--- Список всіх співробітників ---");
        List<Employee> list = db.getAllEmployees();
        if (list.isEmpty()) {
            System.out.println("(співробітників немає)");
            return;
        }
        for (Employee e : list) {
            System.out.println(e);
        }
    }

    private static void showAllTasks() {
        System.out.println("\n--- Список всіх завдань ---");
        List<Task> list = db.getAllTasks();
        if (list.isEmpty()) {
            System.out.println("(завдань немає)");
            return;
        }
        for (Task t : list) {
            System.out.println(t);
        }
    }

    private static void showEmployeesByDepartment() {
        System.out.println("\nДоступні відділи:");
        for (Department d : db.getAllDepartments()) {
            System.out.println(d);
        }
        int deptId = Input.readInt("Введіть номер відділу: ");
        System.out.println("\n--- Співробітники відділу #" + deptId + " ---");
        List<Employee> list = db.getEmployeesByDepartment(deptId);
        if (list.isEmpty()) {
            System.out.println("(у цьому відділі немає співробітників або відділ не існує)");
            return;
        }
        for (Employee e : list) {
            System.out.println(e);
        }
    }

    private static void addTask() {
        System.out.println("\n--- Додавання завдання ---");
        int empId = Input.readInt("Введіть ID співробітника: ");
        if (!db.employeeExists(empId)) {
            System.out.println("Помилка: співробітника з ID " + empId + " не існує.");
            return;
        }
        String desc = Input.readNonEmpty("Введіть опис завдання (до 50 симв.): ");
        if (desc.length() > 50) {
            desc = desc.substring(0, 50);
        }
        if (db.addTaskForEmployee(empId, desc)) {
            System.out.println("Завдання успішно додано!");
        } else {
            System.out.println("Помилка додавання завдання.");
        }
    }

    private static void showTasksByEmployee() {
        int empId = Input.readInt("\nВведіть ID співробітника для перегляду його завдань: ");
        System.out.println("--- Завдання співробітника ID #" + empId + " ---");
        List<Task> list = db.getTasksForEmployee(empId);
        if (list.isEmpty()) {
            System.out.println("(для цього співробітника немає призначених завдань або співробітник не існує)");
            return;
        }
        for (Task t : list) {
            System.out.println(t);
        }
    }

    private static void deleteEmployee() {
        System.out.println("\n--- Видалення співробітника ---");
        int empId = Input.readInt("Введіть ID співробітника для видалення: ");
        if (db.deleteEmployee(empId)) {
            System.out.println("Співробітника ID #" + empId + " та його завдання успішно видалено.");
        } else {
            System.out.println("Не вдалося видалити співробітника.");
        }
    }

    public static void runDemo() {
        System.out.println("=== ДЕМОНСТРАЦІЯ ВИКОНАННЯ ВСІХ ЗАПИТІВ ДО БАЗИ ДАНИХ ===");

        System.out.println("\n1. Список всіх співробітників:");
        for (Employee e : db.getAllEmployees()) {
            System.out.println("   " + e);
        }

        System.out.println("\n2. Список всіх завдань:");
        for (Task t : db.getAllTasks()) {
            System.out.println("   " + t);
        }

        int targetDept = 1;
        System.out.println("\n3. Співробітники відділу #" + targetDept + " (Розробка):");
        for (Employee e : db.getEmployeesByDepartment(targetDept)) {
            System.out.println("   " + e);
        }

        int targetEmp = 101;
        System.out.println("\n4. Додавання нового завдання для співробітника #" + targetEmp + ":");
        db.addTaskForEmployee(targetEmp, "Проведення Code Review для релізу");
        System.out.println("   Завдання додано успішно.");

        System.out.println("\n5. Список завдань для співробітника #" + targetEmp + ":");
        for (Task t : db.getTasksForEmployee(targetEmp)) {
            System.out.println("   " + t);
        }

        int empToDelete = 105;
        System.out.println("\n6. Видалення співробітника #" + empToDelete + " (Григорій Сковорода):");
        db.deleteEmployee(empToDelete);
        System.out.println("   Співробітника видалено.");

        System.out.println("\nПеревірка списку співробітників після видалення:");
        for (Employee e : db.getAllEmployees()) {
            System.out.println("   " + e);
        }

        System.out.println("\n=== ДЕМОНСТРАЦІЮ ЗАВЕРШЕНО ===");
    }
}

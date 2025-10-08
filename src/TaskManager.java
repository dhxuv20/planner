import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

// Task class representing individual tasks
class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Status {
        NOT_STARTED, IN_PROGRESS, DONE
    }
    
    public enum Size {
        SMALL, MEDIUM, LARGE
    }
    
    private String name;
    private String subject;
    private LocalDate dueDate;
    private LocalDate creationDate;
    private LocalDate completionDate;
    private Status status;
    private Size size;
    
    public Task(String name, String subject, LocalDate dueDate, Status status, Size size) {
        this.name = name;
        this.subject = subject;
        this.dueDate = dueDate;
        this.creationDate = LocalDate.now();
        this.status = status;
        this.size = size;
        this.completionDate = null;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getCreationDate() { return creationDate; }
    
    public LocalDate getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDate completionDate) { this.completionDate = completionDate; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { 
        this.status = status;
        if (status == Status.DONE && completionDate == null) {
            this.completionDate = LocalDate.now();
        } else if (status != Status.DONE) {
            this.completionDate = null;
        }
    }
    
    public Size getSize() { return size; }
    public void setSize(Size size) { this.size = size; }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("Task: %s | Subject: %s | Due: %s | Created: %s | Status: %s | Size: %s | Completed: %s",
                name, subject, dueDate.format(formatter), creationDate.format(formatter), 
                status, size, completionDate != null ? completionDate.format(formatter) : "N/A");
    }
}

// Main Task Manager class
public class TaskManager {
    private List<Task> tasks;
    private final String fileName = "tasks.dat";
    private Scanner scanner;
    private DateTimeFormatter dateFormatter;
    
    public TaskManager() {
        tasks = new ArrayList<>();
        scanner = new Scanner(System.in);
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        loadTasksFromFile();
    }
    
    // File operations
    private void saveTasksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(tasks);
            System.out.println("Tasks saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadTasksFromFile() {
        File file = new File(fileName);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
                tasks = (List<Task>) ois.readObject();
                System.out.println("Tasks loaded successfully! (" + tasks.size() + " tasks found)");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading tasks: " + e.getMessage());
                tasks = new ArrayList<>();
            }
        } else {
            System.out.println("No existing task file found. Starting with empty task list.");
        }
    }
    
    // Use case 1: Add a new task
    public void addNewTask() {
        System.out.println("\n=== Add New Task ===");
        
        System.out.print("Enter task name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter subject: ");
        String subject = scanner.nextLine();
        
        LocalDate dueDate = getDateInput("Enter due date (yyyy-MM-dd): ");
        
        Task.Status status = getStatusInput();
        Task.Size size = getSizeInput();
        
        Task newTask = new Task(name, subject, dueDate, status, size);
        tasks.add(newTask);
        saveTasksToFile();
        
        System.out.println("Task added successfully!");
    }
    
    // Use case 2: List all tasks with different views
    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        
        System.out.println("\n=== Task List Options ===");
        System.out.println("1. All tasks");
        System.out.println("2. In-progress tasks (sorted by due date)");
        System.out.println("3. Completed tasks (sorted by due date)");
        System.out.println("4. Tasks created after a certain date");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                displayAllTasks();
                break;
            case 2:
                displayInProgressTasks();
                break;
            case 3:
                displayCompletedTasks();
                break;
            case 4:
                displayTasksAfterDate();
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    private void displayAllTasks() {
        System.out.println("\n=== All Tasks ===");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }
    
    private void displayInProgressTasks() {
        System.out.println("\n=== In-Progress Tasks (sorted by due date) ===");
        List<Task> inProgressTasks = tasks.stream()
                .filter(task -> task.getStatus() == Task.Status.IN_PROGRESS)
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
        
        if (inProgressTasks.isEmpty()) {
            System.out.println("No in-progress tasks found.");
        } else {
            for (int i = 0; i < inProgressTasks.size(); i++) {
                System.out.println((i + 1) + ". " + inProgressTasks.get(i));
            }
        }
    }
    
    private void displayCompletedTasks() {
        System.out.println("\n=== Completed Tasks (sorted by due date) ===");
        List<Task> completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == Task.Status.DONE)
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
        
        if (completedTasks.isEmpty()) {
            System.out.println("No completed tasks found.");
        } else {
            for (int i = 0; i < completedTasks.size(); i++) {
                System.out.println((i + 1) + ". " + completedTasks.get(i));
            }
        }
    }
    
    private void displayTasksAfterDate() {
        LocalDate filterDate = getDateInput("Enter date (yyyy-MM-dd) to filter tasks created after: ");
        
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getCreationDate().isAfter(filterDate))
                .collect(Collectors.toList());
        
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found created after " + filterDate.format(dateFormatter));
        } else {
            System.out.println("\n=== Tasks created after " + filterDate.format(dateFormatter) + " ===");
            for (int i = 0; i < filteredTasks.size(); i++) {
                System.out.println((i + 1) + ". " + filteredTasks.get(i));
            }
        }
    }
    
    // Use case 3: Remove a task
    public void removeTask() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to remove.");
            return;
        }
        
        System.out.println("\n=== Remove Task ===");
        LocalDate filterDate = getDateInput("Enter date (yyyy-MM-dd) to show tasks created after: ");
        
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getCreationDate().isAfter(filterDate))
                .collect(Collectors.toList());
        
        if (filteredTasks.isEmpty()) {
            System.out.println("No tasks found created after " + filterDate.format(dateFormatter));
            return;
        }
        
        System.out.println("\nTasks created after " + filterDate.format(dateFormatter) + ":");
        for (int i = 0; i < filteredTasks.size(); i++) {
            System.out.println((i + 1) + ". " + filteredTasks.get(i));
        }
        
        System.out.print("Enter the index number of the task to delete (1-" + filteredTasks.size() + "): ");
        int index = getIntInput();
        
        if (index >= 1 && index <= filteredTasks.size()) {
            Task taskToRemove = filteredTasks.get(index - 1);
            tasks.remove(taskToRemove);
            saveTasksToFile();
            System.out.println("Task removed successfully!");
        } else {
            System.out.println("Invalid index.");
        }
    }
    
    // Helper methods for input validation
    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd format.");
            }
        }
    }
    
    private Task.Status getStatusInput() {
        System.out.println("Select status:");
        System.out.println("1. Not Started");
        System.out.println("2. In Progress");
        System.out.println("3. Done");
        System.out.print("Choose status (1-3): ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1: return Task.Status.NOT_STARTED;
            case 2: return Task.Status.IN_PROGRESS;
            case 3: return Task.Status.DONE;
            default:
                System.out.println("Invalid choice. Defaulting to Not Started.");
                return Task.Status.NOT_STARTED;
        }
    }
    
    private Task.Size getSizeInput() {
        System.out.println("Select size:");
        System.out.println("1. Small");
        System.out.println("2. Medium");
        System.out.println("3. Large");
        System.out.print("Choose size (1-3): ");
        
        int choice = getIntInput();
        switch (choice) {
            case 1: return Task.Size.SMALL;
            case 2: return Task.Size.MEDIUM;
            case 3: return Task.Size.LARGE;
            default:
                System.out.println("Invalid choice. Defaulting to Medium.");
                return Task.Size.MEDIUM;
        }
    }
    
    private int getIntInput() {
        while (true) {
            try {
                int result = Integer.parseInt(scanner.nextLine());
                return result;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
    
    // Main menu
    public void displayMenu() {
        System.out.println("\n=== Task Management System ===");
        System.out.println("1. Add new task");
        System.out.println("2. List tasks");
        System.out.println("3. Remove task");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");
    }
    
    public void run() {
        System.out.println("Welcome to Task Management System!");
        
        while (true) {
            displayMenu();
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    addNewTask();
                    break;
                case 2:
                    listTasks();
                    break;
                case 3:
                    removeTask();
                    break;
                case 4:
                    System.out.println("Thank you for using Task Management System!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.run();
    }
}

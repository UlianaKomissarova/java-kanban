import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task walk = new Task(
                "Погулять",
                "Идем гулять в парк!",
                Status.NEW
        );

        Task playWithCat = new Task(
                "Поиграть с кошкой",
                "Достань игрушки",
                Status.NEW
        );

        Subtask packUp = new Subtask(
                "Собрать вещи",
                "Возьми все самое нужное с собой",
                Status.NEW
        );

        Subtask rentFlat = new Subtask(
                "Найди, где жить",
                "Поищи объявления об аренде",
                Status.NEW
        );

        ArrayList<Subtask> seaTasks = new ArrayList<>();
        seaTasks.add(packUp);
        seaTasks.add(rentFlat);

        Epic moveToSea = new Epic(
                "Переезд",
                "Едем жить на море",
                Status.NEW,
                seaTasks
        );

        Subtask goForAnInterview = new Subtask(
                "Пойти на собеседование",
                "Рассказать о себе",
                Status.NEW
        );

        ArrayList<Subtask> jobTasks = new ArrayList<>();
        jobTasks.add(goForAnInterview);

        Epic getJob = new Epic(
                "Найти работу",
                "Нужно получать денежки, чтобы тратить",
                Status.NEW,
                jobTasks
        );

        System.out.println("Создаем новые задачи...");

        taskManager.createNewTask(moveToSea);
        System.out.println("Cоздана задача: " + moveToSea.getName());
        taskManager.createNewTask(getJob);
        System.out.println("Cоздана задача: " + getJob.getName());
        taskManager.createNewTask(walk);
        System.out.println("Cоздана задача: " + walk.getName());
        taskManager.createNewTask(playWithCat);
        System.out.println("Cоздана задача: " + playWithCat.getName());

        System.out.println("Введите id эпика: ");
        System.out.println("1 - " + moveToSea.getName());
        System.out.println("2 - " + getJob.getName());
        taskManager.createNewTask(packUp);
        System.out.println("Cоздана задача: " + packUp.getName());

        System.out.println("Введите id эпика: ");
        System.out.println("1 - " + moveToSea.getName());
        System.out.println("2 - " + getJob.getName());
        taskManager.createNewTask(rentFlat);
        System.out.println("Cоздана задача: " + rentFlat.getName());

        System.out.println("Введите id эпика: ");
        System.out.println("1 - " + moveToSea.getName());
        System.out.println("2 - " + getJob.getName());
        taskManager.createNewTask(goForAnInterview);
        System.out.println("Cоздана задача: " + goForAnInterview.getName());

        while (true) {
            printMenu();
            int command = scanner.nextInt();
            switch (command) {
                case 1:
                    System.out.println("Список задач:");
                    for (String s : taskManager.getTaskList()) {
                        System.out.println(s);
                    }
                    break;
                case 2:
                    System.out.println("Изменим статусы созданных задач:");
                    System.out.println("Текущий статус задачи '" + walk.getName() + "': " + walk.getStatus());
                    walk.setStatus(Status.DONE);
                    System.out.println("Измененный статус: " + walk.getStatus());

                    System.out.println("Текущий статус задачи '" + packUp.getName() + "': " + packUp.getStatus());
                    packUp.setStatus(Status.DONE);
                    System.out.println("Измененный статус: " + packUp.getStatus());

                    System.out.println("Текущий статус задачи '" + rentFlat.getName() + "': " + rentFlat.getStatus());
                    rentFlat.setStatus(Status.DONE);
                    System.out.println("Измененный статус: " + rentFlat.getStatus());

                    taskManager.updateTask(moveToSea);
                    System.out.println("Текущий статус эпика '" + moveToSea.getName() + "': " + moveToSea.getStatus());
                    break;
                case 3:
                    taskManager.removeTaskById(walk.getId());
                    taskManager.removeTaskById(getJob.getId());
                    System.out.println("Удалены все лишние задачи.");
                    System.out.println("Текущий список задач:");
                    for (String s : taskManager.getTaskList()) {
                        System.out.println(s);
                    }
                    break;
                case 4:
                    historyManager.add(taskManager.getTaskById(1));
                    historyManager.add(taskManager.getTaskById(2));
                    historyManager.add(taskManager.getTaskById(1));
                    historyManager.add(taskManager.getTaskById(2));
                    historyManager.add(taskManager.getTaskById(1));
                    historyManager.add(taskManager.getTaskById(2));
                    historyManager.add(taskManager.getTaskById(1));
                    historyManager.add(taskManager.getTaskById(2));
                    historyManager.add(taskManager.getTaskById(1));
                    historyManager.add(taskManager.getTaskById(2));
                    historyManager.add(taskManager.getTaskById(7));
                    historyManager.add(taskManager.getTaskById(6));
                    for (Task task : historyManager.getHistory()) {
                        System.out.println(task);
                    }
                case 5:
                    System.out.println("До свидания");
                    return;
                default:
                    System.out.println("Попробуйте еще раз");
            }
        }
    }

    public static void printMenu() {
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Получить список задач");
        System.out.println("2 - Изменить статус задачи");
        System.out.println("3 - Удалить задачу");
        System.out.println("4 - Просмотреть историю задач");
        System.out.println("5 - Выход");
    }
}
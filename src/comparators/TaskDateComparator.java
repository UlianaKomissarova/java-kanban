package comparators;

import tasks.Task;

import java.util.Comparator;

public class TaskDateComparator implements Comparator<Task> {
    @Override
    public int compare(Task o1, Task o2) {
        if (o1.getStartTime() == null) {
            return 1;
        }

        if (o2.getStartTime() == null) {
            return -1;
        }

        if (o2.getStartTime().equals(o1.getStartTime())) {
            return 0;
        }

        if (o2.getStartTime().isBefore(o1.getStartTime())) {
            return 1;
        } else return -1;
    }
}

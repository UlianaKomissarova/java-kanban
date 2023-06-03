package test;

import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;

/**
 * TODO: Я не понимаю, для чего создавать абстрактный класс.
 * @param <T>
 */
abstract class TaskManagerTest<T extends TaskManager> {
    @BeforeEach
    protected void beforeEach() {
    }
}
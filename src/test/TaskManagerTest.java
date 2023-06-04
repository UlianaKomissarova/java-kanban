package test;

import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;

abstract class TaskManagerTest<T extends TaskManager> {
    @BeforeEach
    protected void beforeEach() {
    }
}
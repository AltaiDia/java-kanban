package kanban.manager;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    protected InMemoryTaskManager createManager () {
        return new InMemoryTaskManager();
    }

}
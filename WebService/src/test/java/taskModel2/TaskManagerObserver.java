package taskModel2;

import taskModel1.TaskManager;

/**
 * Date 2023/01/06  11:52
 * author  by HuBingKuan
 */
public class TaskManagerObserver implements Observer{
    private TaskManager taskManager;

    @Override
    public void response(Long taskId) {
        taskManager.release(taskId);
    }
}
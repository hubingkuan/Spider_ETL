package taskModel2;

import taskModel1.ActionType;
import taskModel1.ActivityService;
import taskModel1.TaskManager;

/**
 * Date 2023/01/06  11:44
 * author  by HuBingKuan
 */
public class TaskOngoing extends Subject implements State{
    private ActivityService activityService;
    private TaskManager taskManager;

    @Override
    public void update(Task task, ActionType actionType) {
        if (actionType == ActionType.ACHIEVE) {
            task.setState(new TaskFinished());
            // 通知
            notifyObserver(task.getTaskId());
        } else if (actionType == ActionType.STOP) {
            task.setState(new TaskPaused());
        } else if (actionType == ActionType.EXPIRE) {
            task.setState(new TaskExpired());
        }
    }
}
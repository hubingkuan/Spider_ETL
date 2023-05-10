package taskModel2;

import taskModel1.ActionType;

/**
 * Date 2023/01/06  11:44
 * author  by HuBingKuan
 */
public class TaskPaused implements State{
    @Override
    public void update(Task task, ActionType actionType) {
        if (actionType == ActionType.START) {
            task.setState(new TaskOngoing());
        } else if (actionType == ActionType.EXPIRE) {
            task.setState(new TaskExpired());
        }
    }
}
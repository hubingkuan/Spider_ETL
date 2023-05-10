package taskModel2;

import taskModel1.ActionType;

/**
 * Date 2023/01/06  11:40
 * author  by HuBingKuan
 */
public class TaskInit implements State{
    @Override
    public void update(Task task, ActionType actionType) {
        if(actionType==ActionType.START){
            TaskOngoing taskOngoing = new TaskOngoing();
            taskOngoing.add(new ActivityObserver());
            taskOngoing.add(new TaskManagerObserver());

            task.setState(taskOngoing);
        }
    }
}
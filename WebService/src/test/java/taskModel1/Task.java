package taskModel1;

/**
 * Date 2023/01/06  11:33
 * author  by HuBingKuan
 */
public class Task {
    private Long taskId;

    // 任务默认状态为初始化状态
    private TaskState state = TaskState.INIT;

    // 活动服务
    private ActivityService activityService;

    // 任务管理器
    private TaskManager taskManager;

    // 利用条件分支进行任务更新
    // 违反了开闭原则  也不够高内聚  引入了 taskManager  activityService
    public void updateState(ActionType actionType){
        if (state == TaskState.INIT) {
            if (actionType == ActionType.START) {
                state = TaskState.ONGOING;
            }
        } else if (state == TaskState.ONGOING) {
            if (actionType == ActionType.ACHIEVE) {
                state = TaskState.FINISHED;
                // 任务完成后进对外部服务进行通知
                activityService.notifyFinished(taskId);
                taskManager.release(taskId);
            } else if (actionType == ActionType.STOP) {
                state = TaskState.PAUSED;
            } else if (actionType == ActionType.EXPIRE) {
                state = TaskState.EXPIRED;
            }
        } else if (state == TaskState.PAUSED) {
            if (actionType == ActionType.START) {
                state = TaskState.ONGOING;
            } else if (actionType == ActionType.EXPIRE) {
                state = TaskState.EXPIRED;
            }
        }
    }
}
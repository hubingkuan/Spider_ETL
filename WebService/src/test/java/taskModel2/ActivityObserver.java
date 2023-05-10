package taskModel2;

import taskModel1.ActivityService;

/**
 * Date 2023/01/06  11:51
 * author  by HuBingKuan
 */
public class ActivityObserver implements Observer{
    private ActivityService activityService;

    @Override
    public void response(Long taskId) {
        activityService.notifyFinished(taskId);
    }
}
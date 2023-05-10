package taskModel2;

import taskModel1.ActionType;

/**
 * Date 2023/01/06  11:38
 * author  by HuBingKuan
 * 任务状态的抽象接口
 */
public interface State {
    // 默认实现 不做任何事情
    default void update(Task task, ActionType actionType) {

    }
}
package taskModel2;

import lombok.Data;
import taskModel1.ActionType;

/**
 * Date 2023/01/06  11:41
 * author  by HuBingKuan
 */
@Data
public class Task {
    private Long taskId;
    private State state=new TaskInit();

    public void updateState(ActionType actionType){
        state.update(this, actionType);
    }
}
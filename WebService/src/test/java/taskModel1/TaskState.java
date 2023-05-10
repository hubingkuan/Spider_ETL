package taskModel1;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Date 2023/01/06  11:29
 * author  by HuBingKuan
 */
@AllArgsConstructor
@Getter
public enum TaskState {
    INIT("初始化"),
    ONGOING("进行中"),
    PAUSED("暂停中"),
    FINISHED("已完成"),
    EXPIRED("已过期"),
    ;
    private final String message;
}
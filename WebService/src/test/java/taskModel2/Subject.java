package taskModel2;

import java.util.ArrayList;
import java.util.List;

/**
 * Date 2023/01/06  11:49
 * author  by HuBingKuan
 * 抽象观察者模式中的目标(被观察者)
 */
public abstract class Subject {
    protected List<Observer> observers = new ArrayList<>();

    // 增加观察者方法
    public void add(Observer observer) {
        observers.add(observer);
    }
    // 删除观察者方法
    public void remove(Observer observer) {
        observers.remove(observer);
    }
    // 通知观察者方法
    public void notifyObserver(Long taskId) {
        for (Observer observer : observers) {
            observer.response(taskId);
        }
    }
}
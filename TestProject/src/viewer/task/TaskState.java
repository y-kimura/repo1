package viewer.task;

/**
 * バックグラウンドタスクの実行状況を返す.
 *
 * @author jgb.dev
 */
public class TaskState {
    private boolean finished;
    synchronized
    void finish(){
        this.finished = true;
    }
    synchronized
    public boolean isFinished(){
        return finished;
    }
}

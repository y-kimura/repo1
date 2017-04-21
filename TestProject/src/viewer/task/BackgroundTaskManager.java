package viewer.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * バックグラウンドで処理の実行を管理するクラス.
 * @author myanya
 */
public class BackgroundTaskManager {
    private BackgroundTaskManager(){}

    private static ExecutorService executor = null;

    protected static ExecutorService build(){
        return Executors.newSingleThreadExecutor(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setPriority(Thread.MIN_PRIORITY);
                thread.setDaemon(true);
                thread.setName("BackgroundTaskProcessor");
                return thread;
            }
        });
    }

    /**
     * バックグラウンドタスクを登録する.
     *
     * @param runnable
     */
    synchronized
    public static TaskState executeTask(final Runnable runnable){
        if( executor == null ){
            executor = build();
        }
        final TaskState result = new TaskState();
        executor.submit(new Runnable() {
            public void run(){
                runnable.run();
                result.finish();
            }
        });
        return result;
    }

    /**
     * 登録済みの全てのバックグラウンドタスクをキャンセルする.
     */
    synchronized
    public static void cancelAllTask(){
        if( executor != null ){
            executor.shutdownNow();
            executor = build();
        }
    }

}

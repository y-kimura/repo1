package viewer.view.conf;

import java.util.Properties;

/**
 * Swing関連のコンフィグ情報を永続化するための機構.
 * @author jgb.dev
 */
public interface SwingConfig<T> {
    public void setup(T object);
    public void read(Properties properties);
    public void save(Properties properties);
}

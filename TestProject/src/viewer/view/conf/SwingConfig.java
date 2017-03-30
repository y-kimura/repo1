package viewer.view.conf;

import java.util.Properties;

/**
 * Swing�֘A�̃R���t�B�O�����i�������邽�߂̋@�\.
 * @author jgb.dev
 */
public interface SwingConfig<T> {
    public void setup(T object);
    public void read(Properties properties);
    public void save(Properties properties);
}

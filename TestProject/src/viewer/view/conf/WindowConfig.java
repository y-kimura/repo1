package viewer.view.conf;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Properties;
import javax.swing.JFrame;
import viewer.util.PropertiesUtils;

public class WindowConfig implements SwingConfig<JFrame> {
    
    private String header;
    private JFrame frame;
    private int top;
    private int left;
    private int width;
    private int height;
    private int state;
    
    public WindowConfig(String header){
        this.header = header;
    }
    
    public void setup(JFrame fr){
        this.frame = fr;
        frame.setSize(width, height);
        frame.setLocation(top, left);
        frame.setExtendedState(state);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved( ComponentEvent e ){
                if( frame.getExtendedState() == JFrame.NORMAL ){
                    top = frame.getLocation().x;
                    left = frame.getLocation().y;
                }
            }
            @Override
            public void componentResized( ComponentEvent e ){
                if( frame.getExtendedState() == JFrame.NORMAL ){
                    width = frame.getWidth();
                    height = frame.getHeight();
                }
            }
        });
        frame.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged( WindowEvent e ){
                state = e.getNewState();
            }
        });
    }
    public void sync(){
        
    }
    public void read(Properties props){
        top = PropertiesUtils.intValue(props, header+".window.top", 0);
        left = PropertiesUtils.intValue(props, header+".window.left", 0);
        width = PropertiesUtils.intValue(props, header+".window.width", 640);
        height = PropertiesUtils.intValue(props, header+".window.height", 480);
        state = PropertiesUtils.intValue(props, header+".window.state", JFrame.NORMAL);
    }
    public void save(Properties props){
        PropertiesUtils.set(props, header+".window.top", top);
        PropertiesUtils.set(props, header+".window.left", left);
        PropertiesUtils.set(props, header+".window.width", width);
        PropertiesUtils.set(props, header+".window.height", height);
        PropertiesUtils.set(props, header+".window.state", state);
    }
}

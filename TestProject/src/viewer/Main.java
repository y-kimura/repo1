package viewer;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viewer.view.MainWindow;

public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception {
		SwingUtilities.invokeAndWait(new Runnable(){
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

					ApplicationContext context = new ApplicationContext(new File("data\\conf.properties"));

					MainWindow window = new MainWindow(context);
					window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					window.setVisible(true);

				}catch(Exception e){
					log.error("error",e);
				}
			}
		});
	}
}

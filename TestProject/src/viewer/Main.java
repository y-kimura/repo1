package viewer;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;

import viewer.view.MainWindow;

public class Main {

//	static Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
			// アプリケーション設定の読み込み
			ApplicationContext context = new ApplicationContext(new File("conf.properties"));
		
			// メイン画面の表示
			MainWindow window = new MainWindow(context);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);
		
			// アプリケーション起動成功
//			log.info("0002");
		}catch(Exception e){
			int a = 1;
			// アプリケーション起動失敗
//			log.fatal("0001", e);
		}
	}
}

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
		
			// �A�v���P�[�V�����ݒ�̓ǂݍ���
			ApplicationContext context = new ApplicationContext(new File("conf.properties"));
		
			// ���C����ʂ̕\��
			MainWindow window = new MainWindow(context);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);
		
			// �A�v���P�[�V�����N������
//			log.info("0002");
		}catch(Exception e){
			int a = 1;
			// �A�v���P�[�V�����N�����s
//			log.fatal("0001", e);
		}
	}
}

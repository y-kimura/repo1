package viewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import viewer.ApplicationContext;
import viewer.ApplicationController;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {
	
	private ApplicationContext context;
	private ApplicationController controller;
	private JSplitPane splitpane;
	private TagSearchWindow tagSearchWindow;
	JPanel tagPanel;
	Map<String, Integer> tags = new HashMap<String, Integer>();
	
	JList jList;
	int currentlistIndex;
	
	public MainWindow(final ApplicationContext context){
		super("aaa");
		
		this.context = context;
		this.controller = new ApplicationController(context);
		
        // tagSearchWindow�̍\�z
        this.tagSearchWindow = new TagSearchWindow(context, controller);
        this.tagSearchWindow.setTitle("Tag Search");
        this.context.getTagSearchWindowConfig().setup(this.tagSearchWindow);
        tagSearchWindow.setVisible(true);
		
		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitpane.setLeftComponent(new FileListPanel(context, controller));
		getContentPane().add(splitpane, BorderLayout.CENTER);
		
		// test�^�O
//		context.getTagList().add("apple");
//		context.getTagList().add("orange");
//		context.getTagList().add("melon");
		
		tagPanel = new TagPanel(context, controller);
        tagPanel.setBackground(Color.WHITE);//JPanel�̔w�i�𔒂�
		
        splitpane.setRightComponent(tagPanel);
        
        // ���C���E�B���h�E�̎����ۑ��p�ݒ�
        context.getMainWindowConfig().setup(this);
	}
}

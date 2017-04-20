package viewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
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

	int currentlistIndex;

	public MainWindow(final ApplicationContext context){
		super("aaa");

		this.context = context;
		this.controller = new ApplicationController(context);

        // tagSearchWindow
        this.tagSearchWindow = new TagSearchWindow(context, controller);
        this.tagSearchWindow.setTitle("Tag Search");
        this.context.getTagSearchWindowConfig().setup(this.tagSearchWindow);
        tagSearchWindow.setVisible(true);

		splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitpane.setLeftComponent(new FileListPanel(context, controller));
		getContentPane().add(splitpane, BorderLayout.CENTER);

		tagPanel = new TagPanel(context, controller);
        tagPanel.setBackground(Color.WHITE);

        splitpane.setRightComponent(tagPanel);

     // メニューの構築
        setupMenus();

        context.getMainWindowConfig().setup(this);
	}

	private void setupMenus() {
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
        JMenu showMenu = new JMenu("表示");
        ButtonGroup group = new ButtonGroup();
        /* List */{
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem("List", true);
            item.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ){
                    context.setImageListViewType(ApplicationContext.TYPE_VIEW_LIST);
                }
            });
            showMenu.add(item);
            group.add(item);
        }
        /* Table */{
            final JRadioButtonMenuItem item = new JRadioButtonMenuItem("Detail", false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent e ){
                    context.setImageListViewType(ApplicationContext.TYPE_VIEW_DETAIL);
                }
            });
            showMenu.add(item);
            group.add(item);
        }
        menubar.add(showMenu);
	}
}

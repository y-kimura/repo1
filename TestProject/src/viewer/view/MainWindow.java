package viewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

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

        splitpane.setDividerLocation(context.mainwindowDividerLocation);

        splitpane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
    	    new PropertyChangeListener() {
    	        @Override
    	        public void propertyChange(PropertyChangeEvent pce) {
    	        	context.mainwindowDividerLocation = splitpane.getDividerLocation();
    	        }
        });

     // メニューの構築
        setupMenus();

        context.getMainWindowConfig().setup(this);
	}

	private void setupMenus() {
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
		ButtonGroup group = new ButtonGroup();
		JToggleButton listButton = new JToggleButton("List");
		listButton.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent e ){
                context.setImageListViewType(ApplicationContext.TYPE_VIEW_LIST);
            }
        });
		JToggleButton detailButton = new JToggleButton("Detail");
		detailButton.addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent e ){
                context.setImageListViewType(ApplicationContext.TYPE_VIEW_DETAIL);
            }
        });
		group.add(listButton);
		group.add(detailButton);
		menubar.add(listButton);
		menubar.add(detailButton);
	}
}

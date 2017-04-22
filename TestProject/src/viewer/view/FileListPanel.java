package viewer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.ApplicationControllerListener;
import viewer.model.Item;
import viewer.task.BackgroundTaskManager;
import viewer.util.CreateThumbUtils;

public class FileListPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList jList;
	private ApplicationContext context;
	private ApplicationController controller;

	private DefaultListModel<Item> listModel;

	private static final Logger log = LoggerFactory.getLogger(FileListPanel.class);

	@SuppressWarnings("unchecked")
	public FileListPanel(final ApplicationContext context, final ApplicationController controller){
        this.context = context;
        this.controller = controller;
        setLayout(new BorderLayout());

		listModel = new DefaultListModel<Item>();
		for(Item item: context.filterItemList) {
		    listModel.addElement(item);
		}
		jList = new JList(listModel);
		jList.setFixedCellHeight(100);
		jList.setFixedCellWidth(110);
		jList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		jList.setVisibleRowCount(0);
		setPreferredSize(new Dimension(400, 240));

		jList.addMouseListener(new ListMouseListener());
		jList.addKeyListener(new ListKeyListener());
		jList.addListSelectionListener(new ListSelectionListener() {
           public void valueChanged( ListSelectionEvent e ){
                if( !e.getValueIsAdjusting() ){
                	context.selectIndex = jList.getSelectedIndex();
                    controller.fireSelectedIndexChanged((Item)jList.getSelectedValue());
                } else {
                	context.selectIndex = -1;
                	controller.fireSelectedIndexChanged(null);
                }
            }
		});

		final ListCellRenderer renderer = jList.getCellRenderer();
        jList.setCellRenderer(new ListCellRenderer() {
        	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        		Item item = (Item)value;
        		if (context.getImageListViewType() == ApplicationContext.TYPE_VIEW_LIST) {
            		JLabel label = (JLabel)renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    label.setText(item.getName());
                    label.setVerticalTextPosition(SwingConstants.BOTTOM);
                    label.setHorizontalTextPosition(SwingConstants.CENTER);
                    File thumbFile = getAndCreateThumbFile(item, 1, new Runnable() {
                        public void run(){
                        	if (item.thumbStat.get(1) == 2) {
	                            Rectangle r = list.getCellBounds(index, index);
	                            list.repaint(r);
                        	}
                        }
                    });
                    if(!thumbFile.exists()){
                        label.setIcon(new ImageIcon(ApplicationContext.TMP_THUMB_FILE));
                    } else {
                    	label.setIcon(new ImageIcon(thumbFile.getPath()));
                    }
                    if (item.tags.isEmpty()) {
                        label.setBackground(Color.GRAY);
                    }
            		return label;
        		} else {
        			JPanel panel = new JPanel();
        			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        			for (int i = 1; i <= ApplicationContext.MAX_THUMB ; i++) {
                        File thumbFile = getAndCreateThumbFile(item, i, new Runnable() {
                            public void run(){
                            	for(int value: item.thumbStat.values()) {
                            		if (value == 1)return;
                            	}
                                Rectangle r = list.getCellBounds(index, index);
                                list.repaint(r);
                            }
                        });
                        if(!thumbFile.exists()){
                        	panel.add(new JLabel(new ImageIcon(ApplicationContext.TMP_THUMB_FILE)));
                        } else {
                			panel.add(new JLabel(new ImageIcon(thumbFile.getPath())));
                        }
            			panel.add(Box.createRigidArea(new Dimension(5,5)));
        			}
        			panel.add(new JLabel(item.name));
        			panel.setPreferredSize(null);
        			if (isSelected) {
        				panel.setBackground(Color.CYAN);
        			}
            		return panel;
        		}
        	}
        });

        this.controller.addListener(new ApplicationControllerListener() {
            public void selectedIndexChanged(Item item ){
            }
        });

        this.context.addItemListChangeListener(new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ){
        		listModel = new DefaultListModel<Item>();
        		for(Item item: context.filterItemList) {
        		    listModel.addElement(item);
        		}
            	jList.setModel(listModel);
            }
        });

        this.context.addItemTagChangeListener(new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ){
            	listModel.set(jList.getSelectedIndex(), listModel.getElementAt(jList.getSelectedIndex()));
            }
        });

        this.context.addImageListViewTypeChangeListener(new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ){
            	if ((int)evt.getNewValue() == ApplicationContext.TYPE_VIEW_LIST) {
            		jList.setFixedCellHeight(100);
            		jList.setFixedCellWidth(110);
            		jList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            	} else {
            		jList.setFixedCellHeight(100);
            		jList.setFixedCellWidth(1500);
            		jList.setLayoutOrientation(JList.VERTICAL);
            	}
        		jList.repaint();
            }
        });

        JScrollPane sp = new JScrollPane(jList);
        this.add(sp, BorderLayout.CENTER);
	}

    synchronized
    private File getAndCreateThumbFile(Item item, final int thumbIndex, final Runnable cmd ){
        try {
        	File thumbFile = new File(context.smbDir + "\\"+ ApplicationContext.createThumbFileName(item.name, thumbIndex));
            if(!thumbFile.exists() && !item.thumbStat.containsKey(thumbIndex)){
            	item.thumbStat.put(thumbIndex, 1);
                BackgroundTaskManager.executeTask(new Runnable() {
                    public void run(){
                        try {
            				CreateThumbUtils createThumbUtils = new CreateThumbUtils();
            				createThumbUtils.createThumb(item.file, thumbFile, thumbIndex);
                        	item.thumbStat.replace(thumbIndex, 2);
                        	log.debug("createThumb="+ item.name);
                            cmd.run();
                        }catch(Exception e){
                            log.error(getName() + " :thumbIndex=" +  thumbIndex, e);
                        }
                    }
                });
            }
            return thumbFile;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

	/**
	 * JList�p�̃L�[���X�i�[
	 */
	class ListKeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			super.keyTyped(e);
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				playItem(jList.getSelectedIndex());
			}
		}
	}

	/**
	 * JList�p�̃}�E�X���X�i�[
	 */
	class ListMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				playItem(jList.getSelectedIndex());
			}
		}
	}

	private void playItem(int index) {
		File f = context.filterItemList.get(index).file;
		if (!Desktop.isDesktopSupported()) {
			return;
		}
		try {
			Desktop.getDesktop().open(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

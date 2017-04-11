package viewer.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.ApplicationControllerListener;
import viewer.model.Item;

public class FileListPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JList jList;
	private ApplicationContext context;
	private ApplicationController controller;

	private DefaultListModel listModel;

	@SuppressWarnings("unchecked")
	public FileListPanel(final ApplicationContext context, final ApplicationController controller){
        this.context = context;
        this.controller = controller;
        setLayout(new BorderLayout());

		listModel = new DefaultListModel();
		for(Item item: context.filterItemList) {
		    listModel.addElement(item);
		}
		jList = new JList(listModel);
		jList.setFixedCellHeight(100);
		jList.setFixedCellWidth(90);
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
        		JLabel label = (JLabel)renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        		Item item = (Item)value;
                label.setText(item.getName());
                label.setVerticalTextPosition(SwingConstants.BOTTOM);
                label.setHorizontalTextPosition(SwingConstants.CENTER);
 //               label.setIcon(new ImageIcon(context.smbDir + "\\"+ item.getThumbName()));
        		return label;
        	}
        });

        this.controller.addListener(new ApplicationControllerListener() {
            public void selectedIndexChanged(Item item ){
            }
        });

        this.context.addItemListChangeListener(new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ){
        		listModel = new DefaultListModel();
        		for(Item item: context.filterItemList) {
        		    listModel.addElement(item);
        		}
            	jList.setModel(listModel);
            }
        });

        JScrollPane sp = new JScrollPane(jList);
        this.add(sp, BorderLayout.CENTER);
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

package viewer.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.ApplicationControllerListener;
import viewer.model.Item;
import viewer.model.TagList;

public class TagPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
    private ApplicationController controller;
    private TagList tagList;

    private List<JCheckBox> checkbox;
    private JTextField newTagInput;
//    private List<SelectionListener> listeners;

    public TagPanel(ApplicationContext context, final ApplicationController controller){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.context = context;
        this.controller = controller;
        this.tagList = context.getTagList();

        this.newTagInput = new JTextField(15);
        newTagInput.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        this.checkbox = new ArrayList<JCheckBox>();

        add(newTagInput);
        add(setButton("add"));

        JTree tree = new JTree() {
        	  @Override public void updateUI() {
        	    setCellRenderer(null);
        	    setCellEditor(null);
        	    super.updateUI();
        	    //???: JDK 1.6.0 LnF bug???
        	    setCellRenderer(new CheckBoxNodeRenderer());
        	    setCellEditor(new CheckBoxNodeEditor());
        	  }
        };

        boolean b = true;
        TreeModel model = tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        Enumeration<?> e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            node.setUserObject(new CheckBoxNode(Objects.toString(node.getUserObject(), ""), b));
            b ^= true;
        }

        tree.setEditable(true);
        tree.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

//	    for(Tag tag: tagList.tagList) {
//	        add(setCheckBox(tag.id, tag.name, false, null));
//	    }
        add(new JScrollPane(tree));

        this.controller.addListener(new ApplicationControllerListener() {
            public void selectedIndexChanged(Item item){
//            	removeAll();
//        		//setSize(getPreferredSize());
//                add(newTagInput);
//                add(setButton("add"));
//        		if (item != null) {
//            	    for(Tag tag: tagList.tagList) {
//            	    	if (item.tags.contains(tag.id)) {
//            	        	add(setCheckBox(tag.id, tag.name, true, item));
//            	    	} else {
//            	        	add(setCheckBox(tag.id, tag.name, false, item));
//            	    	}
//            	    }
//        		} else {
//        		    for(Tag tag: tagList.tagList) {
//        		        add(setCheckBox(tag.id, tag.name, false, null));
//        		    }
//        		}
//        		setSize(getPreferredSize());
//        	    validate();
//        	    repaint();
            }
        });
    }

    private JButton setButton(String name) {
    	final JButton button = new JButton(name);

    	button.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
            	String newTag = newTagInput.getText();
            	if (newTag == null || newTag.isEmpty()) {
            		return;
            	}
            	context.addNewTag(newTag);
            }
        });
    	return button;
    }
}

//	//JCheckBox�𐶐�����֐�
//	//����value�̓`�F�b�N�{�b�N�X��
//    private JCheckBox setCheckBox(final int id, String name, boolean checked, final Item item){
//        final JCheckBox chkbox = new JCheckBox(name, checked);
//
//        if (item != null) {
//            chkbox.addActionListener(new ActionListener(){
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                	context.filterItemList.get(0).tags.add(id);
//                    // �I�����ꂽ�Ƃ��̓����ݒ�
//                    if(chkbox.isSelected()){
//                    	item.tags.add(id);
//                    }else{
//                    	item.tags.remove(id);
//                    }
//                }
//            });
//        }
//        chkbox.setMargin(new Insets(0, 0, 0, 0));
//        chkbox.setBackground(Color.WHITE);
//        return chkbox;
//    }
//
//    public void addSelectionListener(SelectionListener listener){
//        listeners.add(listener);
//    }
//    public void removeSelectionListener(SelectionListener listener){
//        listeners.remove(listener);
//    }
//    protected void fireUpdated(){
//        for( SelectionListener l: listeners ){
//            l.updated(this, visibleFlags);
//        }
//    }

class CheckBoxNodeRenderer implements TreeCellRenderer {
    private final JCheckBox checkBox = new JCheckBox();
    private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    @Override public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (leaf && value instanceof DefaultMutableTreeNode) {
            checkBox.setEnabled(tree.isEnabled());
            checkBox.setFont(tree.getFont());
            checkBox.setOpaque(false);
            checkBox.setFocusable(false);
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof CheckBoxNode) {
                CheckBoxNode node = (CheckBoxNode) userObject;
                checkBox.setText(node.text);
                checkBox.setSelected(node.selected);
            }
            return checkBox;
        }
        return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
//*
//delegation pattern
class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {
    private final JCheckBox checkBox = new JCheckBox() {
        private transient ActionListener handler;
        @Override public void updateUI() {
            removeActionListener(handler);
            super.updateUI();
            setOpaque(false);
            setFocusable(false);
            handler = e -> stopCellEditing();
            addActionListener(handler);
        }
    };
    @Override public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
        if (leaf && value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof CheckBoxNode) {
                checkBox.setSelected(((CheckBoxNode) userObject).selected);
            } else {
                checkBox.setSelected(false);
            }
            checkBox.setText(value.toString());
        }
        return checkBox;
    }
    @Override public Object getCellEditorValue() {
        return new CheckBoxNode(checkBox.getText(), checkBox.isSelected());
    }
    @Override public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent && e.getSource() instanceof JTree) {
            MouseEvent me = (MouseEvent) e;
            JTree tree = (JTree) me.getComponent();
            TreePath path = tree.getPathForLocation(me.getX(), me.getY());
            Object o = path.getLastPathComponent();
            if (o instanceof TreeNode) {
                return ((TreeNode) o).isLeaf();
            }
        }
        return false;
    }
}

    class CheckBoxNode {
        public final String text;
        public final boolean selected;
        protected CheckBoxNode(String text, boolean selected) {
            this.text = text;
            this.selected = selected;
        }
        @Override public String toString() {
            return text;
        }
    }

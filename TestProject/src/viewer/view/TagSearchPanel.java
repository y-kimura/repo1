package viewer.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.model.Category;
import viewer.model.Item;
import viewer.model.Tag;
import viewer.model.TagList;
import viewer.model.TagTreeNode;
import viewer.view.component.TagTreeCellEditor;
import viewer.view.component.TagTreeCellRenderer;

public class TagSearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
    private ApplicationController controller;
    private TagList tagList;
    private List<Integer> filterTagList;
	private JTree tree;

//    private List<SelectionListener> listeners;

    public TagSearchPanel(ApplicationContext context, final ApplicationController controller){
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.context = context;
        this.controller = controller;
        this.tagList = context.getTagList();
        this.filterTagList = context.filterTagList;

		tree = new JTree(createTagTreeModel()) {
			@Override
			public void updateUI() {
				setCellRenderer(null);
				setCellEditor(null);
				super.updateUI();
				setCellRenderer(new TagTreeCellRenderer());
				setCellEditor(new TagTreeCellEditor());
			}
		};
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}

		tree.setEditable(true);
		tree.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		add(new JScrollPane(tree));

	    this.context.addTagListChangeListener(new PropertyChangeListener() {
	        public void propertyChange( PropertyChangeEvent evt ){
			tree.setModel(createTagTreeModel());
			for (int i = 0; i < tree.getRowCount(); i++) {
				tree.expandRow(i);
			}	        }
	    });
    }

	private DefaultTreeModel createTagTreeModel() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		Category rootCate = new Category();
		rootCate.name = "root";
		root.setUserObject(new TagTreeNode(rootCate, null));
		for (Category category: context.getCategoryList()) {
			DefaultMutableTreeNode tmpCate = new DefaultMutableTreeNode(category.name);
			tmpCate.setUserObject(new TagTreeNode(category, new Item()));
			for (Tag tag: context.getTagList().tagList) {
				if (tag.categoryId == category.id) {
					DefaultMutableTreeNode tmpTag = new DefaultMutableTreeNode(tag.name);
					tmpTag.setUserObject(new TagTreeNode(tag, new Item()));
					tmpCate.add(tmpTag);
				}
			}
			root.add(tmpCate);
		}
		return new DefaultTreeModel(root);
	}


}
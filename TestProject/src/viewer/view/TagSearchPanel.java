package viewer.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.model.Category;
import viewer.model.Tag;
import viewer.model.TagList;
import viewer.model.TagSearchTreeNode;
import viewer.view.component.TagSearchTreeCellRenderer;

public class TagSearchPanel extends JPanel implements TreeSelectionListener{

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
				setCellRenderer(new TagSearchTreeCellRenderer());
//				setCellEditor(new TagTreeCellEditor());
			}
		};
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}

		tree.setEditable(true);
		tree.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		tree.addTreeSelectionListener(this);

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
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);
		root.setUserObject(new TagSearchTreeNode("root", -1, false, false));
		for (Category category: context.getCategoryList()) {
			DefaultMutableTreeNode tmpCate = new DefaultMutableTreeNode(category.name, true);
			tmpCate.setUserObject(new TagSearchTreeNode(category.name, category.id, false, false));
			for (Tag tag: context.getTagList().tagList) {
				if (tag.categoryId == category.id) {
					DefaultMutableTreeNode tmpTag = new DefaultMutableTreeNode(tag.name, false);
					tmpTag.setUserObject(new TagSearchTreeNode(tag.name, tag.id, true, filterTagList.contains(tag.id)));
					tmpCate.add(tmpTag);
				}
			}
			root.add(tmpCate);
		}
		return new DefaultTreeModel(root);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		TagSearchTreeNode selectedNode = (TagSearchTreeNode)((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
		if (selectedNode.tagFlag) {
			selectedNode.selected = !selectedNode.selected;
			if (selectedNode.selected) {
				filterTagList.add(selectedNode.tagId);
			} else {
				filterTagList.remove(selectedNode.tagId);
			}
			((DefaultTreeModel)tree.getModel()).nodeChanged((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
		}

	}


}
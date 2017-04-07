package viewer.view;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.model.Category;
import viewer.model.Tag;
import viewer.model.TagTreeNode;
import viewer.view.component.TagTreeCellRenderer;

public class TagSearchPanel extends JPanel implements TreeSelectionListener{

	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
    private List<Integer> filterTagList;
	private JTree tree;

    public TagSearchPanel(ApplicationContext context, final ApplicationController controller){
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.context = context;
        this.filterTagList = context.filterTagList;

		tree = new JTree(createTagTreeModel()) {
			@Override
			public void updateUI() {
				setCellRenderer(null);
				setCellEditor(null);
				super.updateUI();
				setCellRenderer(new TagTreeCellRenderer());
				setCellEditor(new DefaultTreeCellEditor(tree, new DefaultTreeCellRenderer()) {
		            @Override public boolean isCellEditable(EventObject e) {
		                return !(e instanceof MouseEvent) && super.isCellEditable(e);
		            }
		        });
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
		root.setUserObject(new TagTreeNode("root", -1, false, false));
		for (Category category: context.getCategoryList()) {
			DefaultMutableTreeNode tmpCate = new DefaultMutableTreeNode(category.name, true);
			tmpCate.setUserObject(new TagTreeNode(category.name, category.id, false, false));
			for (Tag tag: context.getTagList().tagList) {
				if (tag.categoryId == category.id) {
					DefaultMutableTreeNode tmpTag = new DefaultMutableTreeNode(tag.name, false);
					tmpTag.setUserObject(new TagTreeNode(tag.name, tag.id, true, filterTagList.contains(tag.id)));
					tmpCate.add(tmpTag);
				}
			}
			root.add(tmpCate);
		}
		return new DefaultTreeModel(root);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (tree.getLastSelectedPathComponent() != null) {
			TagTreeNode selectedNode = (TagTreeNode)((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
			if (selectedNode.tagFlag) {
				selectedNode.selected = !selectedNode.selected;
				if (selectedNode.selected) {
					filterTagList.add(selectedNode.tagId);
				} else {
					filterTagList.remove((Integer)selectedNode.tagId);
				}
				((DefaultTreeModel)tree.getModel()).nodeChanged((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
				context.filterItemList();
			}
			tree.clearSelection();
		}
	}
}
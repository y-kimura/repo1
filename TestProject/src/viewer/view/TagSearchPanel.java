package viewer.view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.model.Category;
import viewer.model.Tag;
import viewer.model.TagSearchTreeNode;
import viewer.view.component.TagSearchTreeCellRenderer;

public class TagSearchPanel extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
    private List<Integer> filterTagList;
    private List<Integer> antiFilterTagList;
	private JTree tree;

    public TagSearchPanel(ApplicationContext context, final ApplicationController controller){
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.context = context;
        this.filterTagList = context.filterTagList;
        this.antiFilterTagList = context.antiFilterTagList;

		tree = new JTree(createTagTreeModel()) {
			@Override
			public void updateUI() {
				setCellRenderer(null);
				setCellEditor(null);
				super.updateUI();
				setCellRenderer(new TagSearchTreeCellRenderer());
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

		tree.addMouseListener(this);

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
		root.setUserObject(new TagSearchTreeNode("root", -1, false, 0));
		for (Category category: context.getCategoryList()) {
			DefaultMutableTreeNode tmpCate = new DefaultMutableTreeNode(category.name, true);
			tmpCate.setUserObject(new TagSearchTreeNode(category.name, category.id, false, 0));
			for (Tag tag: context.getTagList().tagList) {
				if (tag.categoryId == category.id) {
					DefaultMutableTreeNode tmpTag = new DefaultMutableTreeNode(tag.name, false);
					if (filterTagList.contains(tag.id)) {
						tmpTag.setUserObject(new TagSearchTreeNode(tag.name, tag.id, true, 1));
					} else if (antiFilterTagList.contains(tag.id)) {
						tmpTag.setUserObject(new TagSearchTreeNode(tag.name, tag.id, true, 2));
					} else {
						tmpTag.setUserObject(new TagSearchTreeNode(tag.name, tag.id, true, 0));
					}
					tmpCate.add(tmpTag);
				}
			}
			root.add(tmpCate);
		}
		return new DefaultTreeModel(root);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mousePressed(MouseEvent e) {
		DefaultMutableTreeNode selectTreeNode = (DefaultMutableTreeNode)tree.getClosestPathForLocation(e.getX(), e.getY()).getLastPathComponent();
		TagSearchTreeNode selectedNode = (TagSearchTreeNode)selectTreeNode.getUserObject();
		if (selectedNode.tagFlag) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (selectedNode.status == 0) {
					filterTagList.add(selectedNode.tagId);
					selectedNode.status = 1;
				} else if (selectedNode.status == 1) {
					filterTagList.remove((Integer)selectedNode.tagId);
					selectedNode.status = 0;
				} else if (selectedNode.status == 2) {
					filterTagList.add((Integer)selectedNode.tagId);
					antiFilterTagList.remove((Integer)selectedNode.tagId);
					selectedNode.status = 1;
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				if (selectedNode.status == 0) {
					antiFilterTagList.add(selectedNode.tagId);
					selectedNode.status = 2;
				} else if (selectedNode.status == 1) {
					filterTagList.remove((Integer)selectedNode.tagId);
					antiFilterTagList.add((Integer)selectedNode.tagId);
					selectedNode.status = 2;
				} else if (selectedNode.status == 2) {
					antiFilterTagList.remove((Integer)selectedNode.tagId);
					selectedNode.status = 0;
				}
			}
			((DefaultTreeModel)tree.getModel()).nodeChanged(selectTreeNode);
			context.filterItemList();
		}
		tree.clearSelection();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
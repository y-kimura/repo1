package viewer.view;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.ApplicationControllerListener;
import viewer.model.Category;
import viewer.model.Item;
import viewer.model.Tag;
import viewer.model.TagTreeNode;
import viewer.view.component.TagTreeCellEditor;
import viewer.view.component.TagTreeCellRenderer;
import viewer.view.component.TagTreePopupMenu;

public class TagPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
	private ApplicationController controller;

//	private List<SelectionListener> listeners;
	private JTree tree;

	public TagPanel(ApplicationContext context, final ApplicationController controller){
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.context = context;
		this.controller = controller;

		tree = new JTree(createTagTreeModel(new Item())) {
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

		tree.setComponentPopupMenu(createTagTreePopup());

		add(new JScrollPane(tree));

		this.controller.addListener(new ApplicationControllerListener() {
			public void selectedIndexChanged(Item item){
				if (item != null) {
					tree.setModel(createTagTreeModel(item));
				} else {
					tree.setModel(createTagTreeModel(new Item()));
				}
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
		});
	}

	private DefaultTreeModel createTagTreeModel(Item item) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root", true);
		Category rootCate = new Category();
		rootCate.name = "root";
		root.setUserObject(new TagTreeNode(rootCate, null));
		for (Category category: context.getCategoryList()) {
			DefaultMutableTreeNode tmpCate = new DefaultMutableTreeNode(category.name, true);
			tmpCate.setUserObject(new TagTreeNode(category, item));
			for (Tag tag: context.getTagList().tagList) {
				if (tag.categoryId == category.id) {
					DefaultMutableTreeNode tmpTag = new DefaultMutableTreeNode(tag.name, false);
					tmpTag.setUserObject(new TagTreeNode(tag, item));
					tmpCate.add(tmpTag);
				}
			}
			root.add(tmpCate);
		}
		return new DefaultTreeModel(root);
	}

	private TagTreePopupMenu createTagTreePopup() {
		TagTreePopupMenu jpopup = new TagTreePopupMenu();
		jpopup.add(new AbstractAction("add") {
			protected final JTextField textField = new JTextField(24) {
				protected transient AncestorListener listener;
				@Override public void updateUI() {
					removeAncestorListener(listener);
					super.updateUI();
					listener = new AncestorListener() {
						@Override public void ancestorAdded(AncestorEvent e) {
							requestFocusInWindow();
						}
						@Override public void ancestorMoved(AncestorEvent e)   { /* not needed */ }
						@Override public void ancestorRemoved(AncestorEvent e) { /* not needed */ }
					};
					addAncestorListener(listener);
				}
			};
			@Override public void actionPerformed(ActionEvent e) {
				Object node = jpopup.getPath().getLastPathComponent();
				if (node instanceof DefaultMutableTreeNode) {
					TagTreeNode tagTreeNode = (TagTreeNode)((DefaultMutableTreeNode) node).getUserObject();
					if (tagTreeNode.tagFlag) {
						return;
					}
					textField.setText("");
					String dialogTitle = "";
					if (tagTreeNode.getName().equals("root")) {
						dialogTitle = "add Category";
					} else {
						dialogTitle = "add Tag";
					}
					int result = JOptionPane.showConfirmDialog(tree, textField, dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						if (tagTreeNode.getName().equals("root")) {
							context.addCategoryList(textField.getText());
						} else {
							context.getTagList().add(textField.getText(), tagTreeNode.category.id);
						}
						if (context.selectIndex < 0) {
							tree.setModel(createTagTreeModel(new Item()));
						} else {
							tree.setModel(createTagTreeModel(context.filterItemList.get(context.selectIndex)));
						}
						for (int i = 0; i < tree.getRowCount(); i++) {
							tree.expandRow(i);
						}
					}
				}
			}
		});

		jpopup.add(new AbstractAction("edit") {
			protected final JTextField textField = new JTextField(24) {
				protected transient AncestorListener listener;
				@Override public void updateUI() {
					removeAncestorListener(listener);
					super.updateUI();
					listener = new AncestorListener() {
						@Override public void ancestorAdded(AncestorEvent e) {
							requestFocusInWindow();
						}
						@Override public void ancestorMoved(AncestorEvent e)   { /* not needed */ }
						@Override public void ancestorRemoved(AncestorEvent e) { /* not needed */ }
					};
					addAncestorListener(listener);
				}
			};
			@Override public void actionPerformed(ActionEvent e) {
				Object node = jpopup.getPath().getLastPathComponent();
				if (node instanceof DefaultMutableTreeNode) {
					TagTreeNode tagTreeNode = (TagTreeNode)((DefaultMutableTreeNode) node).getUserObject();
					textField.setText(tagTreeNode.getName());
					int result = JOptionPane.showConfirmDialog(tree, textField, "edit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						String str = textField.getText();
						if (!str.trim().isEmpty()) {
							DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
							tagTreeNode.setName(str);
							model.valueForPathChanged(jpopup.getPath(), tagTreeNode);
						}
					}
				}
			}
		});

//		private final Action removeNodeAction = new AbstractAction("remove") {
//		@Override public void actionPerformed(ActionEvent e) {
//			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
//			if (!node.isRoot()) {
//				JTree tree = (JTree) getInvoker();
//				DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
//				model.removeNodeFromParent(node);
//			}
//		}
//	};

		return jpopup;
	}
}

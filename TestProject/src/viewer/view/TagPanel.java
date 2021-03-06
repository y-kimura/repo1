package viewer.view;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.ApplicationControllerListener;
import viewer.model.Category;
import viewer.model.Item;
import viewer.model.Tag;
import viewer.model.TagTreeNode;
import viewer.view.component.TagTreeCellRenderer;
import viewer.view.component.TagTreePopupMenu;

public class TagPanel extends JPanel implements TreeSelectionListener{

	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
	private ApplicationController controller;

//	private List<SelectionListener> listeners;
	private JTree tree;

	public TagPanel(ApplicationContext context, final ApplicationController controller){
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.context = context;
		this.controller = controller;

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

		tree.setComponentPopupMenu(createTagTreePopup());

		tree.addTreeSelectionListener(this);

		add(new JScrollPane(tree));

		this.controller.addListener(new ApplicationControllerListener() {
			public void selectedIndexChanged(Item item){
				tree.setModel(createTagTreeModel());
				for (int i = 0; i < tree.getRowCount(); i++) {
					tree.expandRow(i);
				}
			}
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
					if (context.selectIndex < 0) {
						tmpTag.setUserObject(new TagTreeNode(tag.name, tag.id, true, false));
					} else {
						tmpTag.setUserObject(new TagTreeNode(tag.name, tag.id, true, context.filterItemList.get(context.selectIndex).isSelectedTag(tag.id)));
					}
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
					if (tagTreeNode.name.equals("root")) {
						dialogTitle = "add Category";
					} else {
						dialogTitle = "add Tag";
					}
					int result = JOptionPane.showConfirmDialog(tree, textField, dialogTitle, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						if (tagTreeNode.name.equals("root")) {
							context.addCategoryList(textField.getText());
						} else {
							context.getTagList().add(textField.getText(), tagTreeNode.tagId);
						}
						tree.setModel(createTagTreeModel());
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
					textField.setText(tagTreeNode.name);
					int result = JOptionPane.showConfirmDialog(tree, textField, "edit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						String str = textField.getText();
						if (!str.trim().isEmpty()) {
							if (tagTreeNode.tagFlag) {
								for (Tag tag: context.getTagList().tagList) {
									if (tag.id == tagTreeNode.tagId) {
										tag.name = str;
									}
								}
							} else {
								for (Category cate: context.getCategoryList()) {
									if (cate.id == tagTreeNode.tagId) {
										cate.name = str;
									}
								}
							}
							tagTreeNode.name = str;
							((DefaultTreeModel)tree.getModel()).nodeChanged((DefaultMutableTreeNode) node);
						}
					}
				}
			}
		});

		jpopup.add(new AbstractAction("moveCate") {
			@Override public void actionPerformed(ActionEvent e) {
				Object node = jpopup.getPath().getLastPathComponent();
				if (node instanceof DefaultMutableTreeNode) {
					TagTreeNode tagTreeNode = (TagTreeNode)((DefaultMutableTreeNode) node).getUserObject();
					if (!tagTreeNode.tagFlag) {
						return;
					}
					Tag targetTag = null;
					for (Tag tag: context.getTagList().tagList) {
						if (tag.id == tagTreeNode.tagId) {
							targetTag = tag;
							break;
						}
					}
					JComboBox<String> categoryComboBox = new JComboBox<String>();
					for (Category cate: context.getCategoryList()) {
						if (cate.id == targetTag.categoryId) continue;
						categoryComboBox.addItem(cate.name);
					}
					int result = JOptionPane.showConfirmDialog(tree, categoryComboBox, targetTag.name + "の移動先", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
						for (Category cate: context.getCategoryList()) {
							if (cate.name.equals(categoryComboBox.getSelectedItem())) {
								targetTag.order = context.getTagList().getMaxOrder(cate.id) + 1;
								targetTag.categoryId = cate.id;
								context.getTagList().sortTagList();
								tree.setModel(createTagTreeModel());
								for (int i = 0; i < tree.getRowCount(); i++) {
									tree.expandRow(i);
								}
								break;
							}
						}
					}
				}
			}
		});
		jpopup.add(new AbstractAction("cangeOrder") {
			@Override public void actionPerformed(ActionEvent e) {
				Object node = jpopup.getPath().getLastPathComponent();
				if (node instanceof DefaultMutableTreeNode) {
					TagTreeNode tagTreeNode = (TagTreeNode)((DefaultMutableTreeNode) node).getUserObject();
					if (!tagTreeNode.tagFlag) {
						return;
					}
					Tag targetTag = null;
					for (Tag tag: context.getTagList().tagList) {
						if (tag.id == tagTreeNode.tagId) {
							targetTag = tag;
							break;
						}
					}
					JComboBox<String> tagComboBox = new JComboBox<String>();
					for (Tag tag: context.getTagList().tagList) {
						if (tag.categoryId == targetTag.categoryId) {
							tagComboBox.addItem(tag.name);
						}
					}
					int result = JOptionPane.showConfirmDialog(tree, tagComboBox, targetTag.name + "の移動先", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.OK_OPTION) {
//						int i = 0;
//						for (Tag tag: context.getTagList().tagList) {
//							if (tag.name.equals(tagComboBox.getSelectedItem())) {
//								context.getTagList().tagList.add(i, targetTag);
//								targetTag.categoryId = cate.id;
//								context.getTagList().sortTagList();
//								tree.setModel(createTagTreeModel());
//								for (int i = 0; i < tree.getRowCount(); i++) {
//									tree.expandRow(i);
//								}
//								break;
//							}
//							i++;
//						}
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

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (tree.getLastSelectedPathComponent() != null && context.selectIndex >= 0) {
			TagTreeNode selectedNode = (TagTreeNode)((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();
			if (selectedNode.tagFlag) {
				selectedNode.selected = !selectedNode.selected;
				if (selectedNode.selected) {
					context.filterItemList.get(context.selectIndex).tags.add(selectedNode.tagId);
				} else {
					context.filterItemList.get(context.selectIndex).tags.remove(selectedNode.tagId);
				}
				((DefaultTreeModel)tree.getModel()).nodeChanged((DefaultMutableTreeNode) tree.getLastSelectedPathComponent());
				context.listeners.firePropertyChange(ApplicationContext.PROP_ITEMTAG, null, null);
			}
			tree.clearSelection();
		}
	}
}

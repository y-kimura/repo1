package viewer.view.component;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import viewer.model.TagSearchTreeNode;
import viewer.model.TagTreeNode;

//delegation pattern
public class TagSearchTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {
		private TagSearchTreeNode checkBoxNode;
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
			TagSearchTreeNode node = (TagSearchTreeNode)((DefaultMutableTreeNode) value).getUserObject();
			if (checkBoxNode.item.isSelectedTag(checkBoxNode.tag.id)) {
				checkBoxNode.item.tags.remove(checkBoxNode.tag.id);
				checkBox.setSelected(false);
			} else {
				checkBoxNode.item.tags.add(checkBoxNode.tag.id);
				checkBox.setSelected(true);
			}
			checkBox.setText(checkBoxNode.tag.name);
			return checkBox;
		}
		@Override public Object getCellEditorValue() {
			return checkBoxNode;
		}
		@Override public boolean isCellEditable(EventObject e) {
			if (e instanceof MouseEvent && e.getSource() instanceof JTree) {
				MouseEvent me = (MouseEvent) e;
				JTree tree = (JTree) me.getComponent();
				TreePath path = tree.getPathForLocation(me.getX(), me.getY());
				Object o = path.getLastPathComponent();
				if (o instanceof TreeNode) {
					TagTreeNode node = (TagTreeNode)((DefaultMutableTreeNode) o).getUserObject();
					return node.tagFlag;
				}
			}
			return false;
		}
	}
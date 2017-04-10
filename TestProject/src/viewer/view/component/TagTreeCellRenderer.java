package viewer.view.component;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import viewer.model.TagTreeNode;

public class TagTreeCellRenderer implements TreeCellRenderer {
	private final JCheckBox checkBox = new JCheckBox();
	private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		TagTreeNode node = (TagTreeNode)((DefaultMutableTreeNode) value).getUserObject();
		if (node.tagFlag) {
			checkBox.setEnabled(tree.isEnabled());
			checkBox.setFont(tree.getFont());
			checkBox.setOpaque(true);
			checkBox.setFocusable(false);
			checkBox.setText(node.name);
			checkBox.setSelected(node.selected);
			if (node.selected) {
				checkBox.setOpaque(true);
				checkBox.setBackground(Color.YELLOW);
			} else {
				checkBox.setOpaque(false);
			}
			return checkBox;
		}
		return renderer.getTreeCellRendererComponent(tree, node.name, selected, expanded, leaf, row, hasFocus);
	}
}

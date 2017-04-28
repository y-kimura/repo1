package viewer.view.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import viewer.model.TagSearchTreeNode;

public class TagSearchTreeCellRenderer extends DefaultTreeCellRenderer {
	//private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		TagSearchTreeNode node = (TagSearchTreeNode)((DefaultMutableTreeNode) value).getUserObject();
		if (node.tagFlag) {
			JLabel c = (JLabel)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			if (node.status == 0) {
				c.setIcon(new ColorIcon(Color.GRAY));
			} else if (node.status == 1) {
				c.setIcon(new ColorIcon(Color.GREEN));
			} else if (node.status == 2) {
				c.setIcon(new ColorIcon(Color.RED));
			}
			c.setText(node.name);
			return c;
		}
		return super.getTreeCellRendererComponent(tree, node.name, selected, expanded, leaf, row, hasFocus);
	}
	class ColorIcon implements Icon {
	    private final Color color;
	    protected ColorIcon(Color color) {
	        this.color = color;
	    }
	    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
	        Graphics2D g2 = (Graphics2D) g.create();
	        g2.translate(x, y);
	        g2.setPaint(color);
	        g2.fillRect(1, 1, getIconWidth() - 2, getIconHeight() - 2);
	        g2.dispose();
	    }
	    @Override public int getIconWidth() {
	        return 16;
	    }
	    @Override public int getIconHeight() {
	        return 16;
	    }
	}
}

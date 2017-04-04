package viewer.view.component;

import java.awt.Component;
import java.util.Optional;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class TagTreePopupMenu extends JPopupMenu {
	protected TreePath path;
	public TreePath getPath() {
		return path;
	}

	@Override public void show(Component c, int x, int y) {
		if (c instanceof JTree) {
			JTree tree = (JTree) c;
			//TreePath[] tsp = tree.getSelectionPaths();
			path = tree.getPathForLocation(x, y);
			//if (Objects.nonNull(path) && Arrays.asList(tsp).contains(path)) {
			Optional.ofNullable(path).ifPresent(p -> {
				tree.setSelectionPath(p);
				super.show(c, x, y);
			});
		}
	}
}
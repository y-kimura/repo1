package viewer.model;

public class TagTreeNode {
	public String name;
	public int tagId;
	public boolean tagFlag;
	public boolean selected;
	public TagTreeNode(String name, int tagId, boolean tagFlag, boolean selected) {
		this.name = name;
		this.tagId = tagId;
		this.tagFlag = tagFlag;
		this.selected = selected;
	}
}
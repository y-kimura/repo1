package viewer.model;

public class TagSearchTreeNode {
	public String name;
	public int tagId;
	public boolean tagFlag;
	public int status;
	public TagSearchTreeNode(String name, int tagId, boolean tagFlag, int status) {
		this.name = name;
		this.tagId = tagId;
		this.tagFlag = tagFlag;
		this.status = status;
	}
}
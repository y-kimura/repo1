package viewer.model;

public class TagSearchTreeNode {
	public final Tag tag;
	public final Category category;
	public final boolean tagFlag;
	public final 
	public TagSearchTreeNode(Tag tag, Item item) {
		this.tag = tag;
		this.tagFlag = true;
		this.category = null;
	}

	public TagSearchTreeNode(Category category, Item item) {
		this.tag = null;
		this.item = item;
		this.tagFlag = false;
		this.category = category;
	}

	public String getName() {
		if (tagFlag) {
			return tag.name;
		} else {
			return category.name;
		}
	}

	public void setName(String name) {
		if (tagFlag) {
			tag.name = name;
		} else {
			category.name = name;
		}
	}
}
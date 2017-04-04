package viewer.model;

public class TagTreeNode {
	public final Tag tag;
	public final Category category;
	public final boolean tagFlag;
	public final Item item;
	public TagTreeNode(Tag tag, Item item) {
		this.tag = tag;
		this.item = item;
		this.tagFlag = true;
		this.category = null;
	}

	public TagTreeNode(Category category, Item item) {
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
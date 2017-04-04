package viewer.model;

import java.util.ArrayList;
import java.util.List;

public class TagList {
	public List<Tag> tagList;
	public TagList() {
		tagList = new ArrayList<Tag>();
	}

	public void add(Tag tag) {
		tagList.add(tag);
	}

	public void add(String name, int categoryId) {
		int size = tagList.size();
		Tag tag = new Tag();
		tag.id = size;
		tag.name = name;
		tag.categoryId = categoryId;
		tagList.add(tag);
	}
}

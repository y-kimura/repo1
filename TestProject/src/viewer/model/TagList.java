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
		int maxId = 0;
		Tag tag = new Tag();
    	for (Tag tag2: tagList) {
    		if (tag2.id > maxId) {
    			maxId = tag2.id;
    		}
    	}
		tag.id = maxId + 1;
		tag.name = name;
		tag.categoryId = categoryId;
		tagList.add(tag);
	}
}

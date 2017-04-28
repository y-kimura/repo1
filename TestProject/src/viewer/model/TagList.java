package viewer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TagList {
	public List<Tag> tagList;
	public TagList() {
		tagList = new ArrayList<Tag>();
	}

	public void add(Tag tag) {
		tagList.add(tag);
	}

	public Tag getTagById(int id) {
		for (Tag tag: tagList) {
			if (tag.id == id) {
				return tag;
			}
		}
		return null;
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

	public int getMaxOrder(int categoryId) {
		int maxOrder = -1;
		for (Tag tag: tagList) {
			if (tag.categoryId == categoryId) {
				if (tag.order > maxOrder) {
					maxOrder = tag.order;
				}
			}
		}
		return maxOrder;
	}

	public void sortTagList() {
		Collections.sort(tagList, new Comparator<Tag>(){
    		public int compare(Tag i1,Tag i2){
    			if (i1.categoryId > i2.categoryId) {
    				return +1;
    			} else if (i1.categoryId < i2.categoryId) {
    				return -1;
    			}
    			return i1.order - i2.order;
    		}
    	});

		int i = 0;
		int currentCateId = -1;
		for (Tag tag: tagList) {
			if (tag.categoryId != currentCateId) {
				currentCateId = tag.categoryId;
				i = 0;
			}
			tag.order = i;
			i++;
		}
	}
}

package viewer.model;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Item implements Serializable{
	private static final long serialVersionUID = 1L;
	public String name;
	public int thumbNumber;
	public String playTime;
	public File file;
	public Set<Integer> tags = new HashSet<Integer>();
	public Map<Integer, Integer> thumbStat = new HashMap<Integer, Integer>();

	public String getName() {
		return name;
	}
	public File getFile() {
		return file;
	}
	public Set<Integer> getTags() {
		return tags;
	}
	public int getThumbNumber() {
		return thumbNumber;
	}
	public void setThumbNumber(int thumbNumber) {
		this.thumbNumber = thumbNumber;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setTags(Set<Integer> tags) {
		this.tags = tags;
	}

	public boolean isSelectedTag(int tagId) {
		if (tags.contains(tagId)) {
			return true;
		} else {
			return false;
		}
	}


}

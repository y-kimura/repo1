package viewer.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Item implements Serializable{
	private static final long serialVersionUID = 1L;
	public String name;
	public String thumbName;
	public String playTime;
	public File file;
	public Set<Integer> tags = new HashSet<Integer>();
	public List<String> sampleImageList = new ArrayList<String>();
	public String getName() {
		return name;
	}
	public String getThumbName() {
		return thumbName;
	}
	public File getFile() {
		return file;
	}
	public Set<Integer> getTags() {
		return tags;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setThumbName(String thumbName) {
		this.thumbName = thumbName;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setTags(Set<Integer> tags) {
		this.tags = tags;
	}
}

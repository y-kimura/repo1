package viewer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import viewer.model.Item;
import viewer.model.Tag;
import viewer.model.TagList;
import viewer.util.IOUtils;
import viewer.util.PropertiesUtils;
import viewer.util.PropertyBuilder;
import viewer.util.PropertyReader;
import viewer.util.PropertyWriter;
import viewer.view.conf.WindowConfig;

public class ApplicationContext {
	
	public static final String PROP_MAINWINDOW = "MainWindow";
	public static final String PROP_TAGSEARCHWINDOW = "TagSearchWindow";
	public static final String PROP_ITEMLIST = "ItemList";
	public static final String PROP_TAGLIST = "TagList";
	public static final String PROP_FILTERLIST = "filterList";
	public static final String SMB_DIR = "d:\\testtest\\ss";
	public static final String MOVIE_DIR = "d:\\testtest";
	
	
	private PropertyChangeSupport listeners;
	private Properties props;
	private File file;
	
	public int selectIndex;
	
	public ApplicationContext(File f){
        this.listeners = new PropertyChangeSupport(this);
        this.props = new Properties();
        this.file = f;
        if( file.exists() && file.canRead() && file.isFile() ){
            this.props = PropertiesUtils.load(file);
        }
		
	    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run(){
	        	finalizeItemList();
//	            finalizeHistory();
//	            finalizeImageListInfoDB();
//	            finalizeImageInfoCache();
	            finalizeMainWindowConfig();
//	            finalizeHistoryWindowConfig();
//	            finalizeTagEditorWindowConfig();
	            finalizeTagSearchWindowConfig();
//	            finalizeFilechooserConfig();
//	            finalizeImageFileFilter();
//	            finalizeRotateType();
//	            finalizeScaleType();
//	            finalizeSmoothType();
//	            finalizeThumbSize();
//	            finalizeImageBackgroundColor();
//	            finalizeImageListSizeVisible();
	            finalizeItemTagSet();
	            PropertiesUtils.save(file, props);
	        }
	    }));
	    initializeItemList();
	    initializeMainWindowConfig();
	    initializeTagSearchWindowConfig();
	    initializeItemTagSet();
	}
	
    private WindowConfig mainwindow = new WindowConfig(PROP_MAINWINDOW);
    protected void initializeMainWindowConfig(){
        mainwindow.read(props);
    }
    protected void finalizeMainWindowConfig(){
        mainwindow.sync();
        mainwindow.save(props);
    }
    public WindowConfig getMainWindowConfig(){
        return mainwindow;
    }

    public Map<String, Item> itemFullMap = new HashMap<String, Item>();
    public List<Item> filterItemList = new ArrayList<Item>();
    public List<Item> sortItemFullList = new ArrayList<Item>();
    
    public void filterItemList() {
		filterItemList = new ArrayList<Item>();
    	if (filterTagList.isEmpty()) {
        	for (Item item: sortItemFullList) {
        		filterItemList.add(item);
        	}
    	} else {
	    	for (Item item: sortItemFullList) {
	    		// or
	    		if (filterType == 0) {
	    			for (Integer id: item.tags) {
		    			if (filterTagList.contains(id)) {
		    	    		filterItemList.add(item);
		    	    		continue;
		    			}
	    			}
	    		// and
	    		} else {
	    			int flag = 0;
	    			for (int id: filterTagList) {
	    				if (!item.tags.contains(id)) {
	    					flag = 1;
	    					break;
	    				}
	    			}
	    			if (flag == 0) {
		    			filterItemList.add(item);
	    			}
	    		}
	    	}
    	}
    	listeners.firePropertyChange(PROP_ITEMLIST, null, filterItemList);
    }
    
	public void sortItemList() {
    	sortItemFullList = new ArrayList<Item>();
    	for (Item item: itemFullMap.values()) {
    		sortItemFullList.add(item);
    	}
    	Collections.sort(sortItemFullList,new Comparator<Item>(){
    		public int compare(Item i1,Item i2){

    			return (int)(i2.file.lastModified()-i1.file.lastModified());
    		}
    	});
    }

    protected void initializeItemList(){
    	
        Iterable<PropertyReader> reader = IOUtils.createPropertyReaderIterable(new File("ITEMLIST.dat"));
        for( PropertyReader pr: reader ){
        	Item item = new Item();
        	item.name = pr.stringValue("NAME", "<no-name>");
        	String tagString = pr.stringValue("TAGS", "");
        	item.tags = new HashSet<Integer>();
        	if (tagString != null && !tagString.isEmpty()) {
            	for (String i: tagString.split(",")) {
            		item.tags.add(Integer.parseInt(i));
            	}
        	}
        	itemFullMap.put(item.name, item);
        }
        
		File[] files = new File(MOVIE_DIR).listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}
			Item item = itemFullMap.get(file.getName());
			if (item != null) {
				item.file = file;
				item.thumbName = "_smb_".concat(file.getName()).concat(".png");
			} else {
				item = new Item();
				item.name = file.getName();
				item.thumbName = "_smb_".concat(file.getName()).concat(".png");
				item.file = file;
				itemFullMap.put(item.name, item);
			}
		}
		
		Map<String, Item> tmpItemFullMap = new HashMap<String, Item>(itemFullMap);
		for (Item item: tmpItemFullMap.values()) {
			if (item.file == null || !item.file.exists()) {
				itemFullMap.remove(item.name);
			}
		}
		sortItemList();
		filterItemList();
    }
    
    protected void finalizeItemList(){
        if(!itemFullMap.isEmpty()){
            List<Item> beans = new ArrayList<Item>();
            beans.addAll(itemFullMap.values());
            IOUtils.writeIterableToPropertiesFile(beans, new File("ITEMLIST.dat"), new PropertyBuilder<Item>() {
                public void build( Item bean, PropertyWriter out ){
                    out.set("NAME", bean.name);
                    String tagString = "";
                    for (int i: bean.tags) {
                    	tagString = tagString.concat(Integer.toString(i)).concat(",");
                    }
                    if (tagString.isEmpty()) {
                        out.set("TAGS", "");
                    } else {
                    	tagString = tagString.substring(0, tagString.length()-1);
                        out.set("TAGS", tagString);
                    }
                }
            });
        }
    }

//    public void setItemList(ItemList list){
//        ImageFileList old = this.viewList;
//        this.modelList = list;
//        this.viewList = build(list);
//        listeners.firePropertyChange(PROP_IMAGEFILELIST, old, this.viewList);
//        getHistory().add(list.getName(), list.getType(), list.getPath());
//    }
    
    public void addItemListChangeListener(PropertyChangeListener listener){
        listeners.addPropertyChangeListener(PROP_ITEMLIST, listener);
    }
    public void removeItemListChangeListener(PropertyChangeListener listener){
        listeners.removePropertyChangeListener(PROP_ITEMLIST, listener);
    }
    
    
    private TagList tagList = new TagList();
    protected void initializeItemTagSet(){
        Iterable<PropertyReader> reader = IOUtils.createPropertyReaderIterable(new File("TAGLIST.dat"));
        for( PropertyReader pr: reader ){
        	Tag tag = new Tag();
        	tag.id = pr.intValue("ID", 0);
        	tag.name = pr.stringValue("NAME", "<no-name>");
        	tagList.add(tag);
        }
    }
    protected void finalizeItemTagSet(){
        if(!tagList.tagList.isEmpty()){
            IOUtils.writeIterableToPropertiesFile(tagList.tagList, new File("TAGLIST.dat"), new PropertyBuilder<Tag>() {
                public void build( Tag bean, PropertyWriter out ){
                    out.set("ID", bean.id);
                    out.set("NAME", bean.name);
                }
            });
        }
    }
    public TagList getTagList(){
        return tagList;
    }
    
    public void addTagListChangeListener(PropertyChangeListener listener){
        listeners.addPropertyChangeListener(PROP_TAGLIST, listener);
    }
    public void removeTagListChangeListener(PropertyChangeListener listener){
        listeners.removePropertyChangeListener(PROP_TAGLIST, listener);
    }
    
    public void addNewTag(String name) {
    	tagList.add(name);
    	listeners.firePropertyChange(PROP_TAGLIST, null, tagList);
    }
    
    
    public List<Integer> filterTagList = new ArrayList<Integer>();
    public int filterType = 1;
    
    public void addFilterListChangeListener(PropertyChangeListener listener){
        listeners.addPropertyChangeListener(PROP_FILTERLIST, listener);
    }
    public void removeFilterListChangeListener(PropertyChangeListener listener){
        listeners.removePropertyChangeListener(PROP_FILTERLIST, listener);
    }
    
    /*-----------------------------------------------------------------------
     * TagSearchWindowInfo
     *----------------------------------------------------------------------*/
    private WindowConfig tagsearchwindow = new WindowConfig(PROP_TAGSEARCHWINDOW);
    protected void initializeTagSearchWindowConfig(){
        tagsearchwindow.read(props);
    }
    protected void finalizeTagSearchWindowConfig(){
        tagsearchwindow.sync();
        tagsearchwindow.save(props);
    }
    public WindowConfig getTagSearchWindowConfig(){
        return tagsearchwindow;
    }
}

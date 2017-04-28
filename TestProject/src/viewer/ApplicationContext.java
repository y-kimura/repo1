package viewer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import viewer.model.Category;
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

	private static final Logger log = LoggerFactory.getLogger(ApplicationContext.class);

	public static final String PROP_MAINWINDOW = "MainWindow";
	public static final String PROP_TAGSEARCHWINDOW = "TagSearchWindow";
	public static final String PROP_ITEMLIST = "ItemList";
	public static final String PROP_ITEMTAG = "ItemTag";
	public static final String PROP_TAGLIST = "TagList";
	public static final String PROP_CATEGORYLIST = "CategoryList";
	public static final String PROP_FILTERLIST = "filterList";
    public static final String PROP_FILELISTVIEWTYPE = "fileListViewType";

	public static final String TAGLIST_FILE = "data\\TAGLIST.dat";
	public static final String CATEGORYLIST_FILE = "data\\CATEGORYLIST.dat";
	public static final String ITEMLIST_FILE = "data\\ITEMLIST.dat";

	public static final String TMP_THUMB_FILE = "data\\TEMP_THUMB.png";

	public static final int MAX_THUMB = 8;

    /** リスト表示形式 */
    public static final int TYPE_VIEW_LIST = 0;
    /** テーブル表示形式 */
    public static final int TYPE_VIEW_DETAIL = 1;

	public PropertyChangeSupport listeners;
	private Properties props;
	private File file;

	public String movieDir;
	public String smbDir;

	public int selectIndex = -1;

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
	    	    finalizeCategoryList();
	            PropertiesUtils.save(file, props);
	        }
	    }));
	    initializeItemList();
	    initializeMainWindowConfig();
	    initializeTagSearchWindowConfig();
	    initializeItemTagSet();
	    initializeCategoryList();
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
    public ArrayList<Item> sortItemFullList = new ArrayList<Item>();

    public void filterItemList() {
		filterItemList = new ArrayList<Item>();
    	if (filterTagList.isEmpty() && antiFilterTagList.isEmpty()) {
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
	    			if (flag == 1)continue;
	    			for (int id: antiFilterTagList) {
	    				if (item.tags.contains(id)) {
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
    			Date date1 = new Date(i1.getFile().lastModified());
    			Date date2 = new Date(i2.getFile().lastModified());
    			return date1.compareTo(date2);
    		}
    	});
    }

    protected void initializeItemList(){

    	String[] suffixArray = {"wmv","mp4","flv"};

    	movieDir = PropertiesUtils.stringValue(props, "movieDirPath", "d:\\testtest");
    	smbDir = PropertiesUtils.stringValue(props, "smbDirPath", "d:\\testtest\\ss");

        Iterable<PropertyReader> reader = IOUtils.createPropertyReaderIterable(new File(ITEMLIST_FILE));
        for( PropertyReader pr: reader ){
        	Item item = new Item();
        	item.name = pr.stringValue("NAME", "<no-name>");
        	item.thumbNumber = pr.intValue("TNUM", 1);
        	String tagString = pr.stringValue("TAGS", "");
        	item.tags = new HashSet<Integer>();
        	if (tagString != null && !tagString.isEmpty()) {
            	for (String i: tagString.split(",")) {
            		item.tags.add(Integer.parseInt(i));
            	}
        	}
        	itemFullMap.put(item.name, item);
        }

		File[] files = new File(movieDir).listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				continue;
			}
			if (!Arrays.asList(suffixArray).contains(getSuffix(file.getName()))) {
				continue;
			}
			Item item = itemFullMap.get(file.getName());
			if (item != null) {
				item.file = file;
			} else {
				item = new Item();
				item.name = file.getName();
				item.thumbNumber = 1;//"_smb1_".concat(file.getName()).concat(".png");
				item.file = file;
				itemFullMap.put(item.name, item);
			}
		}


		Map<String, Item> tmpItemFullMap = new HashMap<String, Item>(itemFullMap);
		for (Item item: tmpItemFullMap.values()) {
			if (item.file == null || !item.file.exists()) {
				itemFullMap.remove(item.name);
				continue;
			}

//			if (!new File(smbDir + "\\" + createThumbFileName(item.name, 1)).exists()) {
//				CreateThumbUtils createThumbUtils = new CreateThumbUtils();
//				createThumbUtils.createThumb(item.file, smbDir);
//			}
		}
		sortItemList();
		filterItemList();
    }

	private static String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}

	public static String createThumbFileName(String name, int thumbNumber) {
		return name + "__smb" + thumbNumber + "__" + ".png";
	}

    protected void finalizeItemList(){
    	PropertiesUtils.set(props, "movieDirPath", movieDir);
    	PropertiesUtils.set(props, "smbDirPath", smbDir);


        if(!itemFullMap.isEmpty()){
            List<Item> beans = new ArrayList<Item>();
            beans.addAll(itemFullMap.values());
            IOUtils.writeIterableToPropertiesFile(beans, new File(ITEMLIST_FILE), new PropertyBuilder<Item>() {
                public void build( Item bean, PropertyWriter out ){
                    out.set("NAME", bean.name);
                    out.set("TNUM", bean.thumbNumber);
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

    public void addItemListChangeListener(PropertyChangeListener listener){
        listeners.addPropertyChangeListener(PROP_ITEMLIST, listener);
    }
    public void removeItemListChangeListener(PropertyChangeListener listener){
        listeners.removePropertyChangeListener(PROP_ITEMLIST, listener);
    }

    public void addItemTagChangeListener(PropertyChangeListener listener){
        listeners.addPropertyChangeListener(PROP_ITEMTAG, listener);
    }

    private TagList tagList = new TagList();
    protected void initializeItemTagSet(){
        Iterable<PropertyReader> reader = IOUtils.createPropertyReaderIterable(new File(TAGLIST_FILE));
        for( PropertyReader pr: reader ){
        	Tag tag = new Tag();
        	tag.id = pr.intValue("ID", 0);
        	tag.name = pr.stringValue("NAME", "<no-name>");
        	tag.order = pr.intValue("ORDER", 0);
        	tag.categoryId = pr.intValue("CATEGORYID", 0);
        	tagList.add(tag);
        }
    }
    protected void finalizeItemTagSet(){
        if(!tagList.tagList.isEmpty()){
            IOUtils.writeIterableToPropertiesFile(tagList.tagList, new File(TAGLIST_FILE), new PropertyBuilder<Tag>() {
                public void build( Tag bean, PropertyWriter out ){
                    out.set("ID", bean.id);
                    out.set("NAME", bean.name);
                    out.set("ORDER", bean.order);
                    out.set("CATEGORYID", bean.categoryId);
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

    public void addNewTag(String name, int categoryId) {
    	tagList.add(name, categoryId);
    	listeners.firePropertyChange(PROP_TAGLIST, null, tagList);
    }

    private List<Category> categoryList = new ArrayList<Category>();
    protected void initializeCategoryList(){
        Iterable<PropertyReader> reader = IOUtils.createPropertyReaderIterable(new File(CATEGORYLIST_FILE));
        for( PropertyReader pr: reader ){
        	Category category = new Category();
        	category.id = pr.intValue("ID", 0);
        	category.name = pr.stringValue("NAME", "<no-name>");
        	category.order = pr.intValue("ORDER", 0);
        	categoryList.add(category);
        }
        if (categoryList.size() == 0) {
	    	Category category = new Category();
	    	category.id = 0;
	    	category.name = "未分類";
	    	category.order = 0;
	    	categoryList.add(category);
        }
    }
    protected void finalizeCategoryList(){
        if(!tagList.tagList.isEmpty()){
            IOUtils.writeIterableToPropertiesFile(categoryList, new File(CATEGORYLIST_FILE), new PropertyBuilder<Category>() {
                public void build( Category bean, PropertyWriter out ){
                    out.set("ID", bean.id);
                    out.set("NAME", bean.name);
                    out.set("ORDER", bean.order);
                }
            });
        }
    }

    public List<Category> getCategoryList(){
        return categoryList;
    }

    public void addCategoryList(String name) {
    	Category category = new Category();
    	category.name = name;
    	int maxId = 0;
    	for (Category cate: categoryList) {
    		if (cate.id > maxId) {
    			maxId = cate.id;
    		}
    	}
    	category.id = maxId + 1;
    	category.order = 0;
    	categoryList.add(category);
    }

    public List<Integer> filterTagList = new ArrayList<Integer>();
    public List<Integer> antiFilterTagList = new ArrayList<Integer>();
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

    /*-----------------------------------------------------------------------
     * ファイルリストのレイアウト方式
     *----------------------------------------------------------------------*/
    private int fileListViewType = 0;
//    protected void initializefileListViewType(){
//    	fileListViewType = PropertiesUtils.intValue(props, PROP_FILELISTVIEWTYPE, 0);
//    }
//    protected void finalizeImageListViewType(){
//        PropertiesUtils.set(props, PROP_FILELISTVIEWTYPE, fileListViewType);
//    }
    public int getImageListViewType(){
        return fileListViewType;
    }
    public void setImageListViewType(int newFileListViewType ){
        int old = this.fileListViewType;
        this.fileListViewType = newFileListViewType;
        listeners.firePropertyChange(PROP_FILELISTVIEWTYPE, old, newFileListViewType);
    }
    public void addImageListViewTypeChangeListener(PropertyChangeListener listener){
        listeners.addPropertyChangeListener(PROP_FILELISTVIEWTYPE, listener);
    }
}

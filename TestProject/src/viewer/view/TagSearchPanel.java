package viewer.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import viewer.ApplicationContext;
import viewer.ApplicationController;
import viewer.ApplicationControllerListener;
import viewer.model.Item;
import viewer.model.Tag;
import viewer.model.TagList;
import viewer.view.bean.Input;

public class TagSearchPanel extends JPanel {
    
//    public static interface NewImageTagInput {
//        @Input(key="New Tag Name", index=1)
//        public String getNewTagName();
//    }
//    public static interface DeleteImageTagInput {
//        @Input(key="Tag", index=1)
//        public Code getSelectedTag();
//    }
//    public static interface EditImageTagInput {
//        @Input(key="Tag", index=1)
//        public Code getTag();
//        @Input(key="New Tag Name", index=2)
//        public String getNewTagName();
//    }
    
//    public static interface SelectionListener {
//        public void updated(TagPanel source, Set<Code> selected);
//    }
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext context;
    private ApplicationController controller;
    private JComboBox sortComboBox;
    private TagList tagList;
    private List<Integer> filterTagList;
    
    private List<JCheckBox> checkbox;
//    private List<SelectionListener> listeners;

    public TagSearchPanel(ApplicationContext context, final ApplicationController controller){
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.context = context;
        this.controller = controller;
        this.tagList = context.getTagList();
        this.filterTagList = context.filterTagList;
        
        String[] combodata = {"aaaa", "bbbb", "cccc", "dddd"};
        sortComboBox = new JComboBox(combodata);
        sortComboBox.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
        add(sortComboBox);
        
        this.checkbox = new ArrayList<JCheckBox>();
        
	    for(Tag tag: tagList.tagList) {
        	add(setCheckBox(tag.id, tag.name, false));
	    }
	    
	    this.context.addTagListChangeListener(new PropertyChangeListener() {
	        public void propertyChange( PropertyChangeEvent evt ){
            	removeAll();
        		setSize(getPreferredSize());
        		add(sortComboBox);
        	    for(Tag tag: tagList.tagList) {
        	    	if (filterTagList.contains((Integer)tag.id)) {
        	        	add(setCheckBox(tag.id, tag.name, true));
        	    	} else {
        	        	add(setCheckBox(tag.id, tag.name, false));
        	    	}
        	    }
        	    validate();
        	    repaint();
	        }
	    });
    }
    
	//JCheckBoxを生成する関数
	//引数valueはチェックボックス名
    private JCheckBox setCheckBox(final int id, String name, boolean checked){
        final JCheckBox chkbox = new JCheckBox(name, checked);

        chkbox.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // 選択されたときの動作を設定
                if(chkbox.isSelected()){
                	context.filterTagList.add(id);
                }else{
                	context.filterTagList.remove((Integer)id);
                }
                context.filterItemList();
            }
        });
        chkbox.setMargin(new Insets(0, 0, 0, 0));
        chkbox.setBackground(Color.WHITE);
        return chkbox;
    }
    

//    
//    public void addSelectionListener(SelectionListener listener){
//        listeners.add(listener);
//    }
//    public void removeSelectionListener(SelectionListener listener){
//        listeners.remove(listener);
//    }
//    protected void fireUpdated(){
//        for( SelectionListener l: listeners ){
//            l.updated(this, visibleFlags);
//        }
//    }
}
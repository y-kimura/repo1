package viewer;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import viewer.model.Item;

public class ApplicationController {
    
    private ApplicationContext context;
    private List<ApplicationControllerListener> listeners = new ArrayList<ApplicationControllerListener>();
    
    private int selectedImageIndex;
    
    public ApplicationController( ApplicationContext context ){
        this.context = context;
//        this.context.addImageFileListChangeListener(new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ){
//                selectedImageIndex = -1;
//            }
//        });
    }


    public void addListener(ApplicationControllerListener listener){
        listeners.add(listener);
    }
    public void removeListener(ApplicationControllerListener listener){
        listeners.remove(listener);
    }
    
    public void fireSelectedIndexChanged(Item item){
        for( ApplicationControllerListener l: listeners ){
            l.selectedIndexChanged(item);
        }
    }
}
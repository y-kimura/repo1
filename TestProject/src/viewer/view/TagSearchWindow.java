package viewer.view;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import viewer.ApplicationContext;
import viewer.ApplicationController;

public class TagSearchWindow extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ApplicationContext context;
    
    private TagSearchPanel tagPanel;
    private JPanel controller;
    
    public TagSearchWindow( ApplicationContext context, final ApplicationController controller){
        this.context = context;
        this.tagPanel = new TagSearchPanel(context, controller);
        this.tagPanel.setLayout(new BoxLayout(tagPanel, BoxLayout.PAGE_AXIS));
        getContentPane().add(tagPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        //setUndecorated(true);//ëïè¸ÇÃñ≥å¯âªÇ∑ÇÈ

    }
    
    public TagSearchPanel getImageTagPanel(){
        return tagPanel;
    }
//    public void setControllBar(Action ... actions){
//        if( controller != null ){
//            getContentPane().remove(controller);
//        }
//        JButton[] buttons = new JButton[actions.length];
//        for( int i=0; i<actions.length; i++ ){
//            buttons[i] = new JButton(actions[i]);
//        }
//        controller = ButtonBarFactory.buildRightAlignedBar(buttons);
//        getContentPane().add(controller, BorderLayout.SOUTH);
//    }
    
}

package github.acodervic.mod.swing.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JScrollPane;
import javax.swing.Timer;

public class ScrolTimer  extends Timer  {
     ArrayBlockingQueue<ScrolTimer> upTimers ;

    Boolean isDown=true;//标识是向下还是向上滚动
    JScrollPane jScrollPane;
    private ScrolTimer(int delay, ActionListener listener) {
        super(delay, listener);
     }

public  ScrolTimer(ArrayBlockingQueue<ScrolTimer> upTimers,JScrollPane jScrollPane,Boolean isDown,int delay) {
        super(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int nowHeight = jScrollPane.getVerticalScrollBar().getValue();
                //判断正向和反向
                int add=0;
                if (upTimers.size()==0) {
                    add=1;//确保最少有一个像素的变化
                }else{
                     add=2;
                }
                //判断正向和反向
                if (isDown) {
                    //向下滚动
                    int targetHeight = nowHeight+(upTimers.size()+add);
                    jScrollPane.getVerticalScrollBar().setValue(targetHeight);
                }else{
                     //向上滚动
                     int targetHeight = nowHeight-(upTimers.size()+add);
                     jScrollPane.getVerticalScrollBar().setValue(targetHeight);        
                }
            }
            
        } );
        this.isDown=isDown;
        this.jScrollPane=jScrollPane;
        this.upTimers=upTimers;

 
     }
     
     public Boolean isDown() {
         return isDown;
     }

     public boolean isUp() {
         return !isDown();
     }
 
 
    
}

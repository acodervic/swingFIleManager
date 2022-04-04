package github.acodervic.mod.swing.panel;

import java.awt.Component;
import java.awt.event.MouseWheelListener;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.swing.table.MyTable;

 
/**
 * 扩展 JScrollPane 加入了拖动和滚动动画
 */
public class MyJScrollPane extends JScrollPane implements UtilFunInter {
    ArrayBlockingQueue<ScrolTimer> timers = new ArrayBlockingQueue<>(9999);// 动画列表
    MyJScrollPane me;
    Value downScoll = new Value(true);// 标识当前滚动条的滚动方向
    Thread processAmimaTrhead;
    MouseWheelListener scrollAnimaAction = obj -> {
        // 确保只对来自 JScrollPane 的滚动i条事件绘制动画
        if (obj.getSource() == getMe()) {
            int wheelRotation = obj.getWheelRotation();
            Boolean isDown = (wheelRotation == 1);
            ScrolTimer timer = new ScrolTimer(timers, getMe(), isDown, 5);
            if (isDown != downScoll.getBoolean()) {
                // 代表方向已经改变
                timers.removeIf(tm -> {// 删除反方向的动画
                    return tm.isDown() != isDown;
                });
            }
            downScoll.setValue(isDown);
            try {
                timers.put(timer);
            } catch (Exception e) {
            }
        }
    };

    DragScrollListener dragScrollAnima=new DragScrollListener(this);//拖动动画监听
    Component view;

    public MyJScrollPane(Component view) {
        super(view);
        this.view=view;
    }

    public MyJScrollPane() {
        super();
    }

    /**
     * @param view the view to set
     */
    public void setViewportView(Component view) {
        super. setViewportView(view);
        this.view = view;
    }

    public MyJScrollPane(Component view,boolean scrollAnima,boolean dragScrollAnima ) {
        super(view);
        this.view=view;
        if (scrollAnima) {
            enableScrollAnima();
        }
        if (dragScrollAnima) {
            enableDragScrollAnima();
        }
    }

    /**
     * @return the me
     */
    MyJScrollPane getMe() {
        if (isNull(me)) {
            me = this;
        }
        return me;
    }

    public void enableScrollAnima() {
        removeMouseWheelListener(scrollAnimaAction);
        addMouseWheelListener(scrollAnimaAction);
        if (notNull(processAmimaTrhead)) {
            processAmimaTrhead.stop();
        }
        timers.clear();
        if (view instanceof MyTable) {
            ((MyTable)view).setScrollableUnitIncrement(0);//为了防止第一次的滚动卡吨效果,设置mytable的滚动像素为0
        }
        // 重新启动线程
        processAmimaTrhead = new Thread(() -> {
            while (true) {
                try {
                    ScrolTimer take = timers.take();
                    SwingUtilities.invokeLater(() -> {
                        take.start();
                    });
                    Thread.sleep(60);
                    take.stop();
                } catch (Exception e) {
                }

            }
        });
        processAmimaTrhead.start();
    }


    public void enableDragScrollAnima() {
		DragScrollListener dl = new DragScrollListener(view);
        //给视口组件绑定动画
		view.addMouseListener(dl);
		view.addMouseMotionListener(dl);
    }
}

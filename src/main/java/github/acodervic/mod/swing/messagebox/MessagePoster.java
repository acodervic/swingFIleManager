package github.acodervic.mod.swing.messagebox;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.swing.notify.model.Message;

/**
 * 消息投递器,用于显示消息
 */
public class MessagePoster implements UtilFunInter {
    ImageIcon icon;
    String title;
    String info;
    String more;
    Boolean showing = false;// 是否一直在监听显示中
    int showTime = 2000;// 显示2s默认不包含动画
    int messageFrameHight = 140;
    int messageFrameWidth = 450;
    int animateTime = 350;
    int screenResolutionMaxWidth = 1920;
    int showInHeight = 50;
    int iconWidth = 80;
    ImageIcon defaultIcon = new ImageIcon(MessagePoster.class.getClassLoader().getResource("message.png"));
    ArrayBlockingQueue<Message> messageBlockingQueue = new ArrayBlockingQueue<>(9999);

    public void startPost() {
        if (!showing) {
            showing = true;
            new Thread(() -> {
                while (showing) {
                    if (showing) {
                        // 尝试从队列中获取消息
                        try {
                            Message msg = messageBlockingQueue.take();
                            msg.setShowedDate(new Date());
                            // 显示消息
                            MessageBoxFrame newMessageFrame = new MessageBoxFrame(msg, messageFrameWidth,
                                    messageFrameHight, iconWidth, defaultIcon);
                            newMessageFrame.setLocation(screenResolutionMaxWidth, showInHeight);
                            Rectangle right = new Rectangle(screenResolutionMaxWidth, showInHeight);
                            Rectangle left = new Rectangle(screenResolutionMaxWidth - newMessageFrame.getWidth() - 20,
                                    showInHeight);
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Animate animate = new Animate(newMessageFrame, right, left, animateTime);
                                    animate.start();
                                }
                            });
                            sleep(animateTime);// 等待动画
                            sleep(showTime);
                            while (newMessageFrame.isDontCose()) {
                                // 查看鼠标是否进入
                                sleep(500);
                            }
                            // 开始消失动动画
                            EventQueue.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Animate animate = new Animate(newMessageFrame, left, right, animateTime);
                                    animate.start();
                                }
                            });
                            sleep(animateTime);
                            sleep(300);
                            clearJfream(newMessageFrame);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            ;
        }

    }

    void clearJfream(JFrame jf) {
        jf.setVisible(false);
        jf = null;
    }

    public MessagePoster() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenResolutionMaxWidth = (int) screenSize.getWidth();
        startPost();
    }

    /**
     * 显示一个消息
     * 
     * @param msg
     * @return
     */
    public boolean show(Message msg) {
        try {
            messageBlockingQueue.put(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMouseWithinComponent(Component c) {
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        Rectangle bounds = c.getBounds();
        bounds.setLocation(c.getLocationOnScreen());
        return bounds.contains(mousePos);
    }

    public static class Animate {

        private JFrame frame;
        private Rectangle from;
        private Rectangle to;
        int time;
        private long startTime;

        public Animate(JFrame panel, Rectangle from, Rectangle to, int time) {
            this.frame = panel;
            this.from = from;
            this.to = to;
            this.time = time;
        }

        public void start() {
            // 先设置到初始位置
            // frame.setLocation(((int)from.getWidth()),(int)from.getHeight());
            Timer timer = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    long duration = System.currentTimeMillis() - startTime;
                    double progress = (double) duration / (double) time;
                    if (progress > 1f) {
                        progress = 0;
                        ((Timer) e.getSource()).stop();
                    } else {
                        if (progress > 0.0) {
                            Rectangle target = calculateProgress(from, to, progress);
                            // frame.setSize(180, 450);
                            // System.out.println(target.getWidth()+" "+target.getHeight()+" "+progress);
                            frame.setLocation((int) target.getWidth(), (int) target.getHeight());
                            frame.setVisible(true);
                        }
                    }

                }
            });
            timer.setRepeats(true);
            timer.setCoalesce(true);
            timer.setInitialDelay(0);
            startTime = System.currentTimeMillis();
            timer.start();
        }
    }

    public static Rectangle calculateProgress(Rectangle startBounds, Rectangle targetBounds, double progress) {
        Rectangle bounds = new Rectangle();
        if (startBounds != null && targetBounds != null) {
            bounds.setLocation(calculateProgress(startBounds.getLocation(), targetBounds.getLocation(), progress));
            bounds.setSize(calculateProgress(startBounds.getSize(), targetBounds.getSize(), progress));
        }
        return bounds;
    }

    public static Point calculateProgress(Point startPoint, Point targetPoint, double progress) {
        Point point = new Point();
        if (startPoint != null && targetPoint != null) {
            point.x = calculateProgress(startPoint.x, targetPoint.x, progress);
            point.y = calculateProgress(startPoint.y, targetPoint.y, progress);

        }
        return point;

    }

    public static int calculateProgress(int startValue, int endValue, double fraction) {
        int value = 0;
        int distance = endValue - startValue;
        value = (int) Math.round((double) distance * fraction);
        value += startValue;
        return value;

    }

    public static Dimension calculateProgress(Dimension startSize, Dimension targetSize, double progress) {
        Dimension size = new Dimension();
        if (startSize != null && targetSize != null) {
            size.width = calculateProgress(startSize.width, targetSize.width, progress);
            size.height = calculateProgress(startSize.height, targetSize.height, progress);
        }
        return size;
    }

}

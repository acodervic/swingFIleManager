package github.acodervic.mod.swing;


import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.awt.Component;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.table.User;

public class MessageBox {

    /**
     * 显示消息框
     * 
     * @param parentComponent_opt
     * @param message
     */
    public static void showMessageDialog(Component parentComponent_opt, String message) {
        nullCheck(message);
        JOptionPane.showMessageDialog(parentComponent_opt, message);
    }

    /**
     * 显示消息框
     * 
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     * @param messageOption
     */
    public static void showMessageDialog(Component parentComponent_opt, String title_opt, String message,
            int messageOption) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        JOptionPane.showMessageDialog(parentComponent_opt, title_opt, message, messageOption);
    }

    /**
     * 显示警告消息框
     *
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     */
    public static void showWarringMessageDialog(Component parentComponent_opt, String title_opt,
            String message) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        showMessageDialog(parentComponent_opt, message, title_opt, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * 显示异常消息框
     * 
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     */
    public static void showErrorMessageDialog(Component parentComponent_opt, String title_opt,
            String message) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        showMessageDialog(parentComponent_opt, message, title_opt, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示信息消息框
     * 
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     */
    public static void showInfoMessageDialog(Component parentComponent_opt, String title_opt, String message) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        showMessageDialog(parentComponent_opt, message, title_opt, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示确认消息框
     * 
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     * @param messageOption
     * @return
     */
    public static Boolean showConfirmDialog(Component parentComponent_opt, String title_opt, String message,
            int messageOption) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        int ret = JOptionPane.showConfirmDialog(parentComponent_opt, message, title_opt, messageOption); // 返回值为0或1
        if (ret == -1) {
            return null;
        }
        if (ret == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 显示确认错误消息框
     *
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     * @return
     */
    public static Boolean showConfirmErrorDialog(Component parentComponent_opt, String title_opt,
            String message) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        return showConfirmDialog(parentComponent_opt, title_opt, message, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 显示信息消息框
     * 
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     * @return
     */
    public static Boolean showConfirmInfoDialog(Component parentComponent_opt, String title_opt,
            String message) {
        nullCheck(message);
        if (title_opt == null) {
            title_opt = "标题";
        }
        return showConfirmDialog(parentComponent_opt, title_opt, message, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示下拉消息框
     *
     * @param <T>
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     * @param optoins
     * @param defultOptionIndex
     * @return
     */
    public static Opt<Object> showComboxDialog(Component parentComponent_opt, String title_opt, String message,
            List  optoins, int defultOptionIndex) {
        nullCheck(message, optoins);
        if (title_opt == null) {
            title_opt = "标题";
        }
        Object defultOption = null;
        if (optoins.size() > defultOptionIndex) {
            defultOption = optoins.get(defultOptionIndex);
        }
        return  new Opt<Object>(JOptionPane.showInputDialog(parentComponent_opt, message, title_opt,
        JOptionPane.PLAIN_MESSAGE, null, optoins.toArray(), defultOption));
    }

    public static void main(String[] args) {
        showComboxDialog(null, "","asdas" , utilFun.newList(new User("1", 0),new User("2", 0)) ,0);
    }
      /**
     * 显示输入消息框
     * 
     * @param parentComponent_opt
     * @param title_opt
     * @param message
     * @param defaultString_opt
     * @param defultOptionIndex
     * @return
     */
      public static String showInputDialog(Component parentComponent_opt, String title_opt, String message,
            String defaultString_opt, int defultOptionIndex) {
          nullCheck(message);
          if (title_opt == null) {
            title_opt = "标题";
        }
        return (String) JOptionPane.showInputDialog(parentComponent_opt, message, title_opt,
                JOptionPane.OK_CANCEL_OPTION, null, null, defaultString_opt);
    }

    public static str showMultilineInputDialog(final String prompt) {
        return showMultilineInputDialog("输入内容", prompt);
    }

    /**
     * 显示多行文本输入框
     * 
     * @param title
     * @param prompt
     * @return
     */
    public static str showMultilineInputDialog(final String title, final String prompt) {
        return showMultilineInputDialog(title, prompt, "");
    }

    /**
     * 显示多行文本输入框
     * 
     * @param title
     * @param prompt
     * @param content
     * @return
     */
    public static str showMultilineInputDialog(final String title, final String prompt, final String content) {
        return showMultilineInputDialog(null, title, prompt, content);
    }

    /**
     * 显示多行文本输入框
     * 
     * @param parent
     * @param title
     * @param prompt
     * @param content
     * @return
     */
    public static str showMultilineInputDialog(final Component parent, final String title, final String prompt,
            final String content) {
        final JTextArea text = new JTextArea(content, 20, 40);
        JOptionPane pane = new JOptionPane(new Object[] { prompt, new JScrollPane(text) }, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        pane.setWantsInput(false);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.pack();
        dialog.setVisible(true);
        Integer value = (Integer) pane.getValue();
        if (value == null || value.intValue() == JOptionPane.CANCEL_OPTION
                || value.intValue() == JOptionPane.CLOSED_OPTION) {
            return new str("");
        }
        return new str(text.getText());
    }

}

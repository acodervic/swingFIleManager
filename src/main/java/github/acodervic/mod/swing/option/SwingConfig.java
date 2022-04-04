package github.acodervic.mod.swing.option;

import static github.acodervic.mod.utilFun.bytesToObj;
import static github.acodervic.mod.utilFun.fileres;
import static github.acodervic.mod.utilFun.json_toStr;
import static github.acodervic.mod.utilFun.objToBytes;
import static github.acodervic.mod.utilFun.print;
import static github.acodervic.mod.utilFun.str;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.option.Option.ControlUi;
import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;

/**
 * 抽象配置实体,需要生成ui的配置类需要继承此对象,然后在配置初始化后调用initConfig将所有的配置变量都添加到options中
 */
public abstract class SwingConfig implements Serializable {
    private static final long serialVersionUID = 1453421496247866053L;
    LinkedList<Option> options = new LinkedList<Option>();
    boolean initConfig = false;// 配置的option只应该被加载一次

    /**
     * 从文件中反序列化加载配置
     *
     * @param <T>
     * @param optionFile
     * @param c
     * @return
     */
    public static <T> Opt<T> loadOptions(File optionFile, Class<T> c) {
        Opt<T> config = new Opt<T>();
        if (!optionFile.exists()) {
            return config;
        }
        return bytesToObj(fileres(optionFile).readBytes(), c);
    }

    /**
     * 将配置和内容持久化保存为文件
     * 
     * @param file
     * @return
     */
    public static boolean saveToFile(File file, SwingConfig config) {
        FileRes configFile = fileres(file);
        configFile.delete();
        configFile.writeByteArray(objToBytes(config));
        return configFile.exists();
    }
    /**
     * config对象的初始化方法,必须实现,然后在内部通过调用addOption将子类的选项参数添加到options中
     */
    public abstract void initConfig();

    /**
     * @return the options
     */
    public LinkedList<Option> getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    protected void setOptions(LinkedList<Option> options) {
        this.options = options;
    }

    /**
     * 根据配置参数构建一个没有按钮的面板
     * 
     * @param lableMaxWidth         参数名标签的最大宽度
     * @param inputMaxWidth         输入控件最大宽度
     * @param onOptionChangeFun_opt 在参数控件更改时候会被调用,如果返回true,控件值则会被自动填充
     * @return
     */
    public JPanel generateOptionsUIPanelNoButton(int lableMaxWidth, int inputMaxWidth,
            BiFunction<Option, Object, Boolean> onOptionChangeFun_opt) {
        return generateOptionsUIPanel(lableMaxWidth, inputMaxWidth, false, null, null, onOptionChangeFun_opt);
    }

    /**
     * 根据options生成ui面板,注意,当控件的ui更改后,配置的值会被自动的填充到config对象上,所以可以不通过ok按钮就可以获取到更改的config对象
     * 
     * @param lableMaxWidth         参数名标签的最大宽度
     * @param inputMaxWidth         输入控件最大宽度
     * @param hasButton             是否生成按钮
     * @param onOk_opt              当前点击成功按钮时候的处理函数
     * @param onCenel_opt           当前点击关闭按钮的时候的处理函数
     * @param onOptionChangeFun_opt 在参数控件更改时候会被调用,如果返回true,控件值则会被自动填充
     * @return
     */
    public JPanel generateOptionsUIPanel(int lableMaxWidth, int inputMaxWidth, boolean hasButton,
            Consumer<SwingConfig> onOk_opt, Runnable onCenel_opt,
            BiFunction<Option, Object, Boolean> onOptionChangeFun_opt) {
        // 自动初始化配置
        if (!initConfig) {
            initConfig();
            initConfig = true;
        }
        JPanel configPanel = new JPanel();
        Box optionsGourp = Box.createVerticalBox();// 创建一个水平箱子
        JPanel buttonPanel = new JPanel();
        // 创建一个水平箱子
        for (Option option : options) {
            JPanel optionP = new JPanel();
            JLabel jLabel = new JLabel(option.getName());
            jLabel.setPreferredSize(new Dimension(lableMaxWidth, 21));
            optionP.add(jLabel);
            // 根据option配置生成Ui控件
            if (option.getType() == ControlUi.TEXTFILED) {
                JTextField optionUi = new JTextField(str(option.getValue()).to_s());
                optionUi.setPreferredSize(new Dimension(inputMaxWidth, 21));
                optionP.add(optionUi);
                option.getComponent().of(optionUi);
                optionUi.addFocusListener(new FocusListener() {

                    @Override
                    public void focusGained(FocusEvent e) {

                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                            if (onOptionChangeFun_opt == null
                                    || onOptionChangeFun_opt.apply(option, optionUi.getText())) {
                                option.setValue(optionUi.getText());
                            } else {
                                optionUi.setText(str(option.getValue()).to_s());
                            }
                            return null;
                        }));

                    }

                });
            } else if (option.getType() == ControlUi.COMBOX) {
                JComboBox optionUi = new JComboBox<>();
                option.getEnumValue().forEach(enumvalue -> {
                    // TODO 从comboxOrRaioRander函数中获取显示文本
                    optionUi.addItem(enumvalue);
                });
                optionUi.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                                // 当值更改后填充
                                if (onOptionChangeFun_opt == null
                                        || onOptionChangeFun_opt.apply(option, optionUi.getSelectedItem())) {
                                    option.setValue(optionUi.getSelectedItem());
                                } else {
                                    optionUi.setSelectedItem(option.getValue());
                                }
                                return null;
                            }));
                        }
                    }
                });
                optionUi.setPreferredSize(new Dimension(inputMaxWidth, 21));
                optionUi.setSelectedItem(option.getValue());
                option.getComponent().of(optionUi);
                optionP.add(optionUi);

            } else if (option.getType() == ControlUi.FILECHOSE) {
                JButton button = new JButton("选择文件");
                if (str(option.getValue()).notEmpty()) {
                    button.setText(str(option.getValue()).to_s());
                }
                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser jfc = new JFileChooser();
                        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                        jfc.showDialog(new JLabel(), "选择");
                        File file = jfc.getSelectedFile();
                        String filePath = file.getAbsolutePath();
                        if (file.isDirectory()) {
                            print("文件夹:" + filePath);
                        } else if (file.isFile()) {
                            print("文件:" + filePath);
                        }
                        // 当值更改后填充
                        if (onOptionChangeFun_opt == null
                                || onOptionChangeFun_opt.apply(option, new FileRes(filePath))) {
                            // 填充option
                            option.setValue(filePath);
                            button.setText(file.getName());
                        } else {
                            if (str(option.getValue()).notEmpty()) {
                                button.setText(str(option.getValue()).to_s());
                            } else {
                                button.setText("选择项检测未通过");
                            }
                        }

                    }
                });
                button.setPreferredSize(new Dimension(inputMaxWidth, 21));
                option.getComponent().of(button);
                optionP.add(button);

            } else if (option.getType() == ControlUi.MULTIPLERADIO) {

                HashMap<JCheckBox, Object> jCheckBoxbindEnumValue = new HashMap<JCheckBox, Object>();
                // 代表目标控件类型是多选的按钮
                List<JCheckBox> jcheckBoxList = new ArrayList<JCheckBox>();
                option.getEnumValue().forEach(e -> {
                    String showText = null;
                    if (option.getComboxOrRaioRander() != null) {
                        try {
                            showText = option.getComboxOrRaioRander().apply(e).toString();
                        } catch (Exception e1) {
                        }
                    }
                    if (showText == null) {
                        showText = e.toString();
                    }
                    // 调用枚举值的toString函数
                    JCheckBox jb = new JCheckBox(showText);
                    jCheckBoxbindEnumValue.put(jb, e);
                    jcheckBoxList.add(jb);
                    // 如果当前 循环的枚举值在默认值中,则默认选中
                    if (option.getValue() instanceof List) {
                        List valueDef = (List) option.getValue();
                        for (Object ob : valueDef) {
                            if (e == ob) {
                                jb.setSelected(true);
                            }
                        }
                    }

                    optionP.add(jb);
                    option.getComponent().of(jcheckBoxList);
                    jb.addItemListener(obj -> {
                        // 在选中单选时候通知判断
                        if (jb.isSelected() && onOptionChangeFun_opt != null) {
                            if (!onOptionChangeFun_opt.apply(option, e)) {
                                // 说明客户端判断没通过
                                jb.setSelected(false);// 取消绑定
                                return;// 不再处理
                            }
                        }
                        // 当每次选中状态更改后自动刷新当前选中值到 option的value中
                        Object value = option.getValue();
                        List values = new ArrayList<>();
                        if (value == null) {
                            option.setValue(new ArrayList<>());
                        } else {
                            values = (List) value;
                        }
                        // 操作选项内部的值
                        values.clear();
                        for (JCheckBox jCheckBox : jcheckBoxList) {
                            // 重新扫描所有选中的单选按钮
                            if (jCheckBox.isSelected()) {
                                // 重新添加枚举到选项list
                                values.add(jCheckBoxbindEnumValue.get(jCheckBox));
                            }
                        }
                        utilFun.print("选项" + option.getName() + "已经更改:多选值" + json_toStr(option.getValue()));
                    });
                });
            }
            optionsGourp.add(optionP);

        }

        // 设置按钮
        if (hasButton) {
                JButton ok = new JButton("确认");
                ok.setPreferredSize(new Dimension(inputMaxWidth, 21));
                ok.addActionListener(obj -> {
                    FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                        onOk_opt.accept(this);
                        return null;
                    }));
                });
                JButton canel = new JButton("取消");
                canel.addActionListener(obj -> {
                    FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                        onCenel_opt.run();
                        return null;
                    }));
                });
                canel.setPreferredSize(new Dimension(inputMaxWidth, 21));
                buttonPanel.add(ok);
                buttonPanel.add(canel);
                optionsGourp.add(buttonPanel);
            }

        configPanel.add(optionsGourp);
        return configPanel;
    }

    /**
     * 添加选项
     * 
     * @param option
     * @return
     */
    protected SwingConfig addOption(Option option) {
        this.options.add(option);
        return this;
    }

    public void name() {

    }

    public static void main(String[] args) {
        String a="3";
        switch (a) {
            case "1":
            case "2":
                case "3":
            System.out.println("asdsad");
                break;
            default:
                break;
        }
    }
}
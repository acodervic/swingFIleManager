package github.acodervic.mod.swing.combox;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.map_keyList;
import static github.acodervic.mod.utilFun.notNull;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

public class ListBindComboBoxModel<T> extends DefaultComboBoxModel {
    // 绑定的数据
    Hashtable<String, List<T>> bindDataMap = new Hashtable<String, List<T>>();
    T nowSelectedItem = null;
    int nowSelectedIndex = -1;
    String nowBindArrayListName = "";
    Consumer<T> onAddFunction = null;
    Consumer<T> onRemoveFunction = null;
    Function<T, String> itemDisplayFunction = null;// 显示选中项的 显示值的函数
    private static final String CLEAR = "clear";
    ArrayList<T> comboxEmptyDataList = new ArrayList<T>();// 空的数据集用于暂时清空表格的时候显示

    public ListBindComboBoxModel(List<T> data) {
        nullCheck(data);
        // 判断是否存在
        addDataSrouces(null, data);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (!this.bindDataMap.containsKey(CLEAR)) {
            this.bindDataMap.put(CLEAR, comboxEmptyDataList);

        }
    }

    /**
     * 绑定数据源(添加数据源则刷新表格),如果数据源不存在则自动调用addDataSrouces添加到数据源maps,如果数据源已经先前被绑定到table,则调用swich切换到数据源并刷新表格
     *
     * @param souces
     * @return
     * @return
     */
    public String bindDataSources(List<T> souces) {
        nullCheck(souces);
        List<String> dataSrouceKeys = map_keyList(this.bindDataMap);
        for (int i = 0; i < dataSrouceKeys.size(); i++) {

            if (souces == this.bindDataMap.get(dataSrouceKeys.get(i))) {
                // 代表书数据源已经存在
                // 自动切换到上面
                switchListSource(dataSrouceKeys.get(i));
                return this.nowBindArrayListName;
            }
        }
        // 数据源不存在
        String sroucesName = addDataSrouces(null, souces);
        if (sroucesName != null) {
            switchListSource(sroucesName);
        }
        return this.nowBindArrayListName;
    }

    /**
     * 添加一个数据源
     *
     * @param name
     * @param data
     * @return
     */
    public String addDataSrouces(String name_opt, List<T> data) {
        nullCheck(data);
        if (name_opt == null) {
            // 判断是否存在
                 List<String> map_keyList = map_keyList(this.bindDataMap);
                for (int i = 0; i < map_keyList.size(); i++) {
                    String key = map_keyList.get(i);
                    List<T> arrayList = this.bindDataMap.get(key);
                    if (arrayList == data) {
                        switchListSource(key);
                        return key;
                    }
                }
                 // 生成key
                 name_opt = System.identityHashCode(data) + "";
         } else {
            removeDataResource(name_opt);
        }
        if (name_opt != null) {
            this.bindDataMap.put(name_opt, data);
            switchListSource(name_opt);
        }
        return name_opt;
    }

    /**
     * 设置当前绑定的list数据源名称
     *
     * @param name
     */
    public void switchListSource(String name) {
        this.comboxLastDataList = getNowBindList();
        this.nowBindArrayListName = name;
        //由于已经切换了数据集
        //index指向可能会发生错误,自动指向到-1
        this.nowSelectedIndex=-1;
        this.nowSelectedItem=null;
    }

    /**
     * 删除一个数据源
     *
     * @param name
     */
    public void removeDataResource(String name) {
        if (this.bindDataMap.containsKey(name)) {
            this.bindDataMap.remove(name);
        }
    }

    /**
     * 从数据源构建一个下拉框模式
     *
     * @param data
     * @param name
     */
    public ListBindComboBoxModel(String name_opt, List<T> data_opt) {
        if (data_opt != null) {
            removeDataResource(name_opt);
            // 加入数据
            addDataSrouces(name_opt, data_opt);
        }
        init();
    }

    /**
     *
     * @return
     */
    public List<T> getNowBindList() {
        return this.bindDataMap.get(nowBindArrayListName);
    }

    @Override
    public int getSize() {
        List<T> nowBindList = getNowBindList();
        if (nowBindList == null) {
            return 0;
        }
        return nowBindList.size();
    }

    @Override
    public Object getElementAt(int index) {
        if (index==-1) {
            return null;
        }
        return getNowBindList().get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        if (onAddFunction != null) {
            onAddFunction.accept(nowSelectedItem);
        }
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        if (onRemoveFunction != null) {
            onRemoveFunction.accept(nowSelectedItem);
        }
    }

    /**
     * 手动设置绑定项
     */
    @Override
    public void setSelectedItem(Object anItem) {
        super.setSelectedItem(anItem);
            if (anItem == null) {
                this.nowSelectedIndex = -1;
                this.nowSelectedItem = null;
            } else if (getNowBindList() != null) {
                if (getNowBindList().contains(anItem)) {
                    // 找到 anItem
                    for (int i = 0; i < getNowBindList().size(); i++) {
                        T t = getNowBindList().get(i);
                        if (t == anItem) {
                            // 设置当前选中项
                            this.nowSelectedIndex = i;
                            this.nowSelectedItem = t;
                            return;
                        }
                    }
                }
            }
    }

    /**
     * @return the nowSelectedIndex
     */
    public int getNowSelectedIndex() {
        return nowSelectedIndex;
    }

    /**
     * 根据实体获取在列表中的下标
     * @param item
     * @return
     */
    public int  getIndexByItem(T item) {
        return getNowBindList().indexOf(item);
    }

    @Override
    public Object getSelectedItem() {
        if (nowSelectedIndex == -1) {
            return null;
        }
        List<T> nowBindList = getNowBindList();

        if (nowSelectedIndex != -1&&notNull(nowBindList) && (nowBindList.size() - 0 < nowSelectedIndex)) {
            nowSelectedItem = nowBindList.get(nowSelectedIndex);
            return nowSelectedIndex;
        }
        return this.nowSelectedItem;
    }

    /**
     *
     */
    public ListBindComboBoxModel() {
        init();
    }

    /**
     * 清空所有的combox(将当前显示绑定到空列表数据)
     */
    public void setEmpty() {
        switchListSource(CLEAR);
    }
    @Override
    public void addElement(Object anObject) {
        List<T> nowBindList = getNowBindList();
        if (anObject!=null&&nowBindList!=null&&!nowBindList.contains(anObject)) {
            nowBindList.add((T)anObject);
        }else{
             super.addElement(anObject);
        }
    }

    /**
     * 设置当前选中项的显示值的函数,否则调用toString()
     *
     * @param setItemDisplayFunction the setItemDisplayFunction to set
     */
    public void setSetItemDisplayFunction(Function<T, String> itemDisplayFunction) {
        this.itemDisplayFunction = itemDisplayFunction;
    }

    List<T> comboxLastDataList = new ArrayList<T>();// 上次绑定的List

}

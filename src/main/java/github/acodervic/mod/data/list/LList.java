package github.acodervic.mod.data.list;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.db.anima.Model;

public class LList<T> extends LinkedList<T> {
    Hashtable<String, T> dataMap = new Hashtable<>();
    Opt<Consumer<T>> onAddCallFun=new Opt<>();//当调用add的时候的函数
    Opt<Consumer<T>> onRemoveCallFun=new Opt<>();//当调用remove的时候的函数

    /**
     * @param onAddCallFun the onAddCallFun to set
     */
    public void setOnAddCallFun(Consumer<T> onAddCallFun) {
        this.onAddCallFun.of(onAddCallFun);
    }


    /**
     * @param onRemoveCallFun the onRemoveCallFun to set
     */
    public void setOnRemoveCallFun(Consumer<T> onRemoveCallFun) {
        this.onRemoveCallFun.of(onRemoveCallFun);
    }
    /**
     * 读取集合的最后一个
     *
     * @return
     */
    public T getLast() {
        if (this.size() == 0) {
            return null;
        }
        return this.get(this.size() - 1);
    }

    /**
     * 读取集合的距离最后一个的偏移n的元素 如n为1,则获取倒数地一个,也就是最后一个,若n为2则获取倒数第二个
     *
     * @return
     */
    public T getLastOffset(int ofsetLast) {
        if (this.size() == 0) {
            return null;
        }
        if ((this.size() - ofsetLast) < 0) {
            return null;
        }
        return this.get(this.size() - ofsetLast);
    }

    /**
     * 获取集合的第一个元素
     * 
     * @return
     */
    public T getFirst() {
        if (this.size() == 0) {
            return null;
        }
        return this.get(0);
    }

    /**
     * 添加一个元素到list中,注意如果元素为Model子类,则会自动尝试提取id到内置map中,方便快速根据id获取数据getDataMap
     */
    @Override
    public boolean add(T e) {
        boolean ok = super.add(e);
        if (e instanceof Model) {
            // 如果是model则尝试自动提取主键 toString 并存放到map中
            ((Model) e).getPrimaryKeyValue().ifNotNull_(key -> {
                this.dataMap.put(key.toString(), e);
            });
        }
        onAdd(e);
        return ok;
    }

     void onAdd(T e) {
        if (onAddCallFun.notNull_()) {
            onAddCallFun.get().accept(e);
        }
    }


    void onRmove(T e) {
        if (onRemoveCallFun.notNull_()) {
            onRemoveCallFun.get().accept(e);
        }
    }
    /**
     * 删除一个元素,如果被删除的元素为Model子类且有主键,则会自动从dataMap删除结果
     */
    @Override
    public boolean remove(Object o) {
        boolean ok = false;
        if (o instanceof Model) {
            try {
                            // 如果是model则尝试自动提取主键 toString 并存放到map中
            Opt<Object> primaryKeyValue = ((Model) o).getPrimaryKeyValue();
            String mapKey = primaryKeyValue.get().toString();
            if (primaryKeyValue.notNull_() && this.dataMap.containsKey(mapKey)) {
                T t = this.dataMap.get(mapKey);
                if (super.remove(t)) {
                    this.dataMap.remove(mapKey);
                    ok = true;
                } else {
                    ok = false;
                }

            }
            } catch (Exception e) {
            }
        }
        //如果失败则调用父删除
        if (ok==false) {
            ok = super.remove(o);
        }
        onRmove((T)o);

        return ok;
    }

    @Override
    public T remove(int index) {
        if (index >= size()) {
            return null;
        }
        T o = get(index);
        if (o instanceof Model) {
            // 如果是model则尝试自动提取主键 toString 并存放到map中
            ((Model) o).getPrimaryKeyValue().ifNotNull_(key -> {
                this.dataMap.remove(key);
            });
        }
        return super.remove(index);
    }

    /**
     * @return the dataMap
     */
    public Hashtable<String, T> getDataMap() {
        return dataMap;
    }

    /**
     * 通过主键读取 对象
     * 
     * @param key
     * @return
     */
    public Opt<T> getByPrimaryKeyString(String key) {
        Opt<T> ref = new Opt<>();
        if (getDataMap().containsKey(key)) {
            ref.of(getDataMap().get(key));
        }
        return ref;
    }

    public LList<T> add_(T e) {
        add(e);
        return this;
    }

    public LList<T> add_(T... e) {
        for (T t : e) {
            add(t);
        }
        return this;
    }

    public LList<T> add_(int index, T e) {
        add(index, e);
        return this;
    }

    public LList<T> addAll_(Collection<? extends T> c) {
        addAll(c);
        return this;
    }

    public LList<T> addAll_(int index, Collection<? extends T> c) {
        addAll(index, c);
        return this;
    }

    public LList<T> remove_(T e) {
        remove(e);
        return this;
    }

    public LList<T> remove_(T... e) {
        for (T t : e) {
            remove(t);
        }
        return this;
    }

    public LList<T> remove_(int... index) {
        for (int i : index) {
            remove(i);
        }
        return this;
    }

    public LList<T> remove_(int index) {
        remove(index);
        return this;
    }


    /**
     * 转换为LinkedList
     * 
     * @return
     */
    public LinkedList<T> toLinkedList() {
        LinkedList<T> data = new LinkedList<T>();
        for (int i = 0; i < this.size(); i++) {
            data.add(this.get(i));
        }
        return data;
    }

    /**
     * 截取List到末尾
     * 
     * @return
     */
    public LList<T> subList(int startIndex) {
        LList<T> data = new LList<T>();
        if (startIndex > (size() - 1)) {
            return data;
        }
        for (int i = startIndex; i < this.size(); i++) {
            data.add(this.get(i));
        }
        return data;
    }

    /**
     * 聚合为行
     * 
     * @param splString_opt
     * @return
     */
    public str toLine(String splString_opt) {
        str str = new str("");
        for (int i = 0; i < size(); i++) {
            str.insertStrToEnd(new str(get(i)).to_s());
            if (splString_opt != null) {
                str.insertStrToEnd(splString_opt);
            }
        }
        return str;
    }

    public str toJson() {
        return  new str(JSONUtil.objToJsonStr(this));
    }

    /**
     * 从List构建一个LList
     */
    public LList(List<T> dataList_opt) {
        if (dataList_opt != null) {
            addAll(dataList_opt);
        }
    }

    public LList() {
    }

    /**
     * 从List新建LList
     * 
     * @param data
     * @return
     */
    public static LList of(List data) {
        return new LList(data);
    }
}
package github.acodervic.mod.data.list;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.db.anima.Model;
 
/**
 * ListC
 * 
 * @param <T>
 */
public class AList<T> extends ArrayList<T> {
    HashMap<String, T> dataMap = new HashMap<>();
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
        return ok;
    }


    /**
     * 删除一个元素,如果被删除的元素为Model子类且有主键,则会自动从dataMap删除结果
     */
    @Override
    public boolean remove(Object o) {
        boolean ok = false;
        if (o instanceof Model) {
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
        } else {
            ok = super.remove(o);
        }
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
    public HashMap<String, T> getDataMap() {
        return dataMap;
    }

    /**
     * 通过主键读取 对象
     * 
     * @param key
     * @return
     */
    public Opt<T> getDataByKey(String key) {
        nullCheck(key);
        Opt<T> ref = new Opt<>();
        if (getDataMap().containsKey(key)) {
            ref.of(getDataMap().get(key));
        }
        return ref;
    }
    public AList<T> add_(T e) {
        add(e);
        return this;
    }

    public AList<T> add_(T... e) {
        for (T t : e) {
            add(t);
        }
        return this;
    }

    public AList<T> add_(int index, T e) {
        add(index, e);
        return this;
    }

    public AList<T> addAll_(Collection<? extends T> c) {
        addAll(c);
        return this;
    }

    public AList<T> addAll_(int index, Collection<? extends T> c) {
        addAll(index, c);
        return this;
    }

    public AList<T> remove_(T e) {
        remove(e);
        return this;
    }

    public AList<T> remove_(T... e) {
        for (T t : e) {
            remove(t);
        }
        return this;
    }

    public AList<T> remove_(int... index) {
        for (int i : index) {
            remove(i);
        }
        return this;
    }

    public AList<T> remove_(int index) {
        remove(index);
        return this;
    }

    public str toJson() {
        return  new str(JSONUtil.objToJsonStr(this));
    }

    /**
     *转换为arraylist
     * @return
     */
    public ArrayList<T> toArrayList() {
        ArrayList<T> data=new ArrayList<T>();
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
    public AList<T> subList(int startIndex) {
        AList<T> data = new AList<T>();
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
    public static void main(String[] args) {
         new AList<String>().add_("null").add_("1","2","3").toJson().print();
    }
}
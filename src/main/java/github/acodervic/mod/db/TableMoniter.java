package github.acodervic.mod.db;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.event.EventActionEnum;
import github.acodervic.mod.db.anima.event.RowChangeEvent;
import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;

public abstract class TableMoniter {
    // 所有监听器的map,注意key=tableName value等于这个表格注册的所有监听器
    Hashtable<String, LinkedList<Consumer<RowChangeEvent>>> updatedListeners = new Hashtable<String, LinkedList<Consumer<RowChangeEvent>>>();
    Hashtable<String, LinkedList<Consumer<RowChangeEvent>>> insertedListeners = new Hashtable<String, LinkedList<Consumer<RowChangeEvent>>>();
    Hashtable<String, LinkedList<Consumer<RowChangeEvent>>> deletedListeners = new Hashtable<String, LinkedList<Consumer<RowChangeEvent>>>();

    public LinkedList<Consumer<RowChangeEvent>> getOnInsertedListenerByTableName(String tableName) {
        if (!insertedListeners.containsKey(tableName)) {
            insertedListeners.put(tableName, new LinkedList<>());
        }

        return insertedListeners.get(tableName);
    }

    public LinkedList<Consumer<RowChangeEvent>> getOnUpdatedListenerByTableName(String tableName) {
        if (!updatedListeners.containsKey(tableName)) {
            updatedListeners.put(tableName, new LinkedList<>());
        }

        return updatedListeners.get(tableName);
    }

    public LinkedList<Consumer<RowChangeEvent>> getOnDeletedListenerByTableName(String tableName) {
        if (!deletedListeners.containsKey(tableName)) {
            deletedListeners.put(tableName, new LinkedList<>());
        }

        return deletedListeners.get(tableName);
    }

    /**
     * 当接收到所有事件时候调用它!,转发对应的消息通知到到具体事件
     * 
     * @param event
     */
    protected void onRowChanged(RowChangeEvent event) {
        String tableName = event.getTableName();
        //必须在新的线程中进行处理.否则无法更新操作,因为被调用时的线程是在连接池线程!
        FixedPool.getStaticFixedPool().exec(new Task<>("TableMoniter_onRowChanged", ()  ->{
            if (event.getAction().equals(EventActionEnum.DELETE.getAction())) {
                // 通知所有删除监听器
                getOnDeletedListenerByTableName(tableName).forEach(task -> {
                    try {
                        task.accept(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else if (event.getAction().equals(EventActionEnum.UPDATE.getAction())) {
                // 通知所有删除监听器
                try {
                    getOnUpdatedListenerByTableName(tableName).forEach(task -> {
                        task.accept(event);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (event.getAction().equals(EventActionEnum.INSERT.getAction())) {
                // 通知所有删除监听器
                try {
                    getOnInsertedListenerByTableName(tableName).forEach(task -> {
                        task.accept(event);
                    });
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
            return  "";
        }));

    };

    /**
     * 启动监视器
     * 
     * @return
     */
    public abstract boolean start();

    /**
     * 当数据被更改，增加,删除,修改
     * 
     * @param event
     */
    public void addOnRowChanged(String table, Consumer<RowChangeEvent> event) {
        addOnRowInserted(table, event);
        addOnRowUpdated(table, event);
        addOnRowDeleted(table, event);
    };

    /**
     * 当数据被增加
     * 
     * @param event
     */
    public void addOnRowInserted(String table, Consumer<RowChangeEvent> event) {
        getOnInsertedListenerByTableName(table).add(event);
    };

    /**
     * 当数据被修改
     * 
     * @param event
     */
    public void addOnRowUpdated(String table, Consumer<RowChangeEvent> event) {
        getOnUpdatedListenerByTableName(table).add(event);
    };

    /**
     * 当数据被删除
     * 
     * @param event
     */
    public void addOnRowDeleted(String table, Consumer<RowChangeEvent> event) {
        getOnDeletedListenerByTableName(table).add(event);
    };

    /**
     * 删除数据库监听任务
     * 
     * @param table
     * @param task
     */
    public void removeListenerTask(String table, Consumer task) {
        this.getOnDeletedListenerByTableName(table).remove(task);
        this.getOnInsertedListenerByTableName(table).remove(task);
        this.getOnUpdatedListenerByTableName(table).remove(task);
    }
}

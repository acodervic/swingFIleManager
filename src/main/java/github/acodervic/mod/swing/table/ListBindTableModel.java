package github.acodervic.mod.swing.table;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.isNull;
import static github.acodervic.mod.utilFun.list_same1;
import static github.acodervic.mod.utilFun.list_sort;
import static github.acodervic.mod.utilFun.map_keyList;
import static github.acodervic.mod.utilFun.newList;
import static github.acodervic.mod.utilFun.notNull;
import static github.acodervic.mod.utilFun.print;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.table.AbstractTableModel;

import github.acodervic.mod.data.Value;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.data.mode.CompareObj;
import github.acodervic.mod.shell.SystemUtil;
import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;
public class ListBindTableModel<T> extends AbstractTableModel {

    private static final String CLEAR = "clear";
    String tableName = SystemUtil.getUUID().toString();
    Map<String, List<T>> maps = new Hashtable<String, List<T>>();
    List<T> filteredDataList = new ArrayList<T>();// 当前过滤的表格,根据过滤函数,会在进行updateTable的时候重新进行填充
    boolean filterIng = false;// 代表当前表格是否处于过滤状态
    ArrayList<T> tableEmptyDataList = new ArrayList<T>();// 空的数据集用于暂时清空表格的时候显示
    List<Function<T, Boolean> > filteredRowsFuns= new ArrayList();// 过滤函数,返回true保留,false被过滤
    Comparator<T> sortFun = null;// 排序函数
    List<String> headers;
    String nowBindArrayListName = "";
    // 代表表格上一次选中的行,避免当选中行改变时重复调用
    List<Integer> lastSelectRows = new ArrayList<Integer>();
    Function<T, List> rowClumumListFunction;
    BiConsumer<T,EditData>	  onEditFinishFunction;
    Function<List<T>, List<T>> processResultFun;// 最终处理结果的函数,在绑定之前进行调用
    Value fistSrollEnd = new Value(true);// 是否进行了第一次滚动,当我们进行了手动滚动,则调用updateTable的时候就不会自动滚动了
    // 过滤==>排序==>处理函数
    public static int RUNINLAST = 3;// 在排序和过滤函数运行后运行
    public static int RUNINFIRST = 1;// 在排序和过滤函数运行之前运行
    public int processResultFunRunTime = 3;// 当updateTable时候processResultFun运行时间,默认是在排序和过滤函数执行完成后运行,
    Function<String, Object> getColumnByHeaderFun;// 通过列名获取,行值
    /**
     * @param processResultFunRunTime the processResultFunRunTime to set
     */
    public void setProcessResultFunRunTime(int processResultFunRunTime) {
        this.processResultFunRunTime = processResultFunRunTime;
    }

    /**
     * @return the getColumnByHeaderFun
     */
    public Function<String, Object> getGetColumnByHeaderFun() {
        return getColumnByHeaderFun;
    }

    /**
     * @param getColumnByHeaderFun the getColumnByHeaderFun to set
     */
    public void setGetColumnByHeaderFun(Function<String, Object> getColumnByHeaderFun) {
        this.getColumnByHeaderFun = getColumnByHeaderFun;
    }

    /**
     * 根据表头列名获取列名在表头中的下标,没有则返回-1
     *
     * @param headerColumn
     * @return
     */
    public int columnNameInHeaderIndex(String headerColumn) {
        for (int i = 0; i < this.headers.size(); i++) {
            if (this.headers.get(i).trim().toLowerCase().equals(headerColumn.trim().toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    /**
     * @return the fistSrollEnd
     */
    public Value getFistSrollEnd() {
        return fistSrollEnd;
    }
    /**
     * 设置过滤函数
     *
     * @param filteredRowsFun the filteredRowsFun to set
     */
    public void addFilterRowsFun(Function<T, Boolean> filteredRowsFun) {
        if (filteredRowsFun!=null&&!this.filteredRowsFuns.contains(filteredRowsFun)) {
            this.filteredRowsFuns.add(filteredRowsFun);
        }
    }

        /**
     * 设置过滤函数
     *
     * @param filteredRowsFun the filteredRowsFun to set
     */
    public void removeFilterRowsFun(Function<T, Boolean> filteredRowsFun) {
        if (filteredRowsFun!=null&&this.filteredRowsFuns.contains(filteredRowsFun)) {
            this.filteredRowsFuns.remove(filteredRowsFun);
        }
    }

    /**
     * @return the filteredRowsFun
     */
    public List<Function<T, Boolean>> getFilteredRowsFuns() {
        return filteredRowsFuns;
    }

    /**
     * 清除结果处理函数
     */
    public void clearProcessResultFun() {
        this.processResultFun = null;
        this.filterIng = false;// 取消过滤模式
    }

    /**
     * 设置最终输出数据处理函数,注意传入的参数是原始绑定的数据集 (如果是过滤模式+排序模式则传递是是先过滤然后在排序的临时数据集),在非过滤模式,函数内部
     * 直接操作输入集合会直接操作(remove/add)原始绑定的数据集,如果你只是想要在修改最终结果显示.必须现将原始数据集添加到一个新的集合中(addAll)然后对新的集合进行操作。
     * 如果处理函数返回null,则会直接使用getTableBindDataList(true)返回的集合数据
     *
     * @param processResultFun the processResultFun to set
     */
    public void setProcessResultFun(Function<List<T>, List<T>> processResultFun) {
        this.processResultFun = processResultFun;
    }

    /**
     * @return the processResultFun
     */
    public Function<List<T>, List<T>> getProcessResultFun() {
        return processResultFun;
    }
    /**
     * 最终处理List,如果返回不为Null的list则会认为此list是过滤之后的,将自动开启过滤模式
     *
     * @return 如果返回Null,则代表
     */
    public void processResult() {
         List<T> newDataList = null;
        if (this.processResultFun != null) {
            List<T> tableBindDataList = getTableBindDataList(true);
            if (tableBindDataList.size() > 0) {
                newDataList = this.processResultFun.apply(tableBindDataList);
            }
        }
        if (newDataList != null) {
            // 代表数据是又一次经过过滤的
            filterIng = true;
            // 将处理的数据输出到过滤列表上
            this.filteredDataList = newDataList;
        } else {
            // 什么也不做
        }
    }
    /**
     * 调用过滤函数对当前行进行过滤,会自动刷新表格
     */
    public void filterRows() {
 
        if (filteredRowsFuns.size()>0) {
            // 代表此此过滤 processResultFun是否在过滤之前运行
            Boolean processResultFunAlreadyRun = false;
            // 如果在过滤之前就已经调用了 processResultFun
            // .这时候filteredDataList中存储的则是processResultFun生成的数据集,不可以清除
            if (processResultFunRunTime == RUNINFIRST && this.processResultFun != null) {
                processResultFunAlreadyRun = true;
            }
            if (!processResultFunAlreadyRun) {
                // processResultFun没有在最开始输出结果,则 清空上一次刷新表格更新的数据集
                filteredDataList.clear();
                filterIng = false;
            }

            // 进行过滤处理
            // 如果 processResultFun != null && processResultFunRunTime ==
            // RUNINFIRST,这里getTableBindDataList(false);读取的就是由
            // processResultFun先前运行的过滤列表,
            List<T> tableBindDataList = new ArrayList<T>();
            if (processResultFunAlreadyRun) {
                tableBindDataList = getTableBindDataList(true);// 读取processResultFun处理的filteredDataList

            } else {
                tableBindDataList = getTableBindDataList(false);// 读取真实绑定的数据集
            }
            // 先清空 filteredDataList,目的是将 tableBindDataList 的数据重新输出到 filteredDataList
            // 注意千万不能clear().一旦clear
            // .tableBindDataList如果读取的是过滤数据,也会被同时清空因为tableBindDataList指向filteredDataList的引用
            List<T> newDataList = new ArrayList<T>();// 存储新过滤后数据的list
            // 进行过滤
            if (tableBindDataList != null) {
                tableBindDataList.forEach(rowObj -> {
                    if (rowObj!=null) {
                        Boolean match=false;
                        List<Function<T, Boolean>> fs = getFilteredRowsFuns();
                            for(int i=0;i<fs.size();i++){
                                Function<T, Boolean> function = fs.get(i);
                                try {
                                    if (function.apply(rowObj)) {
                                        match=true;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        if (match) {
                            newDataList.add(rowObj);
                        }
                    }

                });
            }
            // 用新过滤的数据覆盖原始过滤数据
            this.filteredDataList = newDataList;
            filterIng = true;
        } else if (this.filteredRowsFuns.size()==0 && this.processResultFun == null) {
            // 只有在过滤函数和结果处理函数都为null的时候才会清除过滤状态,因为processResultFun的原理也是通过过滤模式实线的
            stopFilterRows();
        }
    }

    /**
     * 停止过滤
     */
    public void stopFilterRows() {
        clearFilterRows();
    }

    /**
     * 清除过滤数据,会自动的修改内部过滤状态为false
     */
    public void clearFilterRows() {
        this.filteredDataList.clear();
        this.filterIng = false;
    }
    /**
     * 删除所有过滤器
     */
    public void clearFilterRowFuns() {
        this.filteredRowsFuns.clear();
    }
    /**
     * 当用户编辑单元格的时候触发,T为当前正编辑处理的 对象,String 为列名  ,返回值为true则允许单元格编写,否则则不允许编辑单元格
     */
    BiFunction<T,String,Boolean>	 editCloumnFunction;

    /**
     * 读取当前绑定的数据源
     *
     * @param filter 代表是否可以获取到filterlist, true则代表允许返回过滤列表,false代表只能返回真实的list
     * @return
     */
    List<T> getTableBindDataList(boolean filter) {
        if (nowBindArrayListName==null) {
            return new ArrayList<T>();
        }
        // 如果处于 过滤 模式则对绑定的数据进行过滤刷新
        if (filter && filterIng) {
            return filteredDataList;
        } else {
            if (maps.containsKey(nowBindArrayListName)) {
                return maps.get(nowBindArrayListName);
            } else {
                return new ArrayList<T>();
            }
        }

    }

    /**
     * 绑定数据源(添加数据源则刷新表格),如果数据源不存在则自动调用addDataSrouces添加到数据源maps,如果数据源已经先前被绑定到table,则调用swich切换到数据源并刷新表格
     *
     * @param souces
     * @return
     */
    public String bindDataSources(List<T> souces) {
        nullCheck(souces);
        //print("当前数据源个数"+this.maps.size());
        List<String> dataSrouceKeys=map_keyList(maps);
        for (int i = 0; i < dataSrouceKeys.size(); i++) {

            if (souces==maps.get(dataSrouceKeys.get(i))) {
                //代表书数据源已经存在
                //自动切换到上面
                switchListSource(dataSrouceKeys.get(i));
                return this.nowBindArrayListName;
            }
        }  
        //数据源不存在
        switchListSource(addDataSrouces(souces));
        return this.nowBindArrayListName;
    }

    /**
     * 清空所有表格行,不清空数据集
     */
    public void clearRows() {
        // 绑定一个长度为0的数据集
        this.switchListSource(CLEAR);
        // 清空所有表格行
    }

    /**
     * 添加一个数据源,不会更新表格,如果数据源已经被添加(同key引用+同一个对象)则不操作,否则则删除u原来的数据源重新添加
     *
     * @param name_opt 指定的数据源名称,如果为null,则默认为传递 数据源的hashCode
     * @param souces
     * @return 返回数据源key
     */
    public String addDataSrouces(String name_opt, List<T> souces) {
        nullCheck(souces);
        if (name_opt==null) {
            name_opt=System.identityHashCode(souces)+"";

        }
        //是否需要更新数据源
        if (maps.containsKey(name_opt)) {
            //原始数据源和新数据源不同
            if (getTableDataList()!=souces) {
                //覆盖数据源
                maps.remove(name_opt);
                maps.put(name_opt, souces);
  
            }else{
                //相同的数据源
            }
        }else {
            //新加入的数据源
            maps.put(name_opt, souces);
        }
        return name_opt;
    }

    /**
     * 添加一个数据源,不会更新表格,如果数据源已经被添加(同key引用+同一个对象)则不操作,否则则删除u原来的数据源重新添加
     *数据源名称,如果为null,则默认为传递 数据源名的hashCode
      * @param souces
     */
    public String addDataSrouces(List<T> souces) {
        return      addDataSrouces(null, souces);
    }


    /**
     * 判断和上次相比选中行是否发生改变
     *
     * @return
     */  
    public boolean selectChange(List<Integer> rows) {
        if (rows.size()==0 ) {
            return false;
        }
        List<Integer> list_same1 = list_same1(lastSelectRows, rows, (o) -> {
            CompareObj compareObj = (CompareObj) o;
            return ( (Integer)compareObj.getObjA()  ).intValue()== ( (Integer)compareObj.getObjB()  ).intValue();
        });
        if (list_same1.size() == rows.size()) {
            // 都相同则代表选中没有改变
            return false;
        }
        lastSelectRows = rows;
        return true;
    }

    /**
     * @return the lastSelectRows
     */
    public List<Integer> getLastSelectRows() {
        return lastSelectRows;
    }

    /**
     * 切换到数据源
     *
     * @param name  
     * @return
     */
    public boolean switchListSource(String name) {
        nullCheck(name);
        // 绑定空数据集
        if (name.equals(CLEAR)) {
            nowBindArrayListName = name;
            FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                // 刷新添加的行
                fireTableDataChanged();
                return null;
            }));
            return true;
        }
        if (nowBindArrayListName==null) {
            nowBindArrayListName=name;
            FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                 // 刷新添加的行
                 fireTableDataChanged();
                return null;
            }));
            return true;
        }
        if (!nowBindArrayListName.equals(name) && this.maps.containsKey(name)) {
            nowBindArrayListName = name;
            // 刷新表格 ,重新绑定到list
            FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
                // 刷新添加的行
                fireTableDataChanged();
                return null;
            }));
            return true;
        }
        return false;
    }



    /**
     * 当尝试进行编辑行的时候触发,通过返回true,或false 来判断是否允许编辑
     * 
     * @param editCloumnF unction
     */
    public void setEditCloumnFun(BiFunction<T,String,Boolean>  editCloumnFunction) {
        this.editCloumnFunction=editCloumnFunction;
    }
    @Override
    public int getRowCount() {
        if (getTableBindDataList(true) == null) {
            return 0;

        }
        return getTableBindDataList(true).size();
    }

    @Override
    public int getColumnCount() {
        // 自动加上序号
        return headers.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            // 返回序号
            return rowIndex + 1;
        }
        if (rowClumumListFunction == null) {
            return ""; 
        } 
        T rowObj = null;

        try {
            rowObj = this.getTableBindDataList(true).get(rowIndex);
            // 通过函数接口来读取对象中的列
            List colums = rowClumumListFunction.apply(rowObj);
            Object string = colums.get(columnIndex - 1);
            if (string==null) {
                return "";
            }
            if (string instanceof String) {
                return string;
            }else{
                    return string.toString();//调用toString
            }
        } catch (Exception e) {
            int index = columnIndex - 1;
            System.out.println("调用rowTextListFun出错" + e.getMessage() + " => 列:" + headers.get(columnIndex) + " index="
                    + index + "   obj=" + rowObj);
            e.printStackTrace();// 输出异常
        } 
        return ""; 
    }

    /**
     * @param tableDataList_opt
     */
    public ListBindTableModel(List<T> tableDataList_opt, List<String> headers, String sourceName_opt) {
        nullCheck(headers);
        if (tableDataList_opt != null ) {
            if ( sourceName_opt!=null) {
            }else { 
                sourceName_opt=System.identityHashCode(tableDataList_opt)+"";

            }
            this.maps.put(sourceName_opt,  tableDataList_opt);
            this.nowBindArrayListName = sourceName_opt;

        }else {
            //传入null数据
            this.nowBindArrayListName = null;
 
        } 
        this.headers = newList("序号");
        this.headers.addAll(headers);
    }

    public String getColumnName(int col) {
        return this.headers.get(col);
    }
 
 
    /**
     * 设置处理行的函数.提供一个List<String> 每一个string代表一个列(不包含 序号 列)
     * @param rowClumumListFunction
     */
    public void setRowListFunction(Function<T, List> rowClumumListFunction) {
        nullCheck(rowClumumListFunction);
        this.rowClumumListFunction = rowClumumListFunction;
    }

    /**
     * 添加行,添加的对象会直接添加到当前使用的数据源中,不推荐,正确的方法是i直接操作数据源,然后其余自动更新表格
     *
     * @param rowData 如果添加null 则会反映为一个空数据行
     */
    public void addRow( T rowData) {
        // 添加数据
        if (filterIng) {
            // 先添加到filter
            this.getTableBindDataList(true).add(rowData);
        }
        // 再添加到真实List
        this.getTableBindDataList(false).add(rowData);
        FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
            // 刷新添加的行
            fireTableRowsInserted(getRowCount(), getRowCount());
            return null;
        }));
    }

    /**
     * 通过表格下标读取当前数据源绑定的实体
     * 
     * @param rowIndex
     * @return
     */
    public T getDataByRowIndex(int rowIndex) {
        return this.getTableBindDataList(true).get(rowIndex);
    }

    /**
     * 清空当前table,并清空使用的数据源
     */
    public void clearAllRowsWithData() {
        if (filterIng) {
            this.getTableBindDataList(true).clear();
        }
        this.getTableBindDataList(false).clear();
        FixedPool.getStaticFixedPool().exec(new Task<>(() -> {
            fireTableDataChanged();// 重新加载数据
            return null;
        }));
    }

    /**
     * 通过index删除行,并同时删除数据源的List,不推荐
     *
     * @param index
     */
    private void removeRowByIndex(int index) {
        print("删除" + index);
        this.getTableBindDataList(false).remove(index);
        if (filterIng) {
            this.getTableBindDataList(true).remove(index);
        }
        fireTableRowsDeleted(index, index);
    }

    /**
     * 通过index删除行
     * 
     * @param index 
     */ 
    public void removeRowsByIndex(List<Integer> indexs) {
        nullCheck(indexs);
        // 因为目标数据集为list 所以如果删除了前面的元素后面的元素会自动补齐
        // 所以只能从大到小的删
        // 排序下标
        list_sort(indexs, (o1, o2) -> {
            return o2 - o1;
        });
        for (int i = 0; i < indexs.size(); i++) {
            removeRowByIndex(indexs.get(i));
        }
    }

    /**
     * @return the getRowListFunction
     */
    public Function<T, List> getRowListFunction() {
        return rowClumumListFunction;
    }

    /**
     * 通过index删除行
     * 
     * @param index
     */
    public void removeRowsByIndex(int[] indexs) {
        List<Integer> indexList = new ArrayList<Integer>();
        for (int i = 0; i < indexs.length; i++) {
            indexList.add(indexs[i]);
        }
        removeRowsByIndex(indexList);
    } 

    /**
     * 读取真实的绑定数据源
     * 
     * @return the tableDataList
     */
    public List<T> getTableDataList() {
        return getTableBindDataList(false);
    }

    int oldSize = 0;

    /**
     * 监听当前绑定的key对应的数据集是否改变,只是,简单的使用size属性来判断,数据更新则需要手动操作
     *
     * @return
     */
    public boolean bindDataIsChange() {
        // 如果内部存在处理函数则默认为已经改变;
        if (this.sortFun != null || this.filteredRowsFuns != null || this.processResultFun != null) {
            return true;
        }
        if (getTableBindDataList(false).size() != oldSize) {
            oldSize = getTableBindDataList(false).size();
            return true;
        }
        return false;
    }

    /**
     * 使用已经设置的排序器对表格进行排序
     */
    public void sortRows() {
        if (this.sortFun != null) {
            try {
                ListUtil.sort(getTableBindDataList(true), this.sortFun);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    /**
     * 排序表格
     */
    public void sortRows(Comparator<T> sortFun) {
        if (sortFun != null) {
            try {
                this.sortFun = sortFun;
                ListUtil.sort(getTableBindDataList(true), this.sortFun);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除当前表格排序
     */
    public void clearSortRows() {
        if (this.sortFun != null) {
            try {
                this.sortFun = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当表格被编辑的时候调用
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (editCloumnFunction!=null) {
            T t = this.getTableBindDataList(true).get(rowIndex);
            if (isNull(t)) {
                //如果为null 则直接返回可编辑
                return true;
            }else {
                //否则传递到用户的处理函数
                // List<String> colunms = rowListFunction.apply(t);
                return this.editCloumnFunction.apply(t, getHeaders().get(columnIndex));
            }

        }else{
            return false;
        }
    }

    /**
     * 当对单元格进行编辑后的回调
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
        if (onEditFinishFunction!=null) {
            //填充新的数据
            T t = getTableBindDataList(true).get(rowIndex);
            if (notNull(t )) {
                EditData editData = new EditData();
                editData.setColumnName(getHeaders().get(columnIndex));
                editData.setEditedColumnData(aValue);
                this.onEditFinishFunction.accept(t, editData);
            }
        }
    }

    /**
     * @return the onEditFinishFunction
     */
    public BiConsumer<T, EditData> getOnEditFinishFunction() {
        return onEditFinishFunction;
    }

    /**
     * @param onEditFinishFunction the onEditFinishFunction to set
     */
    public void setOnEditFinishFunction(BiConsumer<T, EditData> onEditFinishFunction) {
        this.onEditFinishFunction = onEditFinishFunction;
    }

    /**
     * @return the headers
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * @param headers the headers to set
     */
    public void setHeaders(List<String> headers) {
        if (!this.maps.containsKey(CLEAR)) {
            this.maps.put(CLEAR, tableEmptyDataList);
        }
        this.headers = headers;
    }

    /**
     * 清空所有的行(将当前显示绑定到空 列表数据)
     */
    public void setEmpty() {
        switchListSource(CLEAR);
    }
}
package github.acodervic.mod.data.list;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import github.acodervic.mod.data.JSONUtil;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.mode.CompareObj;
import github.acodervic.mod.function.FunctionUtil;

/**
 * ListUtil
 */
public class ListUtil {
    /**
     * 生成一个泛型集合
     * 
     * @param <T>      类型
     * @param elements 元素
     * @return 集合
     */
    public static <T> ArrayList<T> newList(T... elements) {
        ArrayList<T> list = new ArrayList<T>();
        if (elements!=null) {
            for (T element : elements) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * 将集合对象转为数组对象,使用的时候自己进行强制转换
     * 
     * @param <T>
     * @param data 传入的泛型集合
     * @return 返回一个Object数组,使用的时候自己转换
     */
    public static <T> Object[] toArry(List<T> data) {
        nullCheck(data);
        if (data == null | data.size() == 0) {
            return null;
        }
        Object[] os = new Object[data.size()];
        // 首先提取类型
        for (int i = 0; i < data.size(); i++) {
            os[i] = data.get(i);
        }
        return os;
    }

    /**
     * 将集合对象转为数组对象,使用的时候自己进行强制转换
     * 
     * @param <T>
     * @param data 传入的泛型集合
     * @return 返回一个Object数组,使用的时候自己转换
     */
    public static <T> String[] toStringArry(List<T> data) {
        nullCheck(data);
        if (data == null | data.size() == 0) {
            return null;
        }
        String[] os = new String[data.size()];
        // 首先提取类型
        for (int i = 0; i < data.size(); i++) {
            os[i] = data.get(i).toString();
        }
        return os;
    }

    /**
     * 截取下标中的第几个到第几个,例如1,2,3,4,5 当start=2,end=4的时候，将返回2,3,4
     * 
     * @param <T>   类型
     * @param data  集合数据
     * @param start 开始的字符位置,第几个(非下标!)
     * @param end   结束的字符位置，第几个(非下标!)
     * @return 被截取的集合
     */
    public static <T> List<T> sub(List<T> data, int start, int end) {
        nullCheck(data);
        if (data.size() == 0) {
            return new ArrayList<T>();
        }
        if (start > end) {
            System.out.println("start不能大于end!");
            return new ArrayList<T>();
        }
        ArrayList<T> returnData = new ArrayList<T>();
        for (int i = (start - 1); i < end; i++) {
            returnData.add(data.get(i));
        }
        return returnData;

    }

    /**
     * 调用list所有元素的toString方法来拼接字符串.每个字符串中间使用固定字符分割
     *
     * @param listData      为集合数据
     * @param delimiter_opt 每个数据拼接之前的间隔符(可选null="")
     * @return 拼接之后的字符串
     */
    public static String toStr(List listData, String delimiter_opt) {
        nullCheck(listData);
        // 进行组装
        String result = "";
        for (int i = 0; i < listData.size(); i++) {
            if (listData.get(i) == null) {
                continue;
            }
            if (i == (listData.size() - 1)) {
                result += (listData.get(i).toString());
            } else {
                result += (listData.get(i).toString() + FunctionUtil.get(() -> delimiter_opt.toString()).orElse(""));
            }
        }
        return result;
    }

    /**
     * 转为json字符串
     * 
     * @param list 集合
     * @return json字符串
     */
    public static String toJsonStr(List list) {
        nullCheck(list);
        return JSONUtil.objToJsonStr(list);
    }

    /**
     * 获得数值集合里面的最大值的坐标
     * 
     * @param numbers 数值列表
     * @return 最大值坐标
     */
    public static int maxNumOfIndex(List<Integer> numbers) {
        nullCheck(numbers);
        int max = 0;
        int maxIndex = 0;
        for (int i = 0; i < numbers.size(); i++) {

            if (numbers.get(i) > max) {
                max = numbers.get(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * 获取集合中的最小值
     * 
     * @param numbers 数值列表
     * @return 最小值
     */
    public static Integer minNum(List<Integer> numbers) {
        nullCheck(numbers);
        if (numbers == null || numbers.size() == 0) {
            return null;
        }
        return numbers.stream().reduce(Integer::min).get();// 得到最小值
    }

    /**
     * 获取集合中的最小值所在的下标
     * 
     * @param numbers 数值列表
     * @return 最小值的下标
     */
    public static Integer minNumOfIndex(List<Integer> numbers) {
        if (numbers == null || numbers.size() == 0) {
            return null;
        }
        int min = 0;
        int minIndex = 0;

        for (int i = 0; i < numbers.size(); i++) {

            if (numbers.get(i) < min) {
                min = numbers.get(i);
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * 求和
     * 
     * @param numbers 数值列表
     * @return和
     */
    public static Integer sum(List<Integer> numbers) {
        nullCheck(numbers);
        if (numbers == null || numbers.size() == 0) {
            return null;
        }
        Integer count = 0;
        for (Integer num : numbers) {
            count += num;
        }
        return count;
    }

    /**
     * 求平均值
     * 
     * @param numbers 数值列表
     * @return 平均值
     */
    public static Double averageValue(List<Integer> numbers) {
        nullCheck(numbers);
        if (numbers == null || numbers.size() == 0) {
            return null;
        }
        int sum = sum(numbers);
        return (double) sum / numbers.size();
    }

    /**
     * 求中位数
     * 
     * @param numbers 数值列表
     * @return 中位数
     */
    public static Double midiumNum(List<Integer> numbers) {
        {
            nullCheck(numbers);
            if (numbers == null || numbers.size() == 0) {
                return null;
            }
            double j = 0;
            // 集合排序
            Collections.sort(numbers);
            int size = numbers.size();
            if (size % 2 == 1) {
                j = numbers.get((size - 1) / 2);
            } else {
                // 加0.0是为了把int转成double类型，否则除以2会算错
                j = (numbers.get(size / 2 - 1) + numbers.get(size / 2) + 0.0) / 2;
            }
            return j;
        }
    }

    /**
     * 判断同一个数字在,集合中出现的次数
     * 
     * @param numbers 数值列表
     * @param num     数值
     * @return 出现的次数
     */
    public static int count(List<Integer> numbers, int num) {
        nullCheck(numbers);
        int count = 0;
        for (Integer n : numbers) {
            if (n == num) {
                count += 1;

            }
        }
        return count;
    }

    /**
     * listByte转byte[]
     * 
     * @param bytesList 字节列表
     * @return 字节数组
     */
    public static byte[] listByteTobytes(List<Byte> bytesList) {
        nullCheck(bytesList);
        byte[] byteData = new byte[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
            byteData[i] = bytesList.get(i);
        }
        return byteData;
    }

    /**
     * 将List转为AList
     * 
     * @param <T>
     * @param list
     * @return
     */
    public static <T> AList<T> listToAlist(List<T> list) {
        nullCheck(list);
        AList<T> alist = new AList<T>();
        for (int i = 0; i < list.size(); i++) {
            alist.add(list.get(i));
        }
        return alist;
    }

    /**
     * 将List转为LList
     * 
     * @param <T>
     * @param list
     * @return
     */
    public static <T> LList<T> listToLlist(List<T> list) {
        nullCheck(list);
        LList<T> llist = new LList<T>();
        for (int i = 0; i < list.size(); i++) {
            llist.add(list.get(i));
        }
        return llist;
    }

    /**
     * 切割List
     * 
     * @param <T>
     * @param list
     * @param groupSize
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> list, int groupSize) {
        int length = list.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize; // TODO
        List<List<T>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(list.subList(fromIndex, toIndex));
        }
        return newList;
    }

    /**
     * 求集合1合的差集 如:list1=1,2,3 list2=3.4,5 返回12 求list1的差集合
     * 
     * @param <T>   类型
     * @param <D>   类型
     * @param list1 集合1
     * @param list2 集合2
     * @return 差集
     */
    public static <T, D> List<T> differenceList1(List<T> list1, List<D> list2, Predicate<CompareObj> compareFun) {
        nullCheck(list1, list2, compareFun);
        List<T> differenceSet = new ArrayList<T>();
        // 通过eque来进行对比,如果需要改变对比规则需要其内部包含的对象重写equa方法
        for (int i = 0; i < list1.size(); i++) {
            // 外层循环
            T list1Data = list1.get(i);
            //
            boolean deff = false;
            for (int j = 0; j < list2.size(); j++) {
                D list2Data = list2.get(j);
                // 比较外层的listdata和内层的list2data
                if (compareFun.test(new CompareObj(list1Data, list2Data))) {
                    // 代表外层循环已经对比到了相同的数据
                    deff = true;
                    break;
                }
            }
            // 判断是否对比成功
            if (!deff) {
                differenceSet.add(list1Data);
            }
        }
        return differenceSet;
    }

    /**
     * 求两个集合的差集 如:list1=1,2,3 list2=3.4,5 返回12 求list1的差集合
     * 
     * @param <T>   类型
     * @param <D>   类型
     * @param list1 集合1
     * @param list2 集合2
     * @return 差集合
     */
    public static <T, D> List<T> sameList1(List<T> list1, List<D> list2, Predicate compareFun) {
        nullCheck(list1, list2, compareFun);
        List<T> differenceSet = new ArrayList<T>();
        // 通过eque来进行对比,如果需要改变对比规则需要其内部包含的对象重写equa方法
        for (int i = 0; i < list1.size(); i++) {
            // 外层循环
            T list1Data = list1.get(i);
            //
            boolean deff = false;
            for (int j = 0; j < list2.size(); j++) {
                D list2Data = list2.get(j);
                // 比较外层的listdata和内层的list2data
                if (compareFun.test(new CompareObj(list1Data, list2Data))) {
                    // 代表外层循环已经对比到了相同的数据
                    deff = true;
                    break;
                }
            }
            // 判断是否对比成功,相同则添加
            if (deff) {
                differenceSet.add(list1Data);
            }
        }
        return differenceSet;
    }

    /**
     * list转换类型,如果在处理对象函数接口中返回null,代表丢弃元素,即删除元素
     * 
     * @param <T>      类型
     * @param <D>      类型
     * @param rawList  原始集合
     * @param coustFun 处理函数
     * @return 结果
     */
    public static <T, D> List<T> cast(List<D> rawList, Function<D, T> coustFun) {
        nullCheck(rawList, coustFun);
        List<T> result = new ArrayList<T>();
        for (D rawObj : rawList) {
            // 处理数据
            T data = coustFun.apply(rawObj);
            if (data != null) {
                result.add(data);
            }
        }
        return result;

    }

    /**
     * 过滤集合中的数据,比较函数返回true则需要集合成员。false则丢弃,最终返回所有需要的成员 获取集合中大于小于20元素 List<Integer>
     * list2= filter(list, new Predicate<Integer>() {
     * 
     * @Override public boolean test(Integer t) { return t<20; } });
     * @param <T>        类型
     * @param rawList    输入集合
     * @param compareFun 比较函数
     * @return 结果集合
     */
    public static <T> List<T> filter(List<T> rawList, Predicate compareFun) {
        nullCheck(rawList, compareFun);
        List<T> resultList = new ArrayList<T>();
        for (int i = 0; i < rawList.size(); i++) {
            if (compareFun.test(rawList.get(i))) {
                resultList.add(rawList.get(i));
            }
        }
        return resultList;
    }

    /**
     * 对集合进行排序 按从小到大进行排序 List<Integer> list3= sort(list2,new Comparator<Integer>() {
     * 
     * @Override public int compare(Integer o1, Integer o2) { return
     *           o1.compareTo(o2); } });
     * @param <T>     结果
     * @param list    结果
     * @param sortFun 排序函数
     * @return 排序之后的集合
     */
    public static <T> List<T> sort(List<T> list, Comparator<? super T> sortFun) {
        nullCheck(list, sortFun);
        Collections.sort(list, sortFun);
        return list;
    }

    /**
     * 根据集合内元素返回的字符串的相似度进行排序
     * 
     * 
     * @param <T>
     * @param list    被排序的集合
     * @param sortFun 对于每个即可返回一个字符串(可以返回null,内部会使用str处理)
     * @return
     */
    public static <T> List<T> sortByMatchRatio(List<T> list, Function<T, String> sortFun) {
        nullCheck(list, sortFun);
        Collections.sort(list, (o1, o2) -> {
            str o1String = new str(sortFun.apply(o1));
            str o2String = new str(sortFun.apply(o2));
            return ((int) o1String.getMatchRatio(o2String.to_s()) * 100);
        });
        return list;
    }

    /**
     * 对集合进行去重复,返回一个去重后的集合
     * 
     * @param <T>
     * @param list        原始集合
     * @param biPredicate 比较函数,true代表元素重复则删除当前比较的元素,false代表非重复则保留元素
     * @return 新的集合,实际上无需接收返回,输入的list已经被成功处理了 直接使用即可
     */
    public static <T> List<T> duplicate(List<T> list, BiPredicate<T, T> biPredicate) {
        nullCheck(list, biPredicate);
        List<T> newList = new LinkedList<T>();
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (biPredicate.test(list.get(j), list.get(i))) {
                } else {
                    newList.add(list.get(j));
                }
            }
        }
        return newList;
    }

    /**
     * 高性能过滤list,内部使用hashmap,必须对每个实体生成一个唯一的字符串
     * 
     * @param <T>
     * @param list
     * @param biPredicate
     * @return
     */
    public static <T> List<T> duplicateMap(List<T> list, Function<T, String> biPredicate) {
        nullCheck(list, biPredicate);
        HashMap<String, T> keyMap = new HashMap<String, T>();
        for (int i = 0; i < list.size(); i++) {
            T obj = list.get(i);
            if (obj != null) {
                String key = biPredicate.apply(obj);
                if (!keyMap.containsKey(key)) {
                    keyMap.put(key, obj);
                }
            }
        }
        return keyMap.values().stream().collect(Collectors.toList());
    }

    /**
     * 对集合进行去重复(如果集合长度大于等于maxParallelSize,则自动进行并行去重操作),返回一个去重后的集合
     * 
     * @param <T>
     * @param list            被去重的列表
     * @param biPredicate     对比函数
     * @param maxParallelSize 启用并行的集合长度
     * @param taskListSIze    并行任务组list长度
     * @return
     */
    public static <T> List<T> duplicate(List<T> list, BiPredicate<T, T> biPredicate, int maxParallelSize,
            int taskListSIze) {
        nullCheck(list, biPredicate);
        if (maxParallelSize > 0 && list.size() >= maxParallelSize) {
            // 并行计算
            return duplicateParallel(list, biPredicate, taskListSIze);
        } else {
            return duplicate(list, biPredicate);
        }
    }

    /**
     * 对集合进行去重复(内部实现并行操作,如果被并行的组为1则放弃并行使用基本的去重函数duplicate),返回一个去重后的集合
     * 
     * @param <T>
     * @param list        被去重的列表
     * @param biPredicate 对比函数
     * @param listSIze    切割List的长度
     * @return
     */
    public static <T> List<T> duplicateParallel(List<T> list, BiPredicate<T, T> biPredicate, int listSIze) {
        nullCheck(list, biPredicate);
        if (listSIze <= 1) {
            listSIze = 1000;// 默认1000个一组
        }
        List<List<T>> splitList = splitList(list, listSIze);
        if (splitList.size() == 1) {
            return duplicate(list, biPredicate);
        } else {
            List<T> newList = new LinkedList<T>();
            splitList.parallelStream().forEach(alist -> {
                List<T> duplicate = duplicate(alist, biPredicate);
                newList.addAll(duplicate);
            });
            return duplicate(newList, biPredicate);
        }
    }

    /**
     * 对集合进行随机排序
     * 
     * @param list
     */
    public static void randomSortList(List list) {
        nullCheck(list);
        Collections.shuffle(list);
    }

    /**
     * 对集合进行反转排序
     *
     * @param list
     */
    public static void reverseSortList(List list) {
        nullCheck(list);
        Collections.reverse(list);
    }

}
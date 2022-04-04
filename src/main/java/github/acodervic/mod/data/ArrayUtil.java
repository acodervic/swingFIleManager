package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.data.BaseUtil.nullString;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.data.mode.CompareObj;
import github.acodervic.mod.function.FunctionUtil;

public class ArrayUtil {
 
    /**
     * 复制数组
     * @param data
     * @return
     */
    public static byte[] cloneArray(byte[] data) {
        byte[] data2=new byte[data.length];
               System.arraycopy(data, 0, data2, 0, data.length);
               return data2;
    }
    /**
     * 数组对比
     * @param data1 第一个
     * @param data2 第二个
     * @return
     */
    public  static boolean equals(byte[] data1, byte[] data2) {
        return Arrays.equals(data1, data2);
    }
    /**
     * 生成一个泛型泛型数组
     * 
     * @param <T>      类型
     * @param elements 同类型的元素
     * @return
     */
    public static <T> T[] newArray(T... elements) {
        nullCheck(elements);
        T[] ts = (T[]) Array.newInstance(elements[0].getClass(), elements.length);
        for (int i = 0; i < ts.length; i++) {
            ts[i] = elements[i];
        }
        return ts;
    }

    /**
     * 切割数组,若arrays=1,2,3,4 当start=1,end=2 返回的数组 1,2
     * 出现错误则返回原始数组
     * @param <T>   类型
     * @param array 原始数组
     * @param start 开始的字符位置,第几个(非下标!)
     * @param end   结束的字符位置，第几个(非下标!)
     * @return
     */
    public static <T> T[] sub(T[] array, int start, int end) {
        nullCheck(array);
        if (array.length == 0) {
            return array;
        }
        if (start > end) {
            System.out.println("start不能大于end!");
            return array;
        }
        T[] ts = (T[]) Array.newInstance(array[0].getClass(), (end - start) + 1);
        for (int i = (start - 1); i < end; i++) {
            try {
                ts[i] = (array[i]);
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            }
        }
        return ts;
    }

    /**
     * 数组转list
     * 
     * @param <T>   类型
     * @param array 原始数组
     * @return 一个lis
     */
    public static <T> List<T> toList(T[] array) {
        return ListUtil.newList(array);
    }

    /**
     * 获取集合中的最大值,如果集合为长度为0,返回-1
     * 
     * @param numbers
     * @return
     */
    public static Integer maxNum(List<Integer> numbers) {
        nullCheck(numbers);
        if (numbers.size() == 0) {
            return -1;
        }
        return numbers.stream().reduce(Integer::max).get();// 得到最大值
    }

    /**
     * 获得数组里面的最大值的坐标
     * 
     * @param numbers 输入数值数组
     * @return 数组里面的最大值的坐标
     */
    public static int maxNumOfIndex(int[] numbers) {
        int max = 0;
        int maxIndex = 0;
        for (int i = 0; i < numbers.length; i++) {

            if (numbers[i] > max) {
                max = numbers[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * 调用数组所有元素的toString方法来拼接字符串
     * 
     * @param listData  为集合数据
     * @param delimiter_opt 每个数据拼接之前的间隔符
     * @return
     */
    public static String toStr(Object[] arrays, String delimiter_opt) {
        // 进行组装
        String result = "";
        for (Object data : arrays) {
            result += data.toString() + FunctionUtil. get(() -> delimiter_opt.toString()).orElse(nullString());
        }
        return result;
    }

    /**
     * 调用数组所有元素的toString方法来拼接字符串
     *
     * @param listData  为集合数据
     * @return 最终字符串
     */
    public static String toJsonStr(Object[] datas) {
        return JSONUtil.objToJsonStr(datas);
    }

    /**
     * 判断同一个数字在,数组中出现的次数
     * 
     * @param numbers 目标整型数组
     * @param num     目标数值
     * @return 次数
     */
    public static int countNumShow(int[] numbers, int num) {
        int count = 0;
        for (int n : numbers) {
            if (n == num) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 获取数组中的最大值
     * 
     * @param numbers 目标数值数组
     * @return 出现的最大值
     */
    public static int max(int[] numbers) {
        Arrays.sort(numbers);
        return numbers[numbers.length - 1];
    }

    /**
     * 获取数组中的最小值
     * 
     * @param numbers 目标数值数组
     * @return 最小值
     */
    public static int minNum(int[] numbers) {
        Arrays.sort(numbers);
        return numbers[0];
    }

    /**
     * 获取目标数值数组的最小值所在的下标,如果集合为null或者长度为0,返回-1
     * 
     * @param numbers 目标数值数组
     * @return 最小值存在的下标
     */
    public static int minNumOfIndex(int[] numbers) {
        nullCheck(numbers);
        Arrays.sort(numbers);
        if (numbers.length == 0) {
            return -1;
        }
        int min = 0;
        int minIndex = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] < min) {
                min = numbers[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * 求数值数组中中位数,如果数值数组为0则返回-1
     * 
     * @param numbers 目标数值数组
     * @return 中位数
     */
    public static Double midiumNum(int[] numbers) {
        {
            nullCheck(numbers);
            if (numbers.length == 0) {
                return (double) -1;
            }
            List<Integer> data = new ArrayList<Integer>();
            for (int num : numbers) {
                data.add(num);
            }
            double j = 0;
            // 集合排序
            Collections.sort(data);
            int size = data.size();
            if (size % 2 == 1) {
                j = data.get((size - 1) / 2);
            } else {
                // 加0.0是为了把int转成double类型，否则除以2会算错
                j = (data.get(size / 2 - 1) + data.get(size / 2) + 0.0) / 2;
            }
            return j;
        }
    }

    /**
     * 求数组平均值
     * 
     * @param numbers 目标数值数组
     * @return 平均值
     */
    public static double averageValue(int[] numbers) {
        nullCheck(numbers);
        int sum = sum(numbers);
        return (double) sum / numbers.length;
    }

    /**
     * 求数组和
     * 
     * @param numbers 目标数值数组
     * @return 和
     */
    public static int sum(int[] numbers) {
        nullCheck(numbers);
        return Arrays.stream(numbers).sum();
    }

    /**
     * 输出数组中的数据,自动调用集合中的tostring 方法
     * 
     * @param arrays       数值数组
     * @param splitStr_opt 分割的字符串(可选,null="")
     */
    public static void printData(Object[] arrays, String splitStr_opt) {
        for (Object data : arrays) {
            System.out.print("(" + data.getClass().getName() + ":" + data.toString() + ")"
                    + FunctionUtil.get(() -> splitStr_opt).orElse(nullString()));
        }
    }

    /**
     * 输出集合中的数据,自动调用集合中的tostring 方法
     * 
     * @param listData
     * @param splitStr_opt 分割的字符串(可选,null="")
     */
    public static void printData(List<Object> listData, String splitStr_opt) {
        for (Object data : listData) {
            System.out.print("(" + data.toString() + "   " + data.getClass().getName() + ")"
                    + FunctionUtil.get(() -> splitStr_opt).orElse(nullString()));
        }
    }

    /**
     * 求两个数组的差集 如:list1=1,2,3 list2=3.4,5 返回12 求list1的差集合
     * 
     * @param <T>    类型
     * @param <D>    类型
     * @param array1 数组1
     * @param array2 数组2
     * @return 一个差集数组
     */
    public static <T, D> List<T> differenceArray1(T[] array1, D[] array2, Predicate compareFun) {
        nullCheck(array1, array2, compareFun);
        List<T> differenceSet = new ArrayList<T>();
        // 通过eque来进行对比,如果需要改变对比规则需要其内部包含的对象重写equa方法
        for (int i = 0; i < array1.length; i++) {
            // 外层循环
            T list1Data = array1[i];
            //
            boolean deff = false;
            for (int j = 0; j < array2.length; j++) {
                D list2Data = array2[j];
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
     * 求两个集合的差集 如:list1=1,2,3 list2=3.4,5 返回3 求list1的差集合，如果需要数组自己转换
     * 
     * @param <T>   类型
     * @param <D>   类型
     * @param list1 集合1
     * @param list2 集合2
     * @return
     */
    public static <T, D> List<T> sameArray1(T[] array1, D[] array2, Predicate compareFun) {
        nullCheck(array1, array2, compareFun);
        List<T> differenceSet = new ArrayList<T>();
        // 通过eque来进行对比,如果需要改变对比规则需要其内部包含的对象重写equa方法
        for (int i = 0; i < array1.length; i++) {
            // 外层循环
            T list1Data = array1[i];
            boolean deff = false;
            for (int j = 0; j < array2.length; j++) {
                D list2Data = array2[j];
                // 比较外层的listdata和内层的list2data
                if (compareFun.test(new CompareObj(list1Data, list2Data))) {
                    // 代表外层循环已经对比到了相同的数据
                    deff = true;
                    break;
                }
            }
            // 判断是否对比成功
            if (deff) {
                differenceSet.add(list1Data);
            }
        }
        return differenceSet;
    }

}

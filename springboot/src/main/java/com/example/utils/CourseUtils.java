package com.example.utils;

import com.example.common.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CourseUtils {
    //二进制：20位表周次
    private static final int COURSE_NUMBER = 20;
    // 总位数：5位星期 + 12位课次 = 17位
    private static final int WEEK_BITS = 5;    // 星期（1-5）
    private static final int COURSE_BITS = 12; // 课次（1-12）
    private static final int TOTAL_BITS = WEEK_BITS + COURSE_BITS; // 17位

    /**
     * 创建周次位图
     *
     * @param type  类型: "ALL"-全部, "SINGLE"-单周, "DOUBLE"-双周
     * @param total 总周数
     * @return 周次位图的long值
     */
    public static long createNumber(String type, int total) {
        if (total <= 0 || total > COURSE_NUMBER) {
            throw new ValidationException("总周数必须在1到" + COURSE_NUMBER + "之间");
        }
        long bitSet = 0L;
        if ("ALL".equals(type)) {
            // 设置前total位为1
            bitSet = (1L << total) - 1;
        } else if (total <= COURSE_NUMBER / 2) {
            for (int i = 0; i < total; i++) {
                if ("SINGLE".equals(type) && (i + 1) % 2 == 1) {
                    bitSet |= (1L << i); // 设置第i位为1
                } else if ("DOUBLE".equals(type) && (i + 1) % 2 == 0) {
                    bitSet |= (1L << i); // 设置第i位为1
                }
            }
        } else {
            throw new ValidationException("当类型为非全周时，总周数不能超过" + (COURSE_NUMBER / 2));
        }
        return bitSet;
    }

    /**
     * 创建时间位图：5位表星期，12位表课次
     *
     * @param weekTime 格式为: 星期*10000 + 开始时间*100 + 结束时间 (如: 10103 表示星期一，第1-3节课)
     * @return 时间位图的long值
     */
    public static long createTime(int weekTime) {

        long bitSet = 0L;
        int week = weekTime / 10000;
        if (week < 1 || week > 5) {
            throw new IllegalArgumentException("星期必须在1-5之间");
        }
        // 设置星期位（第0-4位）
        bitSet |= (1L << (week - 1));
        int timePart = weekTime % 10000;
        int startTime = timePart / 100;
        int endTime = timePart % 100;

        if (startTime < 1 || startTime > 12 || endTime < 1 || endTime > 12 || startTime > endTime) {
            throw new IllegalArgumentException("课次时间必须在1-12之间，且开始时间不能晚于结束时间");
        }
        // 设置课次位（第5-16位，对应原WEEK_BITS到TOTAL_BITS-1）
        for (int i = startTime; i <= endTime; i++) {
            bitSet |= (1L << (WEEK_BITS + i - 1));
        }
        return bitSet;
    }

    /**
     * 从courseTime二进制值解析出weekTime（格式：星期*10000 + 开始时间*100 + 结束时间）
     *
     * @param courseTime 二进制表示的时间值
     * @return weekTime值
     */
    public static int analysisTime(long courseTime) {
        // 获取星期部分（低5位）
        long weekdayBits = getWeekdayPart(courseTime);
        // 计算星期（找到第一个被设置的位）
        int weekday = 0;
        for (int i = 0; i < 5; i++) {
            if ((weekdayBits & (1L << i)) != 0) {
                weekday = i + 1; // 星期从1开始
                break;
            }
        }
        // 获取课次部分（高12位）
        long classtimeBits = getClassTimePart(courseTime);
        // 找到连续的课次范围
        int startTime = 0, endTime = 0;
        boolean started = false;

        for (int i = 0; i < 12; i++) {
            if ((classtimeBits & (1L << i)) != 0) {
                if (!started) {
                    startTime = i + 1; // 课次从1开始
                    started = true;
                }
                endTime = i + 1; // 更新结束时间
            }
        }
        // 返回格式化的时间字符串：星期*10000 + 开始时间*100 + 结束时间
        return weekday * 10000 + startTime * 100 + endTime;
    }

    /**
     * 从courseNumber二进制值解析出周期类型
     *
     * @param courseNumber 二进制表示的周次值
     * @return 包含周期类型
     */
    public static String analysisWeek(long courseNumber, int total) {

        boolean hasOdd = false;   // 是否包含奇数周
        boolean hasEven = false;  // 是否包含偶数周

        for (int i = 0; i < total; i++) {
            if ((courseNumber & (1L << i)) != 0) {
                if ((i + 1) % 2 == 1) {
                    hasOdd = true;   // 奇数周被设置
                } else {
                    hasEven = true;  // 偶数周被设置
                }
            }
        }

        // 根据奇偶周的设置情况判断周期类型
        if (hasOdd && hasEven) {
            // 如果同时包含奇数周和偶数周，则为全周
            return "ALL";
        } else if (hasOdd && !hasEven) {
            // 只包含奇数周
            return "SINGLE";
        } else if (!hasOdd && hasEven) {
            // 只包含偶数周
            return "DOUBLE";
        } else {
            // 没有任何位被设置
            return "NONE";
        }
    }


    /**
     * 检查指定位是否为1
     */
    public static boolean isBitSet(long value, int position) {
        if (position < 0) {
            throw new IllegalArgumentException("位置不能为负数");
        }
        return (value & (1L << position)) != 0;
    }

    /**
     * 设置指定位为1
     */
    public static long setBit(long value, int position) {
        if (position < 0) {
            throw new IllegalArgumentException("位置不能为负数");
        }
        return value | (1L << position);
    }

    /**
     * 清除指定位
     */
    public static long clearBit(long value, int position) {
        if (position < 0) {
            throw new IllegalArgumentException("位置不能为负数");
        }
        return value & ~(1L << position);
    }

    /**
     * 获取星期部分（低5位）
     */
    public static long getWeekdayPart(long courseTime) {
        return courseTime & 0x1F; // 0x1F = 31 = 2^5 - 1
    }

    /**
     * 获取课次部分（第5-16位）
     */
    public static long getClassTimePart(long courseTime) {
        return (courseTime >> WEEK_BITS) & ((1L << COURSE_BITS) - 1); // 0xFFF = 4095 = 2^12 - 1
    }

    /**
     * 检查是否有时间冲突：检查两个时间位图是否有重叠的位
     */
    public static boolean hasTimeConflict(long time1, long time2) {
        return (time1 & time2) != 0;
    }

    /**
     * 检查是否有周次冲突：检查两个周次位图是否有重叠的位
     */
    public static boolean hasNumberConflict(long number1, long number2) {
        return (number1 & number2) != 0;
    }

}

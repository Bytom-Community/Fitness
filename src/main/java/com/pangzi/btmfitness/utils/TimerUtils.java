package com.pangzi.btmfitness.utils;

import java.util.Calendar;

/**
 * @author zxw
 * @date 2018.11.09
 */
public class TimerUtils {

    public static String formatDateByTimes(Long times) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(times);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuffer stringBuffer = new StringBuffer();
        if (month < 10 && day < 10) {
            stringBuffer = stringBuffer.append(year).append("-").append("0").append(month).append("-").append("0").append(day);
        } else if (month < 10) {
            stringBuffer = stringBuffer.append(year).append("-").append("0").append(month).append("-").append(day);
        } else if (day < 10) {
            stringBuffer = stringBuffer.append(year).append("-").append(month).append("-").append("0").append(day);
        } else {
            stringBuffer = stringBuffer.append(year).append("-").append(month).append("-").append(day);
        }
        return stringBuffer.toString();
    }

    /**
     * 获取指定日期的0.0.0.000点时间戳
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Long getTimesZeroByDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定日期的23.59.59.999点时间戳
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Long getTimesEndByDay(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当天0.0.0.000点时间戳
     *
     * @return
     */
    public static Long getTimesDayZero() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取当天23.59.59.999时间戳
     *
     * @return
     */
    public static Long getTimesDayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    /**
     * 获取指定年份指定月数的第一天0点时间戳
     *
     * @return
     */
    public static Long getTimesFirstDayMonth(int year, int month) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();

    }

    /**
     * 获取指定年份指定月份的最后一天24点时间戳
     *
     * @return
     */
    public static Long getTimesLastDayMonth(int year, int month, int lastDay) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();

    }

    /**
     * 获取指定年份指定月数的最后一天
     *
     * @param currentMonth
     * @return
     */
    public static int getLastDayMonth(int year, int currentMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, currentMonth, 1);
        calendar.add(Calendar.DATE, -1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }
}

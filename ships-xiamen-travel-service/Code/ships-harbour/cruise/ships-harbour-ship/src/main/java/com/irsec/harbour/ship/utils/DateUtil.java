package com.irsec.harbour.ship.utils;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    /**
     * 两个相差的秒数
     * 相减
     * @param date1
     * @param date2
     * @return
     */
    public static long dateSubtract(Date date1, Date date2){
        if(date1 == null || date2 == null){
            return -1;
        }
        return (Math.abs(date1.getTime() - date2.getTime()))/1000;
    }

    /**
     * 只需要 Date 类型的日期比较
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean dateEqual(Date date1, Date date2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        if (date1 == null && date2 == null) {
            return true;
        } else if (date1 != null && date2 != null) {
            return simpleDateFormat.format(date1).equals(simpleDateFormat.format(date2));
        } else {
            return false;
        }


    }

    /**
     * 截取只保留日期
     *
     * @param date
     * @return
     */
    public static Date truncateDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DATE);
        cal.set(y, m, d, 0, 0, 0);

        return cal.getTime();
    }


    /**
     * 增加天数
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.DAY_OF_MONTH, days);

        return cal.getTime();
    }

    public static Date addTruncateDays(Date date, int days) {
        Date truncateDate = truncateDate(date);
        return addDays(truncateDate, days);
    }


    /**
     * 增加几个月
     *
     * @param date
     * @param months
     * @return
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    public static Date strToDate(String str, String format){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        Date date = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static String dateToStr(Date date, String format){
        if(date == null){
            return null;
        }
        String str = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        str = simpleDateFormat.format(date);
        return str;
    }

    public static Date getBeforeOneYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        return c.getTime();
    }

    public static Date getBeforeOneMonth(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    public static Date getToday0dian(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getToday24dian(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

//    public static Date getBeforeOneHour(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.HOUR_OF_DAY,-1);
//        return calendar.getTime();
//    }

    public static Date getBeforOneHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static Date getAfterOneHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}
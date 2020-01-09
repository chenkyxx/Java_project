package com.irsec.harbour.ship;

import com.irsec.harbour.ship.utils.DateUtil;
import com.irsec.harbour.ship.utils.PinYinUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class UtilsTest {

    @Test
    public void testPinYin() {
        PinYinUtil pinYinUtil = new PinYinUtil();


        System.out.println("abc123".startsWith("abc123"));

        System.out.println(System.currentTimeMillis());
        System.out.println(pinYinUtil.toPinYin("中国美大康"));
        System.out.println(pinYinUtil.toPinYin("Kobe"));
        System.out.println(pinYinUtil.toPinYin("1"));
        System.out.println(pinYinUtil.toPinYin("0"));
    }

    @Test
    public void testCurrentTimeMillis() throws InterruptedException {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            System.out.println(System.currentTimeMillis());


            Random rand = new Random();
            String randomNum = String.format("%06d", rand.nextInt(999999));
            System.out.println(randomNum);

            if (list.contains(randomNum)) {
                System.out.println("重复");
            } else {
                list.add(randomNum);
            }
        }
    }

    @Test
    public void testAscii() {

        String newBarCode = "";
        for (int i = 0; i < 6; i++) {
            Random rand = new Random();
            int num = rand.nextInt(36);

            if (num < 10) {
                newBarCode = newBarCode + num;
            } else {
                newBarCode = newBarCode + ((char) ('a' + num - 10));
            }
        }

        System.out.println(newBarCode);
    }


    @Test
    public void testDateUtil() {

//        System.out.println("今天=" + DateUtil.addDays(new Date(), 0));
//        System.out.println("明天=" + DateUtil.addDays(new Date(), 1));
//        System.out.println("昨天=" + DateUtil.addDays(new Date(), -1));
//        System.out.println("10天后=" + DateUtil.addDays(new Date(), 10));
//        System.out.println("下个月=" + DateUtil.addMonths(new Date(), 1));
//        System.out.println("上个月=" + DateUtil.addMonths(new Date(), -1));
//
//        System.out.println("今天=" + DateUtil.truncateDate(new Date()));
//
//        System.out.println("明天=" + DateUtil.addTruncateDays(new Date(), -1));
    }


    @Test
    public void testSign() {



        String s1 = (
                "/api/v1/flight/query" + "20190713230352" + "3a8a427582004ce7a30cadb3e13cf057");

        //s1 = "api/v1/flight/query201907132236003a8a427582004ce7a30cadb3e13cf057";

        String s = DigestUtils.md5DigestAsHex(s1.getBytes());
        System.out.println(s);



    }

}

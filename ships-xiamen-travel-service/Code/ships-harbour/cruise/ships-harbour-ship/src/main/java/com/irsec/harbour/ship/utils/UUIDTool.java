package com.irsec.harbour.ship.utils;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class UUIDTool {
    public static String newUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    //
    public static String getRandomCh(){
        char[] words = {'0','1','2','3','4','5','6','7','8','9','a'
                ,'b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s'
                ,'t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K'
                ,'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        StringBuffer sb = new StringBuffer();
        int i=1;
        int lastIndex =-1;
        Random random =new Random();
        while (i<=2){
            int index = random.nextInt(words.length);
            if(index != lastIndex){
                sb.append(words[index]);
                lastIndex = index;
                i++;
            }
        }
        return sb.toString();
    }
    //将一个字符串字符顺序打乱
    public static String getStrOutOfOrder(String passportId){
        char[] chars = passportId.toCharArray();
        int[] flag = new int[passportId.length()];
        StringBuffer sb = new StringBuffer();
        Random random =new Random();
        int i=0;
        while (i<passportId.length()){
            int tempIndex = random.nextInt(passportId.length());
            if(flag[tempIndex] == 0){
                sb.append(tempIndex);
                flag[tempIndex] = -1;
                i++;
            }
        }
        return sb.toString();
    }


}

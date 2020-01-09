package com.irsec.harbour.ship.utils;

import org.assertj.core.util.Arrays;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Jethro
 * @Date: 2019/8/23 16:50
 * @Description:
 */
public class BeanExtUtil {

    public static String[] getNullProperty(Object object)throws Exception{
        Field[] fields;
        List<String> nullPropertyList = new ArrayList<>();
        if(object == null){
            return null;
        }
        fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(StringUtils.isEmpty(field.get(object))){
                nullPropertyList.add(field.getName());
            }
        }

        String[] nullPropertyArray = new String[nullPropertyList.size()];
        return nullPropertyList.toArray(nullPropertyArray);
    }


    public static void setValue(Object object,String property, Object value) throws IllegalAccessException {
        Field[] fields;
        List<String> nullPropertyList = new ArrayList<>();

        fields = object.getClass().getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            String name = field.getName();
            if(name.compareTo(property) == 0){
                field.set(name,value);
            }
        }
    }
}

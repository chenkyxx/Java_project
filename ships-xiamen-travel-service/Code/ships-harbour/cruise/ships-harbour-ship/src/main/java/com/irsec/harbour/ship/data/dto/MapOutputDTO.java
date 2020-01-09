package com.irsec.harbour.ship.data.dto;

import java.util.HashMap;
import java.util.Map;

public class MapOutputDTO extends BaseOutputDTO {

    private Map<String, Object> data = new HashMap();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key)
    {
        return data.get(key);
    }

    public boolean containsKey(String key)
    {
        return data.containsKey(key);
    }


    public Map<String, Object> getData() {
        return data;
    }
}

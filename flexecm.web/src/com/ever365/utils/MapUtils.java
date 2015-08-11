package com.ever365.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
    public static <V, K> Map<K, V> newMap(K k, V v) {
        Map m = new HashMap(1);
        m.put(k, v);
        return m;
    }

    public static <V, K> Map<K, V> tribleMap(K k1, V v1,K k2, V v2,K k3, V v3) {
        Map m = new HashMap(3);
        m.put(k1, v1);
        m.put(k2, v2);
        m.put(k3, v3);
        return m;
    }

    public static <K, X> String get(Map<K, X> map, K k) {
        if (map.get(k) == null) {
            return null;
        }
        Object v = map.get(k);
        return v.toString();
    }

    public static void putToMap(Map<String, Object> map, String outerKey, String innerKey, Object value) {
        if (map.get(outerKey) == null) {
            map.put(outerKey, newMap(innerKey, value));
        } else {
            Map innerMap = (Map) map.get(outerKey);
            innerMap.put(innerKey, value);
        }
    }
}


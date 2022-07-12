package context;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TestStore {

    private static final ThreadLocal<ConcurrentHashMap<Object, Object>> map = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static synchronized void put (Object key, Object value) {
        if (key != null && value != null) {map.get().put(key, value);}
    }

    public static synchronized Object remove (Object key) {
        return key != null ? ((ConcurrentHashMap<?, ?>)map.get()).remove(key) : null;
    }

    public static synchronized Object get (Object key) {
        return key != null ? ((ConcurrentHashMap<?, ?>)map.get()).get(key) : null;
    }

    public static synchronized Set<Object> items() {
        return Collections.unmodifiableSet(((ConcurrentHashMap)map.get()).keySet());
    }

    static synchronized void clear() {((ConcurrentHashMap<?, ?>)map.get()).clear();}
}

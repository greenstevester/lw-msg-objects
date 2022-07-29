package net.greensill.lw.msg.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode
public class MsgHeader implements Map<String,Object>, Serializable {

    public static final String MSG_HEADER_IS_IMMUTABLE = "Msg header is immutable";

    private final Map<String, Object> headerMap;

    private MsgHeader() {headerMap = Collections.emptyMap();}

    @JsonCreator
    public MsgHeader(Map<String,Object> header) {
        headerMap = (!header.isEmpty()) ? Maps.newHashMap(header) : Collections.emptyMap();
    }

    public MsgHeader and(String key, Object value) {
        Map<String, Object> map = Maps.newHashMap(headerMap);
        map.put(key,value);
        return new MsgHeader(map);
    }

    public static MsgHeader plus(String key, Object value) {
        return new MsgHeader(Collections.singletonMap(key, value));
    }

    public static MsgHeader emptyMsgHeader() {return new MsgHeader();}

    @Override
    public int size() {
        return headerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return headerMap.isEmpty();
    }

    public boolean isNullOrEmpty() {
        return headerMap==null || isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return headerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return headerMap.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return headerMap.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        throw new UnsupportedOperationException(MSG_HEADER_IS_IMMUTABLE);
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException(MSG_HEADER_IS_IMMUTABLE);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException(MSG_HEADER_IS_IMMUTABLE);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(MSG_HEADER_IS_IMMUTABLE);
    }

    @Override
    public Set<String> keySet() {
        return headerMap.keySet();
    }

    @Override
    public Collection<Object> values() {
        return headerMap.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return headerMap.entrySet();
    }

    protected Object readResolve() {
        if (isNullOrEmpty()) {
            return MsgHeader.emptyMsgHeader();
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(", values=[");
        if (!isNullOrEmpty()) {
            sb.append(Joiner.on(",").withKeyValueSeparator("=").join(headerMap));
        }
        return sb.append("]").toString();
    }
}

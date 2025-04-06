package uj.wmii.pwj.map2d;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Map2DImpl<R, C, V> implements Map2D<R, C, V> {

    private Map<R, Map<C,V>> map = new HashMap<>();

    @Override
    public V put(R rowKey, C columnKey, V value) {
        if(rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> row = map.computeIfAbsent(rowKey, k -> new HashMap<>());
        return row.put(columnKey, value);
    }

    @Override
    public V get(R rowKey, C columnKey) {
        if(rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> row = map.get(rowKey);
        if(row == null) return null;
        return row.get(columnKey);
    }

    @Override
    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        if(rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> row = map.get(rowKey);
        if(row == null || row.get(columnKey) == null) return defaultValue;
        return row.get(columnKey);
    }

    @Override
    public V remove(R rowKey, C columnKey) {
        if(rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> row = map.get(rowKey);
        if(row == null) return null;
        return row.remove(columnKey);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean nonEmpty() {
        return !map.isEmpty();
    }

    @Override
    public int size() {
        int size = 0;
        for(R rowKey : map.keySet()) {
            size += map.get(rowKey).size();
        }
        return size;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Map<C, V> rowView(R rowKey) {
        if(rowKey == null) throw new NullPointerException();
        Map<C, V> row = map.get(rowKey);
        if(row == null) return new HashMap<>();
        return Collections.unmodifiableMap(row);
    }

    @Override
    public Map<R, V> columnView(C columnKey) {
        if(columnKey == null) throw new NullPointerException();
        Map<R,V> column = new HashMap<>();
        for(R rowKey : map.keySet()) {
            Map<C,V> value = map.get(rowKey);
            if(value.containsKey(columnKey) )
                column.put(rowKey, value.get(columnKey));
        }
        return Collections.unmodifiableMap(column);
    }

    @Override
    public boolean containsValue(V value) {
        for(R rowKey : map.keySet()) {
            if(map.get(rowKey).containsValue(value)) return true;
        }
        return false;
    }

    @Override
    public boolean containsKey(R rowKey, C columnKey) {
        if(rowKey == null || columnKey == null) throw new NullPointerException();
        Map<C, V> row = map.get(rowKey);
        return row != null && row.containsKey(columnKey);
    }

    @Override
    public boolean containsRow(R rowKey) {
        if(rowKey == null) throw new NullPointerException();
        Map<C, V> row = map.get(rowKey);
        if(row == null) return false;
        return !row.isEmpty();
    }

    @Override
    public boolean containsColumn(C columnKey) {
        if(columnKey == null) throw new NullPointerException();
        for (Map<C, V> rowMap : map.values()) {
            if (rowMap.containsKey(columnKey) && rowMap.get(columnKey) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<R, Map<C, V>> rowMapView() {
        if(map.isEmpty()) return Collections.emptyMap();
        Map<R, Map<C, V>> rowMap = new HashMap<>();
        for(R rowKey : map.keySet()) {
            Map<C, V> value = Map.copyOf(map.get(rowKey));
            rowMap.put(rowKey, value);
        }
        return Collections.unmodifiableMap(rowMap);
    }

    @Override
    public Map<C, Map<R, V>> columnMapView() {
        if(map.isEmpty()) return Collections.emptyMap();
        Map<C, Map<R, V>> columnMap = new HashMap<>();
        for (Map.Entry<R, Map<C, V>> rowEntry : map.entrySet()) {
            R rowKey = rowEntry.getKey();
            Map<C, V> rowValues = rowEntry.getValue();
            for (Map.Entry<C, V> columnEntry : rowValues.entrySet()) {
                C columnKey = columnEntry.getKey();
                V value = columnEntry.getValue();
                columnMap.computeIfAbsent(columnKey, k -> new HashMap<>()).put(rowKey, value);
            }
        }
        return Collections.unmodifiableMap(columnMap);
    }


    @Override
    public Map2D<R, C, V> fillMapFromRow(Map<? super C, ? super V> target, R rowKey) {
        if(rowKey == null) throw new NullPointerException();
        Map<C,V> row = map.get(rowKey);
        if(row == null) return null;
        target.putAll(row);
        return this;
    }

    @Override
    public Map2D<R, C, V> fillMapFromColumn(Map<? super R, ? super V> target, C columnKey) {
        if(columnKey == null) throw new NullPointerException();
        for(Map.Entry<R, Map<C, V>> rowEntry : map.entrySet()){
            R rowKey = rowEntry.getKey();
            Map<C,V> rowValues = rowEntry.getValue();
            if(rowValues.containsKey(columnKey)) {
                target.put(rowKey, rowValues.get(columnKey));
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAll(Map2D<? extends R, ? extends C, ? extends V> source) {
        for(R rowKey : source.rowMapView().keySet()) {
            Map<? extends C, ? extends V> rowValue = source.rowMapView().get(rowKey);
            for(Map.Entry<? extends C, ? extends V> rowEntry : rowValue.entrySet()) {
                C columnKey = rowEntry.getKey();
                V value = rowEntry.getValue();
                this.put(rowKey, columnKey, value);
            }
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToRow(Map<? extends C, ? extends V> source, R rowKey) {
        if(rowKey == null) throw new NullPointerException();
        for(Map.Entry<? extends C, ? extends V> rowEntry : source.entrySet()) {
            C columnKey = rowEntry.getKey();
            V value = rowEntry.getValue();
            this.put(rowKey, columnKey, value);
        }
        return this;
    }

    @Override
    public Map2D<R, C, V> putAllToColumn(Map<? extends R, ? extends V> source, C columnKey) {
        if(columnKey == null) throw new NullPointerException();
        for(Map.Entry<? extends R, ? extends V> rowEntry : source.entrySet()) {
            R rowKey = rowEntry.getKey();
            V value = rowEntry.getValue();
            this.put(rowKey, columnKey, value);
        }
        return this;
    }

    @Override
    public <R2, C2, V2> Map2D<R2, C2, V2> copyWithConversion(Function<? super R, ? extends R2> rowFunction, Function<? super C, ? extends C2> columnFunction, Function<? super V, ? extends V2> valueFunction) {
        Map2DImpl<R2, C2, V2> newMap= new Map2DImpl<>();
        for(R rowKey : map.keySet()) {
            Map<C,V> row = map.get(rowKey);
            for(Map.Entry<C,V> rowEntry : row.entrySet()) {
                C columnKey = rowEntry.getKey();
                V value = rowEntry.getValue();
                newMap.put(rowFunction.apply(rowKey), columnFunction.apply(columnKey), valueFunction.apply(value));
            }
        }
        newMap.map = Collections.unmodifiableMap(newMap.map);
        return newMap;
    }
}

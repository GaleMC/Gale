package org.galemc.gale.registry.valuebasedmap;

import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A highly optimized map where the key type extends {@link RegistryIdAware}.
 *
 * <p>
 * Not every method of {@link Map} is implemented.
 * </p>
 *
 * <p>
 * It stores the values in an array of the same length as {@link #possibleKeys}.
 * The array is only constructed if a value is added.
 * </p>
 *
 * <p>
 * It cannot be used with {@code null} keys or values.
 * </p>
 */
public class RegistryIdAware2ObjectArrayMap<K extends RegistryIdAware, V> implements Map<K, V> {

    private final K[] possibleKeys;
    private @Nullable Object @Nullable [] values;
    private int size;

    public RegistryIdAware2ObjectArrayMap(K[] possibleKeys) {
        this.possibleKeys = possibleKeys;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable V get(Object key) {
        if (this.values == null) {
            return null;
        }
        return (V) this.values[((K) key).getIdInRegistry()];
    }

    @Override
    public V put(K key, V value) {
        if (this.values == null) {
            this.values = new Object[this.possibleKeys.length];
            this.values[key.getIdInRegistry()] = value;
            this.size = 1;
            return value;
        }
        return this.putWithNonNullValues(key, value);
    }

    private V putWithNonNullValues(K key, V value) {
        int index = key.getIdInRegistry();
        if (this.values[index] == null) {
            this.size++;
        }
        this.values[index] = value;
        return value;
    }

    @Override
    public @Nullable V remove(Object key) {
        if (this.values == null) {
            return null;
        }
        int index = ((K) key).getIdInRegistry();
        Object existingValue = this.values[index];
        if (existingValue == null) {
            return null;
        }
        this.values[index] = null;
        this.size--;
        return (V) existingValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (this.values == null) {
            this.values = new Object[this.possibleKeys.length];
        }
        map.forEach(this::putWithNonNullValues);
    }

    @Override
    public void clear() {
        this.values = null;
        this.size = 0;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    private class ValuesCollection implements Collection<V> {

        @Override
        public int size() {
            return RegistryIdAware2ObjectArrayMap.this.size;
        }

        @Override
        public boolean isEmpty() {
            return RegistryIdAware2ObjectArrayMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return RegistryIdAware2ObjectArrayMap.this.containsValue(o);
        }

        private class Iterator implements java.util.Iterator<V> {

            private int arrayIndex;

            Iterator() {
                this.skipForward();
            }

            private void skipForward() {
                while (this.arrayIndex < RegistryIdAware2ObjectArrayMap.this.size && RegistryIdAware2ObjectArrayMap.this.values[arrayIndex] == null) {
                    this.arrayIndex++;
                }
            }

            @Override
            public boolean hasNext() {
                return this.arrayIndex < RegistryIdAware2ObjectArrayMap.this.size;
            }

            @Override
            public V next() {
                V toReturn = (V) RegistryIdAware2ObjectArrayMap.this.values[this.arrayIndex++];
                this.skipForward();
                return toReturn;
            }

        }

        @Override
        public java.util.Iterator<V> iterator() {
            return new Iterator();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(V v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Collection<V> values() {
        if (this.values == null) {
            return Collections.emptyList();
        }
        return new ValuesCollection();
    }

    private class EntrySet implements Set<Map.Entry<K, V>> {

        @Override
        public int size() {
            return RegistryIdAware2ObjectArrayMap.this.size;
        }

        @Override
        public boolean isEmpty() {
            return RegistryIdAware2ObjectArrayMap.this.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            throw new UnsupportedOperationException();
        }

        private class Iterator implements java.util.Iterator<java.util.Map.Entry<K, V>> {

            private static class Entry<K, V> implements java.util.Map.Entry<K, V> {

                K key;
                V value;

                @Override
                public K getKey() {
                    return this.key;
                }

                @Override
                public V getValue() {
                    return this.value;
                }

                @Override
                public V setValue(V value) {
                    V oldValue = this.value;
                    this.value = value;
                    return oldValue;
                }

            }

            private int arrayIndex;
            private Entry<K, V> entry;

            Iterator() {
                this.skipForward();
            }

            private void skipForward() {
                while (this.arrayIndex < RegistryIdAware2ObjectArrayMap.this.size && RegistryIdAware2ObjectArrayMap.this.values[arrayIndex] == null) {
                    this.arrayIndex++;
                }
            }

            @Override
            public boolean hasNext() {
                return this.arrayIndex < RegistryIdAware2ObjectArrayMap.this.size;
            }

            @Override
            public Entry<K, V> next() {
                this.entry.key = RegistryIdAware2ObjectArrayMap.this.possibleKeys[this.arrayIndex];
                this.entry.value = (V) RegistryIdAware2ObjectArrayMap.this.values[this.arrayIndex++];
                this.skipForward();
                return this.entry;
            }

        }

        @Override
        public java.util.Iterator<Entry<K, V>> iterator() {
            return new Iterator();
        }

        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Entry<K, V> kvEntry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException();
        }

    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (this.values == null) {
            return Collections.emptySet();
        }
        return new EntrySet();
    }

    @Override
    public @Nullable V getOrDefault(Object key, @Nullable V defaultValue) {
        V value = this.get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public @Nullable V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (this.values == null) {
            V value = mappingFunction.apply(key);
            this.values = new Object[this.possibleKeys.length];
            this.values[key.getIdInRegistry()] = value;
            this.size = 1;
            return value;
        }
        int index = key.getIdInRegistry();
        Object existingValue = this.values[index];
        if (existingValue != null) {
            return (V) existingValue;
        }
        V value = mappingFunction.apply(key);
        this.values[index] = value;
        this.size++;
        return value;
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (this.values == null) {
            return;
        }
        for (int i = 0; i < this.values.length; i++) {
            Object value = this.values[i];
            if (value != null) {
                action.accept(this.possibleKeys[i], (V) value);
            }
        }
    }

}

package net.shadowking21.stylishbattleranks.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.LinkedList;

import java.util.LinkedList;

public class BoundedList<E> {
    private final int maxSize;
    private final LinkedList<Wrapper<E>> list;

    public BoundedList(int maxSize) {
        this.maxSize = maxSize;
        this.list = new LinkedList<>();
    }
    public void add(E element) {
        if (list.size() >= maxSize) {
            list.removeLast();
        }
        list.addFirst(new Wrapper<>(element));
    }
    public E get(int index) {
        return list.get(index).getValue();
    }

    public int getMaxSize() {
        return maxSize;
    }
    public int size() {
        return list.size();
    }

    public String getType(int index) {
        return list.get(index).getType();
    }

    @Override
    public String toString() {
        return list.toString();
    }
    private static class Wrapper<T> {
        private final T value;

        public Wrapper(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public String getType() {
            return value.getClass().getSimpleName();
        }

        @Override
        public String toString() {
            return value + " (тип: " + getType() + ")";
        }
    }
}

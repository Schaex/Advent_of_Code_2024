package com.schaex.util;

import java.util.Iterator;
import java.util.function.Consumer;

public class MultiIterator<T> implements Iterator<T> {
    private final Iterable<T>[] iterables;
    private Iterator<T> current;
    private int cursor = 0;

    public MultiIterator(Iterable<T>... iterables) {
        this.iterables = iterables;
    }

    @Override
    public boolean hasNext() {
        if (cursor >= iterables.length) {
            return false;
        }

        if (current == null) {
            current = iterables[cursor++].iterator();
        }

        if (current.hasNext()) {
            return true;
        }

        current = null;

        return hasNext();
    }

    @Override
    public T next() {
        return current.next();
    }

    public static <T> void forEach(Consumer<T> action, Iterable<T>... iterables) {
        for (Iterable<T> iterable : iterables) {
            iterable.forEach(action);
        }
    }
}

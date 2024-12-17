package com.schaex.util;

import java.util.function.Supplier;

public class Lazy<T> implements Supplier<T> {
    private Supplier<T> supplier;
    private T item;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (supplier != null) {
            item = supplier.get();
            supplier = null;
        }

        return item;
    }
}

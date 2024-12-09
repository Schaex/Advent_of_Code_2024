package com.schaex.util;

public interface PublicCloneable<T> extends Cloneable {
    T clone() throws CloneNotSupportedException;
}

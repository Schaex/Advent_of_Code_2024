package com.schaex.util.tuples;

public class Pair<A, B> {
    private A left;
    private B right;

    public A left() {
        return left;
    }

    public void setLeft(A left) {
        this.left = left;
    }

    public B right() {
        return right;
    }

    public void setRight(B right) {
        this.right = right;
    }

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }
}

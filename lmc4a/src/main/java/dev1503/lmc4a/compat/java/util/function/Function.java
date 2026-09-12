package dev1503.lmc4a.compat.java.util.function;

public interface Function<T, R> {
    R apply(T t);

    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        if (before == null) {
            throw new NullPointerException("before cannot be null");
        }
        return (V v) -> apply(before.apply(v));
    }

    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        if (after == null) {
            throw new NullPointerException("after cannot be null");
        }
        return (T t) -> after.apply(apply(t));
    }

    static <T> Function<T, T> identity() {
        return t -> t;
    }
}
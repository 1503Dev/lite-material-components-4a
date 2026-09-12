package dev1503.lmc4a.compat.java.util;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Optional<T> {
    private static final Optional<?> EMPTY = new Optional<>();
    private final T value;

    private Optional() {
        this.value = null;
    }

    private Optional(T value) {
        this.value = value;
    }

    public static <T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    public static <T> Optional<T> of(T value) {
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        return new Optional<>(value);
    }

    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public void ifPresent(Action<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    public void ifPresentOrElse(Action<? super T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public Optional<T> filter(Predicate<? super T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("Predicate cannot be null");
        }
        if (!isPresent()) {
            return this;
        }
        return predicate.test(value) ? this : empty();
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        if (mapper == null) {
            throw new NullPointerException("Mapper cannot be null");
        }
        if (!isPresent()) {
            return empty();
        }
        return Optional.ofNullable(mapper.apply(value));
    }

    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        if (mapper == null) {
            throw new NullPointerException("Mapper cannot be null");
        }
        if (!isPresent()) {
            return empty();
        }
        Optional<U> result = mapper.apply(value);
        if (result == null) {
            throw new NullPointerException("Mapper returned null");
        }
        return result;
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        if (supplier == null) {
            throw new NullPointerException("Supplier cannot be null");
        }
        return value != null ? value : supplier.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (exceptionSupplier == null) {
            throw new NullPointerException("Exception supplier cannot be null");
        }
        if (value != null) {
            return value;
        }
        throw exceptionSupplier.get();
    }

    public T orElseThrow() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Optional)) {
            return false;
        }
        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null ? String.format("Optional[%s]", value) : "Optional.empty";
    }

    public interface Action<T> {
        void accept(T t);
    }

    public interface Predicate<T> {
        boolean test(T t);
    }

    public interface Function<T, R> {
        R apply(T t);
    }

    public interface Supplier<T> {
        T get();
    }
}
package org.machinemc.scriptive.formatify;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Result<T> {

    private final T value;
    private final @Nullable String error;

    private Result(T value, @Nullable String error) {
        this.value = value;
        this.error = error;
    }

    public T value() {
        return value;
    }

    public boolean successful() {
        return error == null;
    }

    public @Nullable String error() {
        return error;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Result<?> that = (Result<?>) obj;
        return Objects.equals(this.value, that.value) &&
                Objects.equals(this.error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, error);
    }

    @Override
    public String toString() {
        return "Result[" +
                "value=" + value + ", " +
                "error=" + error + ']';
    }

    public static <T> Result<T> of(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> error(String error) {
        return new Result<>(null, error);
    }

}

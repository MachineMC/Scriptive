package org.machinemc.scriptive.formatify.parameter;

import org.jetbrains.annotations.NotNull;
import org.machinemc.scriptive.formatify.Formatify;
import org.machinemc.scriptive.formatify.exceptions.ParseException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

public class ArgumentQueue implements Iterable<String> {

    private final Formatify formatify;
    private final Queue<String> queue;

    public ArgumentQueue(Formatify formatify) {
        this.formatify = formatify;
        this.queue = new LinkedList<>();
    }

    public ArgumentQueue(ArgumentQueue queue) {
        this.formatify = queue.formatify;
        this.queue = new LinkedList<>(queue.queue);
    }

    public void offer(String argument) {
        queue.offer(argument);
    }

    public String poll() {
        return queue.poll();
    }

    public String pollOr(String error) throws ParseException {
        String argument = queue.poll();
        if (argument == null) throw new ParseException(error);
        return argument;
    }

    public <T> T poll(Function<String, T> function) {
        return function.apply(queue.poll());
    }

    public <T> T pollOr(Function<String, T> function, String error) {
        return function.apply(pollOr(error));
    }

    public <T> T pollOrDefault(Function<String, T> function, T defaultValue) {
        T value = function.apply(queue.poll());
        return value != null ? value : defaultValue;
    }

    public String peek() {
        return queue.peek();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public @NotNull Iterator<String> iterator() {
        return queue.iterator();
    }

    public Formatify formatify() {
        return formatify;
    }

    @Override
    public String toString() {
        return queue.toString();
    }

}

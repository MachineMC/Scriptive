package org.machinemc.scriptive.formatify;

import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.arguments.Argument;
import org.machinemc.scriptive.formatify.arguments.ParsedArguments;
import org.machinemc.scriptive.formatify.lexer.token.Token;
import org.machinemc.scriptive.formatify.tags.*;
import org.machinemc.scriptive.formatify.tree.Node;
import org.machinemc.scriptive.formatify.tree.RootNode;
import org.machinemc.scriptive.formatify.tree.TagNode;
import org.machinemc.scriptive.formatify.tree.TextNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

public class FormatifyParser {

    public static final char TAG_START = '<', TAG_CLOSE = '/', TAG_END = '>', ARGUMENT_SEPARATOR = ':';

    public static RootNode buildTree(
            String message,
            LinkedList<Token> tokens,
            TagRegistry registry,
            Consumer<String> errorHandler
    ) {
        RootNode root = new RootNode();
        Node current = root;
        LinkedList<String> openTags = new LinkedList<>();
        for (Token token : tokens) {
            switch (token.type()) {
                case TEXT -> current.addChild(new TextNode(current, token.value()));
                case TAG_OPEN -> {
                    ParsedTag tag = parseTag(token.value(), registry);
                    if (tag == null) {
                        current.addChild(new TextNode(current, token.substring(message)));
                        break;
                    }

                    if (!tag.successful()) {
                        current.addChild(new TextNode(current, token.substring(message)));
                        errorHandler.accept(tag.error());
                        break;
                    }

                    if (tag.tag() == DefaultTags.RESET) {
                        openTags.clear();
                        current = root;
                    } else {
                        openTags.addFirst(tag.label());
                        current.addChild(current = new TagNode(current, tag));
                    }
                }
                case TAG_CLOSE -> {
                    if (token.value().equals("reset"))
                        break;

                    int index = 1;
                    boolean found = false;
                    for (String tag : openTags) {
                        if (tag.equals(token.value())) {
                            found = true;
                            break;
                        }
                        index++;
                    }
                    if (!found) {
                        current.addChild(new TextNode(current, token.substring(message)));
                        break;
                    }
                    for (int i = 0; i < index; i++) {
                        current = current.getParent() == null ? root : current.getParent();
                        openTags.removeFirst();
                    }
                }
            }
        }
        return root;
    }

    public static TextComponent componentFromTree(Node node) {
        TextComponent component = node.evaluate();
        if (node.getChildren().isEmpty())
            return component;

        for (Node child : node.getChildren())
            component.append(componentFromTree(child));

        return component;
    }

    private static ParsedTag parseTag(String string, TagRegistry registry) {
        String[] split = split(string, FormatifyParser.ARGUMENT_SEPARATOR);
        String identifier = split[0];
        Tag tag = registry.getTag(identifier, true).orElse(null);
        if (tag == null)
            return null;
        String[] args = tag instanceof DynamicTag ? split : new String[split.length - 1];
        if (split.length > 1 && args.length == split.length - 1)
            System.arraycopy(split, 1, args, 0, args.length);
        ParsedArguments parsedArguments;
        if (tag.getTagParser() != null) {
            parsedArguments = tag.getTagParser().parse(identifier, args);
        } else {
            parsedArguments = parseArguments(tag, args);
        }
        return new ParsedTag(tag, parsedArguments, identifier);
    }

    private static ParsedArguments parseArguments(Tag tag, String[] args) {
        Map<String, Object> parsedArguments = new HashMap<>();
        int index = 0;
        for (Map.Entry<String, Argument<?>> entry : tag.getArguments().entrySet()) {
            try {
                Result<?> parsed = entry.getValue().parse(args[index++]);
                if (!parsed.successful())
                    return new ParsedArguments(parsed.error());
                parsedArguments.put(entry.getKey(), parsed.value());
            } catch (IndexOutOfBoundsException e) {
                return new ParsedArguments("Not enough arguments");
            }
        }
        if (index < args.length)
            return new ParsedArguments("Too many arguments");
        return new ParsedArguments(parsedArguments);
    }

    private static String[] split(String string, char splitChar) {
        int length = string.length();
        int count = 1;
        for (int i = 0; i < length; i++) {
            if (string.charAt(i) == splitChar)
                count++;
        }
        String[] strings = new String[count];

        int startIndex = 0;
        int arrayIndex = 0;
        for (int i = 0; i < length; i++) {
            if (splitChar != string.charAt(i))
                continue;
            strings[arrayIndex++] = string.substring(startIndex, i);
            startIndex = i + 1;
        }

        strings[arrayIndex] = string.substring(startIndex);

        return strings;
    }

}

package org.machinemc.scriptive.formatify.parser;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.Formatify;
import org.machinemc.scriptive.formatify.exceptions.FormatifyException;
import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.formatify.lexer.token.TokenType;
import org.machinemc.scriptive.formatify.parameter.ArgumentQueue;
import org.machinemc.scriptive.formatify.lexer.LexicalAnalyzer;
import org.machinemc.scriptive.formatify.lexer.token.Token;
import org.machinemc.scriptive.formatify.tag.Tag;

import java.util.*;

public class FormatifyParser {

    private static final char ARGUMENT_SEPARATOR = ':';
    
    private final Formatify formatify;
    private final Deque<Token> tokens;

    public FormatifyParser(Formatify formatify, String string) {
        this(formatify, new LexicalAnalyzer(string).tokenize());
    }

    public FormatifyParser(Formatify formatify, Deque<Token> tokens) {
        this.formatify = formatify;
        this.tokens = tokens;
    }

    public TextComponent parse() {
        TextComponent root = TextComponent.empty();
        while (!tokens.isEmpty()) {
            Token token = tokens.peek();
            TextComponent component = null;
            if (token.type() == TokenType.TAG_OPEN)
                component = handleTag();
            if (component == null) {
                tokens.removeFirst();
                component = TextComponent.of(token.raw());
            }
            root.append(component);
        }
        return root;
    }

    private TextComponent handleTag() {
        Token openTag = tokens.removeFirst();
        String[] tagArray = split(openTag.data(), ARGUMENT_SEPARATOR);
        Tag tag = parseTag(tagArray);
        if (tag == null) {
            tokens.addFirst(openTag);
            return null;
        }
        Deque<Token> subTokens = new LinkedList<>();
        int level = 1;
        while (!tokens.isEmpty()) {
            Token token = tokens.poll();
            if (token.type() == TokenType.TAG_OPEN && token.data().equals("reset"))
                break;
            if (token.type() == TokenType.TEXT_LITERAL || !token.data().equals(tagArray[0])) {
                subTokens.add(token);
                continue;
            }
            if (token.type() == TokenType.TAG_OPEN) {
                level++;
            } else if (--level == 0) {
                break;
            }
            subTokens.add(token);
        }
        TextComponent parsed = detachFromRoot(new FormatifyParser(formatify, subTokens).parse());
        tag.apply(parsed);
        return parsed;
    }

    private Tag parseTag(String[] tagArray) {
        String tagName = tagArray[0];
        ArgumentQueue arguments = new ArgumentQueue(formatify);
        for (int i = 1; i < tagArray.length; i++)
            arguments.offer(tagArray[i]);
        try {
            Tag tag = formatify.tagResolver().resolve(tagName, arguments)
                .orElseThrow(() -> new ParseException("Tag '" + tagName + "' doesn't exist"));
            if (!arguments.isEmpty()) throw new ParseException("Too many arguments");
            return tag;
        } catch (ParseException parseException) {
            ParseException exception = parseException.withMessage(
                    "Error parsing tag '<%s>': %s",
                    String.join(String.valueOf(ARGUMENT_SEPARATOR), tagArray),
                    parseException.getMessage()
            );
            if (formatify.isStrict()) throw new FormatifyException(exception);
            formatify.errorHandler().accept(exception);
            return null;
        }
    }

    private static TextComponent detachFromRoot(TextComponent root) {
        List<Component> components = new ArrayList<>(root.getSiblings());
        TextComponent detached = (TextComponent) components.removeFirst();
        components.forEach(detached::append);
        return detached;
    }
    
    private static String[] split(String string, char splitChar) {
        List<String> strings = new ArrayList<>();

        int level = 0;
        StringBuilder builder = new StringBuilder();
        for (int currentIndex = 0, length = string.length(); currentIndex < length; currentIndex++) {
            char c = string.charAt(currentIndex);
            if (c == '\\') {
                builder.append(string.charAt(++currentIndex));
                continue;
            } else if (c == '<') {
                level++;
            } else if (c == '>' && level > 0) {
                level--;
            } else if (c == splitChar && level == 0) {
                strings.add(builder.toString());
                builder = new StringBuilder();
                continue;
            }
            builder.append(c);
        }

        strings.add(builder.toString());

        return strings.toArray(new String[0]);
    }

}

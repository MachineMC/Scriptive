package org.machinemc.scriptive.formatify.parser;

import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.Formatify;
import org.machinemc.scriptive.formatify.exceptions.FormatifyException;
import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.formatify.parameter.ArgumentQueue;
import org.machinemc.scriptive.formatify.lexer.LexicalAnalyzer;
import org.machinemc.scriptive.formatify.lexer.token.Token;
import org.machinemc.scriptive.formatify.parser.node.Node;
import org.machinemc.scriptive.formatify.parser.node.RootNode;
import org.machinemc.scriptive.formatify.parser.node.TagNode;
import org.machinemc.scriptive.formatify.parser.node.TextNode;
import org.machinemc.scriptive.formatify.tag.Tag;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class FormatifyParser {

    private static final char ARGUMENT_SEPARATOR = ':';
    
    private final Formatify formatify;
    private final List<Token> tokens;

    public FormatifyParser(Formatify formatify, String string) {
        this.formatify = formatify;
        this.tokens = new LexicalAnalyzer(string).tokenize();
    }

    public TextComponent parse() {
        return buildTree().evaluate();
    }

    private RootNode buildTree() {
        Deque<TagNode> openTags = new LinkedList<>();
        RootNode rootNode = new RootNode();
        Node currentNode = rootNode;
        for (Token token : tokens) {
            switch (token.type()) {
                case TEXT_LITERAL -> currentNode = new TextNode(currentNode, token.data());
                case TAG_OPEN -> {
                    String[] tagArray = split(token.data(), ARGUMENT_SEPARATOR);
                    if (tagArray.length == 1 && tagArray[0].equals("reset")) {
                        openTags.clear();
                        currentNode = rootNode;
                        continue;
                    }
                    Tag tag = parseTag(tagArray);
                    if (tag == null) {
                        currentNode = new TextNode(currentNode, token.raw());
                        continue;
                    }
                    currentNode = new TagNode(currentNode, tagArray[0], tag);
                    openTags.push((TagNode) currentNode);
                }
                case TAG_CLOSE -> {
                    String tagName = token.data();
                    int closeTags = 0;
                    boolean found = false;
                    for (TagNode tagNode : openTags) {
                        closeTags++;
                        if (tagName.equals(tagNode.name())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        currentNode = new TextNode(currentNode, token.raw());
                        break;
                    }
                    for (int i = 0; i < closeTags; i++)
                        openTags.pop();
                    currentNode = openTags.peek();
                    if (currentNode == null) currentNode = rootNode;
                }
            }
        }
        return rootNode;
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
            if (formatify.isStrict()) throw new FormatifyException(parseException);
            formatify.errorHandler().accept(parseException);
            return null;
        }
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

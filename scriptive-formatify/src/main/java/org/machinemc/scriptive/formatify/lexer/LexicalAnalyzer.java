package org.machinemc.scriptive.formatify.lexer;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.formatify.lexer.token.Token;
import org.machinemc.scriptive.formatify.lexer.token.TokenType;

import java.util.LinkedList;
import java.util.List;

public class LexicalAnalyzer {

    private final String string;
    private int cursor = 0;

    public LexicalAnalyzer(String string) {
        this.string = string;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new LinkedList<>();
        Token token = nextToken();
        while (token != null) {
            tokens.add(token);
            token = nextToken();
        }
        return tokens;
    }

    private @Nullable Token nextToken() {
        if (!canRead()) return null;
        int start = cursor;
        if (peek() == '<') {
            Token token = parseTag();
            if (token != null) return token;
            cursor = start + 1;
        }
        while (canRead() && peek() != '<')
            cursor++;
        return new Token(TokenType.TEXT_LITERAL, string.substring(start, cursor));
    }

    private Token parseTag() {
        if (!canRead(2)) return null;
        int start = cursor;
        int level = 0;
        TokenType type = peek(1) == '/' ? TokenType.TAG_CLOSE : TokenType.TAG_OPEN;
        while (canRead()) {
            char c = read();
            if (c == '<') level++;
            else if (c == '>' && --level == 0) {
                return new Token(
                    type,
                    string.substring(start + (type == TokenType.TAG_CLOSE ? 2 : 1), cursor - 1),
                    string.substring(start, cursor)
                );
            }
        }
        return null;
    }

    private char peek() {
        return peek(0);
    }

    private char peek(int offset) {
        return string.charAt(cursor + offset);
    }

    private char read() {
        return string.charAt(cursor++);
    }

    private boolean canRead() {
        return canRead(1);
    }

    private boolean canRead(int length) {
        return cursor + length - 1 < string.length();
    }

}

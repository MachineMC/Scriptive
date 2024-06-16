package org.machinemc.scriptive.formatify.lexer.token;

public record Token(TokenType type, String data, String raw) {

    public Token(TokenType type, String data) {
        this(type, data, data);
    }

}

package org.machinemc.scriptive.formatify.lexer.token;

public record Token(TokenType type, String value, int start, int end) {

    public boolean is(TokenType type, TokenType... types) {
        if (this.type == type)
            return true;
        for (TokenType otherType : types) {
            if (this.type == otherType)
                return true;
        }
        return false;
    }

    public String substring(String string) {
        return string.substring(start, end);
    }

}

package org.machinemc.scriptive.formatify.lexer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.formatify.lexer.token.Token;
import org.machinemc.scriptive.formatify.lexer.token.TokenType;
import org.machinemc.scriptive.util.StringReader;

import java.util.LinkedList;

import static org.machinemc.scriptive.formatify.FormatifyParser.*;

public class LexicalAnalyzer {

    private final StringReader reader;

    public LexicalAnalyzer(@NotNull String data) {
        this.reader = new StringReader(data) ;
    }

    public LinkedList<Token> lex() {
        LinkedList<Token> tokenList = new LinkedList<>();
        Token token;
        while (reader.canRead()) {
            token = nextToken();
            tokenList.add(token);
        }
        return tokenList;
    }

    private Token nextToken() {
        int start = reader.getCursor();
        char c = reader.peek();
        if (c == TAG_START) {
            Token token = handleTag();
            if (token != null)
                return token;
            reader.setCursor(start + 1);
        }
        reader.readUntil(TAG_START);
        return new Token(
                TokenType.TEXT,
                reader.getInput().substring(start, reader.getCursor()),
                start, reader.getCursor()
        );
    }

    private @Nullable Token handleTag() {
        if (!reader.canRead(2))
            return null;

        int level = 0;
        int startIndex = reader.getCursor();
        TokenType type = reader.peek(1) == TAG_CLOSE ? TokenType.TAG_CLOSE : TokenType.TAG_OPEN;
        do {
            char c;
            if ((c = reader.read()) == TAG_START) {
                level++;
            } else if (c == TAG_END) {
                level--;
            }
        } while (level != 0 && reader.canRead());
        if (level != 0)
            return null;
        return new Token(
                type,
                reader.getInput().substring(startIndex + (type == TokenType.TAG_CLOSE ? 2 : 1), reader.getCursor() - 1),
                startIndex,
                reader.getCursor()
        );
    }

}

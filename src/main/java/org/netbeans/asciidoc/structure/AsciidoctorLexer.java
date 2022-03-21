package org.netbeans.asciidoc.structure;

import org.netbeans.api.lexer.*;
import org.netbeans.spi.lexer.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AsciidoctorLexer implements Lexer<AsciidoctorTokenId> {
  private static final Logger LOGGER = Logger.getLogger(AsciidoctorLexer.class.getName());

  private final LexerRestartInfo<AsciidoctorTokenId> info;
  private final AsciidoctorTokenizer tokenizer;
  private Iterator<AsciidoctorToken> tokensItr;

  private AsciidoctorLexer(LexerRestartInfo<AsciidoctorTokenId> info) {
    this.info = Objects.requireNonNull(info, "info");
    this.tokenizer = new AsciidoctorTokenizer();
    this.tokensItr = null;
  }

  public static Lexer<AsciidoctorTokenId> create(LexerRestartInfo<AsciidoctorTokenId> info) {
    return new AsciidoctorLexer(info);
  }

  @Override
  public Token<AsciidoctorTokenId> nextToken() {
    if (tokensItr == null) {
      tokensItr = readTokens().iterator();
    }

    if (!tokensItr.hasNext()) {
      return null;
    }

    AsciidoctorToken token = tokensItr.next();
    // BEGIN ADITO
    try
    {
      return info.tokenFactory().createToken(token.getId(), token.getLength());
    }
    catch (Exception originalEx)
    {
      try
      {
        // try it with no length
        return info.tokenFactory().createToken(token.getId());
      }
      catch (Exception innerEx)
      {
        //throw the original exception
        throw originalEx;
      }
    }
    // END ADITO
  }

  @Override
  public Object state() {
    return null;
  }

  @Override
  public void release() {
  }

  private Collection<AsciidoctorToken> readTokens() {
    LexerInput input = info.input();
    try {
      return tokenizer.readTokens(input::read);
    } catch (Exception ex) {
      LOGGER.log(Level.INFO, "Internal error: Tokenizer failed to read tokens.", ex);

      int count = input.readLength();
      while (input.read() != LexerInput.EOF) {
        count++;
      }

      return Collections.singletonList(new AsciidoctorToken(AsciidoctorTokenId.PLAIN, 0, count));
    }
  }
}

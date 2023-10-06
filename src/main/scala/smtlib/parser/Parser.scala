package smtlib
package parser

import lexer.Tokens
import Tokens.{Token, TokenKind}
import lexer.Lexer

class Parser(val lexer: Lexer) extends ParserCommon with ParserTerms with ParserCommands with ParserCommandsResponses

object Parser {

  class UnknownCommandException(val commandName: TokenKind) extends Exception("Unknown command name token: " + commandName)

  class UnexpectedTokenException(found: Token, expected: Seq[TokenKind])
    extends Exception("Unexpected token at position: " + found.getPos + ". Expected: " + expected.mkString("[",",","]") + ". Found: " + found)

  class UnexpectedEOFException(expected: Seq[TokenKind])
    extends Exception("Unexpected end of file. Expected: " + expected.mkString("[",",","]"))

  class UnsupportedException(msg: String)
    extends Exception(msg)

  class IllformedCommandException(val command: Token, extra: String)
    extends Exception(s"Ill-formed command ${command.toString} @ ${command.getPos}: $extra")

  def fromString(str: String): Parser = {
    val lexer = new Lexer(new java.io.StringReader(str))
    new Parser(lexer)
  }

}

package smtlib
package interpreters

import smtlib.lexer.Lexer
import smtlib.parser.Parser
import smtlib.printer.{Printer, RecursivePrinter}
import trees.Commands._

import java.io.BufferedReader

class BitwuzlaInterpreter(executable: String,
                      args: Array[String],
                      printer: Printer = RecursivePrinter,
                      parserCtor: BufferedReader => Parser = out => new Parser(new Lexer(out)))
  extends ProcessInterpreter(executable, args, printer, parserCtor) {

  printer.printCommand(SetOption(PrintSuccess(true)), in)
  in.write("\n")
  in.flush()
  parser.parseGenResponse

}

object BitwuzlaInterpreter {

  def buildDefault: BitwuzlaInterpreter = {
    val executable = "bitwuzla"
    val args = Array("-v", "0",
                     "--produce-models",
                     "--lang", "smt2")
    new BitwuzlaInterpreter(executable, args)
  }

}

package smtlib
package interpreters

import smtlib.lexer.Lexer
import smtlib.parser.Parser
import smtlib.printer.{Printer, RecursivePrinter}
import trees.Commands._

import java.io.BufferedReader

class CVC5Interpreter(executable: String,
                      args: Array[String],
                      printer: Printer = RecursivePrinter,
                      parserCtor: BufferedReader => Parser = out => new Parser(new Lexer(out)))
  extends ProcessInterpreter(executable, args, printer, parserCtor) {

  printer.printCommand(SetOption(PrintSuccess(true)), in)
  in.write("\n")
  in.flush()
  parser.parseGenResponse

}

object CVC5Interpreter {

  def buildDefault: CVC5Interpreter = {
    val executable = "cvc5"
    val args = Array("-q",
                     "-i",
                     "--produce-models",
                     "--print-success",
                     "--lang", "smt2.6")
    new CVC5Interpreter(executable, args)
  }

}

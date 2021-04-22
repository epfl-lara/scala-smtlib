package smtlib
package interpreters

import trees.Terms._
import trees.Commands._
import trees.CommandsResponses._

class CVC5Interpreter(executable: String, args: Array[String], tailPrinter: Boolean = false)
  extends ProcessInterpreter(executable, args, tailPrinter) {

  printer.printCommand(SetOption(PrintSuccess(true)), in)
  in.write("\n")
  in.flush
  parser.parseGenResponse

}

object CVC5Interpreter {

  def buildDefault: CVC4Interpreter = {
    val executable = "cvc5"
    val args = Array("-q",
                     "-i",
                     "--produce-models",
                     "--dt-rewrite-error-sel",
                     "--print-success",
                     "--lang", "smt2.6")
    new CVC4Interpreter(executable, args)
  }

}

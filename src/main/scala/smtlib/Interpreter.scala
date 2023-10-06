package smtlib

import parser.Parser
import trees.Commands.{Script, Command}
import trees.CommandsResponses.CommandResponse
import trees.Terms._
import printer.Printer

/*
 * An interpreter is a stateful object that can eval Commands and returns
 * CommandResponse.
 *
 * Note that despite returning the command response, the interpreter should handle the printing
 * of those responses itself. That is because it needs to handle the verbosity and *-output-channel
 * options commands, and will do the correct printing depending on the internal state.
 * The responses are returned as a way to progamatically communicate with a solver.

 * TODO: The interaction of the set-option for the channels with the eval interface
         seems just wrong. Need to clarify that.
 */
trait Interpreter {

  val printer: Printer
  val parser: Parser

  def eval(cmd: SExpr): SExpr

  //A free method is kind of justified by the need for the IO streams to be closed, and
  //there seems to be a decent case in general to have such a method for things like solvers
  //note that free can be used even if the solver is currently solving, and act as a sort of interrupt
  def free(): Unit

  def interrupt(): Unit
}

object Interpreter {

  import java.io.Reader
  import java.io.FileReader
  import java.io.BufferedReader
  import java.io.File

  def execute(script: Script)(using interpreter: Interpreter): Unit = {
    for(cmd <- script.commands)
      interpreter.eval(cmd)
  }

  def execute(scriptReader: Reader)(using Interpreter): Unit =
    execute(new Parser(new lexer.Lexer(scriptReader)))

  def execute(file: File)(using Interpreter): Unit =
    execute(new Parser(new lexer.Lexer(new BufferedReader(new FileReader(file)))))

  private def execute(parser: Parser)(using interpreter: Interpreter): Unit = {
    // do-while, Scala 3 style
    // See https://docs.scala-lang.org/scala3/reference/dropped-features/do-while.html
    while ({
      val cmd = parser.parseCommand
      if(cmd != null)
        interpreter.eval(cmd)
      cmd != null
    }) () // This trailing () is important! If we remove it, it messes with the next statement
  }
}

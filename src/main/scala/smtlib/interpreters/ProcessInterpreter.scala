package smtlib
package interpreters

import lexer.Lexer
import parser.Parser
import trees.Terms._
import trees.Commands._
import trees.CommandsResponses._
import printer._

import java.io._

abstract class ProcessInterpreter private[smtlib](override val printer: Printer,
                                                  override val parser: Parser,
                                                  protected val process: Process,
                                                  protected val in: BufferedWriter,
                                                  protected val out: BufferedReader,
                                                  protected val err: BufferedReader) extends Interpreter {

  private def this(printer: Printer, otherArgs: (Parser, Process, BufferedWriter, BufferedReader, BufferedReader)) =
    this(printer, otherArgs._1, otherArgs._2, otherArgs._3, otherArgs._4, otherArgs._5)

  def this(executable: String,
           args: Array[String],
           printer: Printer = RecursivePrinter,
           parserCtor: BufferedReader => Parser = out => new Parser(new Lexer(out))) =
    this(printer, ProcessInterpreter.ctorHelper(executable, args, parserCtor))

  def parseResponseOf(cmd: SExpr): SExpr = cmd match {
    case CheckSat() => parser.parseCheckSatResponse
    case GetAssertions() => parser.parseGetAssertionsResponse
    case GetUnsatCore() => parser.parseGetUnsatCoreResponse
    case GetUnsatAssumptions() => parser.parseGetUnsatAssumptionsResponse
    case GetProof() => parser.parseGetProofResponse
    case GetValue(_, _) => parser.parseGetValueResponse
    case GetAssignment() => parser.parseGetAssignmentResponse

    case GetOption(_) => parser.parseGetOptionResponse
    case GetInfo(_) => parser.parseGetInfoResponse

    case GetModel() => parser.parseGetModelResponse

    case (_: Command) => parser.parseGenResponse

    //in the case the input was not a known command, we assume nothing and
    //parse an arbitrary s-expr
    case _ => parser.parseSExpr
  }

  /*
   * eval is blocking, and not synchronized. You
   * should not invoke eval from different threads.
   */
  override def eval(cmd: SExpr): SExpr = {
    def flushErr: String = {
      val sb = new StringBuilder
      var ok = err.ready()
      while (ok) {
        val line = err.readLine()
        if (line != null) {
          if (sb.nonEmpty) {
            sb += '\n'
          }
          sb ++= line
          ok = err.ready()
        } else {
          ok = false
        }
      }
      sb.toString()
    }

    try {
      printer.printSExpr(cmd, in)
      in.write("\n")
      in.flush()

      val res = parseResponseOf(cmd)
      if (err.ready()) {
        Error("Solver encountered some error: " + flushErr)
      } else {
        res
      }
    } catch {
      case ex: Exception =>
        if (cmd == CheckSat()) CheckSatStatus(UnknownStatus)
        else Error("Solver encountered exception: " + ex)
    }
  }

  private var isKilled = false

  override def free(): Unit = synchronized {
    if(!isKilled) {
      try {
        printer.printCommand(Exit(), in)
        in.write("\n")
        in.flush

        process.destroyForcibly()
        in.close()
      } catch {
        case (io: java.io.IOException) => ()
      } finally {
        isKilled = true
        try { in.close() } catch { case (io: java.io.IOException) => () }
      }
    }
  }

  def kill(): Unit = synchronized {
    if(!isKilled) {
      try {
        process.destroyForcibly()
        in.close()
      } catch {
        case (io: java.io.IOException) => ()
      } finally {
        isKilled = true
      }
    }
  }

  override def interrupt(): Unit = synchronized {
    kill()
  }
}

object ProcessInterpreter {
  private def ctorHelper(executable: String,
                         args: Array[String],
                         parserCtor: BufferedReader => Parser): (Parser, Process, BufferedWriter, BufferedReader, BufferedReader) = {
    val process = java.lang.Runtime.getRuntime.exec(executable +: args)
    val in = new BufferedWriter(new OutputStreamWriter(process.getOutputStream))
    val out = new BufferedReader(new InputStreamReader(process.getInputStream))
    val err = new BufferedReader(new InputStreamReader(process.getErrorStream))
    val parser = parserCtor(out)
    (parser, process, in, out, err)
  }
}
package smtlib
package it

import scala.sys.process._
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedReader, BufferedWriter, StringReader, StringWriter}
import parser.Parser
import lexer.Lexer
import printer.RecursivePrinter
import interpreters._
import smtlib.trees.Commands._
import smtlib.trees.CommandsResponses.{Error => Err}


class ProcessInterpreterTests extends AnyFunSuite with TestHelpers {

  //TODO: properly get all interpreters
  def interpreters: Seq[Interpreter] = Seq(getZ3Interpreter)

  test("Interpreter can be created with complex executable names") {
    // sometimes we are given arguments in the executable name itself;
    // check that these are split and processed correctly
    val z3Interpreter = new Z3Interpreter("z3 -in", Array("-smt2")) {}
    z3Interpreter.eval(trees.Commands.SetLogic(trees.Commands.AUFLIA()))
    z3Interpreter.eval(trees.Commands.CheckSat())
    z3Interpreter.interrupt()
  }

  test("Interrupt after free does not throw an exception") {
    //TODO: check against all interpreters
    val z3Interpreter = getZ3Interpreter

    z3Interpreter.eval(trees.Commands.SetLogic(trees.Commands.AUFLIA()))
    z3Interpreter.eval(trees.Commands.CheckSat())
    z3Interpreter.free()
    z3Interpreter.interrupt()
  }

  test("Interrupt can be called multiple times safely") {
    //TODO: check against all interpreters
    val z3Interpreter = getZ3Interpreter

    z3Interpreter.eval(trees.Commands.SetLogic(trees.Commands.AUFLIA()))
    z3Interpreter.eval(trees.Commands.CheckSat())
    z3Interpreter.interrupt()
    z3Interpreter.interrupt()
    z3Interpreter.interrupt()
    z3Interpreter.interrupt()
    z3Interpreter.interrupt()
  }

  test("ProcessInterpreter reads stderr") {
    val outContent = "success"
    val out = new BufferedReader(new StringReader(outContent))
    val errContent = "Oh no, I stumbled upon a pebble :("
    val err = new BufferedReader(new StringReader(errContent))
    val in = new BufferedWriter(new StringWriter)
    class DummyInterpreter extends ProcessInterpreter(RecursivePrinter, new Parser(new Lexer(out)), null, in, out, err)

    val dummyInterpreter = new DummyInterpreter
    val got = dummyInterpreter.eval(SetOption(PrintSuccess(true)))
    assert(got === Err(s"Solver encountered some error: $errContent"))
  }

  //TODO: test interrupt on a long running check-sat command with big benchmarks

}

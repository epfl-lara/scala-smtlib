package smtlib
package it

import scala.sys.process._

import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.io.FileReader

import parser.Parser
import lexer.Lexer
import printer.RecursivePrinter
import interpreters._


/** Checks the parser on .smt2 files.
  *
  * Compare the result of running command by command with an interpreter against
  * running the corresponding executable directly on the .smt2 files.
  */
class SmtLibRunnerTests extends AnyFunSuite with TestHelpers {

  filesInResourceDir("regression/smtlib/solving/all", _.endsWith(".smt2")).foreach(file => {
    val z3Test = s"With Z3: SMTLIB benchmark ${file.getPath}"
    if(isZ3Available) {
      test(z3Test) {
        compareWithInterpreter(executeZ3)(getZ3Interpreter, file)
      }
    } else {
      ignore(z3Test) {}
    }

    val cvc4Test = s"With CVC4: SMTLIB benchmark ${file.getPath}"
    if(isCVC4Available) {
      test(cvc4Test) {
        compareWithInterpreter(executeCVC4)(getCVC4Interpreter, file)
      }
    } else {
      ignore(cvc4Test) {}
    }

    val cvc5Test = s"With cvc5: SMTLIB benchmark ${file.getPath}"
    if (isCVC5Available) {
      test(cvc5Test) {
        compareWithInterpreter(executeCVC5)(getCVC5Interpreter, file)
      }
    } else {
      ignore(cvc5Test) {}
    }
  })

  // Z3-specific
  filesInResourceDir("regression/smtlib/solving/z3", _.endsWith(".smt2")).foreach(file => {
    val z3Test = "With Z3: SMTLIB benchmark: " + file.getPath
    if (isZ3Available) {
      test(z3Test) {
        compareWithInterpreter(executeZ3)(getZ3Interpreter, file)
      }
    } else {
      ignore(z3Test) {}
    }
  })

  // CVC-specifc
  filesInResourceDir("regression/smtlib/solving/cvc4", _.endsWith(".smt2")).foreach(file => {
    val cvc4Test = "With CVC4: SMTLIB benchmark: " + file.getPath
    if (isCVC4Available) {
      test(cvc4Test) {
        compareWithWant(getCVC4Interpreter, file, new File(file.getPath + ".want"))
      }
    } else {
      ignore(cvc4Test) {}
    }

    val cvc5Test = "With cvc5: SMTLIB benchmark: " + file.getPath
    if (isCVC5Available) {
      test(cvc5Test) {
        compareWithWant(getCVC5Interpreter, file, new File(file.getPath + ".want"))
      }
    } else {
      ignore(cvc5Test) {}
    }
  })

  def compareWithInterpreter(executable: (File) => (String => Unit) => Unit)
                            (interpreter: Interpreter, file: File) = {
    val lexer = new Lexer(new FileReader(file))
    val parser = new Parser(lexer)

    executable(file) { (expected: String) =>
      val cmd = parser.parseCommand
      assert(cmd !== null)
      val res: String = interpreter.eval(cmd).toString
      assert(expected.trim === res.trim)
    }
    assert(parser.parseCommand === null)
  }

  def compareWithWant(interpreter: Interpreter, file: File, want: File) = {

    val lexer = new Lexer(new FileReader(file))
    val parser = new Parser(lexer)

    for(expected <- scala.io.Source.fromFile(want).getLines()) {
      val cmd = parser.parseCommand
      assert(cmd !== null)
      val res: String = interpreter.eval(cmd).toString
      assert(expected.trim === res.replace('\n', ' ').trim)
    }
    assert(parser.parseCommand === null)
  }
}

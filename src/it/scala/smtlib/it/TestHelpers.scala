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

/** Provide helper functions to test solver interfaces and files.
  *
  * Provides functions to access a list of files in resources.
  * Provides functions to get access to an interpreter to
  * an SMT solver running locally. Assume standard names of executable
  * are available in current working directory.
  */
trait TestHelpers {

  private val all: String => Boolean = (s: String) => true
  private val resourceDirHard = "src/it/resources/"

  def filesInResourceDir(dir : String, filter : String=>Boolean = all) : Iterable[File] = {
    val d = this.getClass.getClassLoader.getResource(dir)

    val asFile = if(d == null || d.getProtocol != "file") {
      // We are in Eclipse. The only way we are saved is by hard-coding the path
      new File(resourceDirHard + dir)
    } else new File(d.toURI())

    val files = asFile.listFiles()
    if(files == null)
      Nil
    else
      files.filter(f => filter(f.getPath()))
  }

  def getZ3Interpreter: Interpreter = Z3Interpreter.buildDefault
  def getCVC4Interpreter: Interpreter = CVC4Interpreter.buildDefault
  def getCVC5Interpreter: Interpreter = CVC5Interpreter.buildDefault

  def isZ3Available: Boolean = try {
    val _: String = "z3 -help".!!
    true
  } catch {
    case _: Exception => false
  }

  def isCVC4Available: Boolean = try {
    // Make sure to pass a dummy option to cvc4, otherwise it starts in interactive mode (and does not exit)
    val _: String = "cvc4 --version".!!
    true
  } catch {
    case _: Exception => false
  }

  def isCVC5Available: Boolean = try {
    // Ditto for cvc5
    val _: String = "cvc5 --version".!!
    true
  } catch {
    case _: Exception => false
  }

  def executeZ3(file: File)(f: (String) => Unit): Unit = {
    Seq("z3", "-smt2", file.getPath) ! ProcessLogger(f, s => ())
  }

  def executeCVC4(file: File)(f: (String) => Unit): Unit = {
    Seq("cvc4", "--lang", "smt", file.getPath) ! ProcessLogger(f, s => ())
  }

  def executeCVC5(file: File)(f: (String) => Unit): Unit = {
    Seq("cvc5", "--lang", "smt", file.getPath) ! ProcessLogger(f, s => ())
  }

}


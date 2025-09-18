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
import smtlib.trees.Commands.*
import smtlib.trees.Terms.Term
import smtlib.trees.Tree
import smtlib.trees.TreesOps
import smtlib.theories.FixedSizeBitVectors.BitVectorSort
import smtlib.theories.FloatingPoint.FloatingPointSort

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
  def getBitwuzlaInterpreter: Interpreter = BitwuzlaInterpreter.buildDefault

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

  def isBitwuzlaAvailable: Boolean = try {
    val _: String = "bitwuzla --version".!!
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

  def executeBitwuzla(file: File)(f: (String) => Unit): Unit = {
    Seq("bitwuzla", "-v", "0", "--lang", "--smt2", file.getPath) ! ProcessLogger(f, s => ())
  }

  ////////////////////////////////////////////////////////////////
  // Solver specific helpers

  /**
  * Is test in the fragment supported by bitwuzla?
  * 
  * May not contain ints, reals, arrays, ADTs
  *
  */
  def testIsBitwuzlaCompatible(cmds: Seq[Command], formula: Term): Boolean =
    def isCompat(term: Term): Boolean =
      def violatingTerm(term: Tree, children: Seq[Boolean]): Boolean = {
        import smtlib.theories.*
        term match
          case FixedSizeBitVectors.BitVectorConstant(_, _) => false // internally contains IntNumeral, but in a valid place
          case Ints.NumeralLit(_) => true
          case Reals.DecimalLit(_) => true
          case FixedSizeBitVectors.Int2BV(_, _) => true
          case FixedSizeBitVectors.BV2Nat(_) => true
          // this is enough to rule out the current problematic tests, 
          // but may need refinement
          case _ => children.contains(true)
      }
      !TreesOps.fold(violatingTerm)(term)
    
    def compatibleCommand(cmd: Command): Boolean =
      cmd match
        case DeclareFun(_, paramSorts, sort) => (sort +: paramSorts).forall {
            case BitVectorSort(_) => true
            case FloatingPointSort(_, _) => true
            case _ => false
        }
        case DeclareSort(_, _) => false
        case DeclareDatatypes(_) => false
        case Assert(term) => isCompat(term)
        case _ => true // may need refinement
    cmds.forall(compatibleCommand) && (isCompat(formula))

}


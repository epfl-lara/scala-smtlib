package smtlib
package printer

import lexer.Lexer
import parser.Terms._
import parser.Commands._
import parser.Parser

import common._

import java.io.StringReader

import org.scalatest.FunSuite

import scala.language.implicitConversions

class PrinterTests extends FunSuite {

  override def suiteName = "Printer suite"

  private implicit def strToSym(str: String): SSymbol = SSymbol(str)
  private implicit def strToId(str: String): Identifier = Identifier(SSymbol(str))
  private implicit def strToKeyword(str: String): SKeyword = SKeyword(str)
  private implicit def symToTerm(sym: SSymbol): QualifiedIdentifier = QualifiedIdentifier(sym.name)


  private def checkTerm(term: Term): Unit = {

    val directPrint: String = PrettyPrinter.toString(term)

    val parser = Parser.fromString(directPrint)
    val parsedAgain: Term = parser.parseTerm
    val printAgain: String = PrettyPrinter.toString(parsedAgain)

    assert(directPrint === printAgain)
    assert(term === parsedAgain)
  }

  test("Printing attributes") {
    def parseAttribute(str: String): Attribute = {
      val reader = new StringReader(str)
      val lexer = new Lexer(reader)
      val parser = new Parser(lexer)
      val attr = parser.parseAttribute
      attr
    }

    assert(parseAttribute(":test") === Attribute(SKeyword("test")))
    assert(parseAttribute(":key") === Attribute(SKeyword("key")))
    assert(parseAttribute(":abcd") === Attribute(SKeyword("abcd")))
    assert(parseAttribute(":test alpha") === Attribute(SKeyword("test"), Some(SSymbol("alpha"))))
    assert(parseAttribute(":test 42") === Attribute(SKeyword("test"), Some(SNumeral(42))))
    assert(parseAttribute(""":test "hello" """) === Attribute(SKeyword("test"), Some(SString("hello"))))
    assert(parseAttribute(""":test 23.12 """) === Attribute(SKeyword("test"), Some(SDecimal(23.12))))
    assert(parseAttribute(""":test (abc def) """) === 
                          Attribute(SKeyword("test"), 
                                    Some(SList(
                                          List(SSymbol("abc"), SSymbol("def"))))
                                   ))
  }

  test("Printing Sorts") {
    def parseSort(str: String): Sort = {
      val reader = new StringReader(str)
      val lexer = new Lexer(reader)
      val parser = new Parser(lexer)
      val sort = parser.parseSort
      sort
    }

    assert(parseSort("A") === Sort("A"))
    assert(parseSort("(A B)") === Sort("A", Seq(Sort("B"))))
    assert(parseSort("(Array From To)") === Sort("Array", Seq(Sort("From"), Sort("To"))))

  }

  test("Printing Identifiers") {
    def parseId(str: String): Identifier = {
      val reader = new StringReader(str)
      val lexer = new Lexer(reader)
      val parser = new Parser(lexer)
      val id = parser.parseIdentifier
      id
    }

    assert(parseId("abc") === Identifier("abc"))
    assert(parseId("test") === Identifier("test"))
    assert(parseId("(_ a 1)") === Identifier("a", Seq(1)))
    assert(parseId("(_ a 42 12)") === Identifier("a", Seq(42, 12)))

  }

  test("Printing simple Terms") {

    checkTerm(SNumeral(42))
    checkTerm(QualifiedIdentifier("abc"))
    checkTerm(FunctionApplication(
            QualifiedIdentifier("f"), Seq(QualifiedIdentifier("a"), QualifiedIdentifier("b"))))
    checkTerm(Let(VarBinding("a", QualifiedIdentifier("x")), Seq(), QualifiedIdentifier("a")))

    checkTerm(ForAll(SortedVar("a", Sort("A")), Seq(), QualifiedIdentifier("a")))
    checkTerm(Exists(SortedVar("a", Sort("A")), Seq(), QualifiedIdentifier("a")))
    checkTerm(AnnotatedTerm(QualifiedIdentifier("a"), Attribute(SKeyword("note"), Some(SSymbol("abcd"))), Seq()))

  }

  test("Parsing composed Terms") {

  }

/*
  test("Parsing single commands") {

    assert(parseUniqueCmd("(set-logic QF_UF)") === SetLogic(QF_UF))

    assert(parseUniqueCmd("(declare-sort A 0)") === DeclareSort("A", 0))
    assert(parseUniqueCmd("(define-sort A (B C) (Array B C))") ===
                          DefineSort("A", Seq("B", "C"), 
                                            Sort(Identifier("Array"), Seq(Sort("B"), Sort("C")))
                                    ))
    assert(parseUniqueCmd("(declare-fun xyz (A B) C)") ===
           DeclareFun("xyz", Seq(Sort("A"), Sort("B")), Sort("C")))

    assert(parseUniqueCmd("(push 1)") === Push(1))
    assert(parseUniqueCmd("(push 4)") === Push(4))
    assert(parseUniqueCmd("(pop 1)") === Pop(1))
    assert(parseUniqueCmd("(pop 2)") === Pop(2))
    assert(parseUniqueCmd("(assert true)") === Assert(QualifiedIdentifier("true")))
    assert(parseUniqueCmd("(check-sat)") === CheckSat())

    assert(parseUniqueCmd("(get-assertions)") === GetAssertions())
    assert(parseUniqueCmd("(get-proof)") === GetProof())
    assert(parseUniqueCmd("(get-unsat-core)") === GetUnsatCore())
    assert(parseUniqueCmd("(get-value (x y z))") === GetValue(SSymbol("x"), Seq(SSymbol("y"), SSymbol("z"))))
    assert(parseUniqueCmd("(get-assignment)") === GetAssignment())

    assert(parseUniqueCmd("(get-option :keyword)") === GetOption("keyword"))
    assert(parseUniqueCmd("(get-info :authors)") === GetInfo(AuthorsInfoFlag))

    assert(parseUniqueCmd("(exit)") === Exit())
  }
  */

  /*
  test("Parsing set-option command") {
    assert(parseUniqueCmd("(set-option :print-success true)") === SetOption(PrintSuccess(true)))
    assert(parseUniqueCmd("(set-option :print-success false)") === SetOption(PrintSuccess(false)))
    assert(parseUniqueCmd("(set-option :expand-definitions true)") === SetOption(ExpandDefinitions(true)))
    assert(parseUniqueCmd("(set-option :expand-definitions false)") === SetOption(ExpandDefinitions(false)))
    assert(parseUniqueCmd("(set-option :interactive-mode true)") === SetOption(InteractiveMode(true)))
    assert(parseUniqueCmd("(set-option :interactive-mode false)") === SetOption(InteractiveMode(false)))
    assert(parseUniqueCmd("""(set-option :regular-output-channel "test")""") === 
                          SetOption(RegularOutputChannel("test")))
    assert(parseUniqueCmd("""(set-option :diagnostic-output-channel "toto")""") === 
                          SetOption(DiagnosticOutputChannel("toto")))
    assert(parseUniqueCmd("(set-option :random-seed 42)") === SetOption(RandomSeed(42)))
    assert(parseUniqueCmd("(set-option :verbosity 4)") === SetOption(Verbosity(4)))

  }
  */

  /*
  test("Parsing set-info command") {
    assert(parseUniqueCmd("""(set-info :author "Reg")""") === SetInfo(Attribute(SKeyword("author"), Some(SString("Reg")))))
    assert(parseUniqueCmd("""(set-info :number 42)""") === SetInfo(Attribute(SKeyword("number"), Some(SNumeral(42)))))
    assert(parseUniqueCmd("""(set-info :test)""") === SetInfo(Attribute(SKeyword("test"), None)))
  }

  test("Unknown command") {
    val reader1 = new StringReader("(alpha beta)")
    val lexer1 = new Lexer(reader1)
    val parser1 = new Parser(lexer1)
    intercept[UnknownCommandException] {
      parser1.parseCommand
    }
  }
  */

/*
  test("simple benchmark") {
    val reader1 = new StringReader("""
      (set-logic QF_UF)
      (declare-fun f (Int) Int)
      (declare-fun a () Int)
      (assert (= (f a) a))
      (check-sat)
    """)
    val lexer1 = new Lexer(reader1)
    val parser1 = new Parser(lexer1)
    assert(parser1.parseCommand === SetLogic(QF_UF))
    assert(parser1.parseCommand === DeclareFun("f", Seq(Sort("Int")), Sort("Int")))
    assert(parser1.parseCommand === DeclareFun("a", Seq(), Sort("Int")))
    assert(parser1.parseCommand === 
           Assert(FunctionApplication(
                    QualifiedIdentifier("="),
                    Seq(
                      FunctionApplication(
                        QualifiedIdentifier("f"),
                        Seq(QualifiedIdentifier("a"))
                      ),
                      QualifiedIdentifier("a")
                    )
                  ))
           )
    assert(parser1.parseCommand === CheckSat())

  }
  */

}
package smtlib
package it

import scala.sys.process._

import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import java.io.FileReader

import interpreters._

import trees.Terms._
import trees.Commands._
import trees.CommandsResponses._


/** Checks that formula build with theories module are correctly handled by solvers */
class TheoriesBuilderTests extends AnyFunSuite with TestHelpers {


  def mkTest(formula: Term, expectedStatus: Status, prefix: String) = {

    if(isZ3Available) {
      test(prefix + ": with Z3") {
        val z3Interpreter = getZ3Interpreter
        val assertion = Assert(formula)
        assert(z3Interpreter.eval(assertion) === Success)
        val res = z3Interpreter.eval(CheckSat())
        res match {
          case CheckSatStatus(status) => assert(status === expectedStatus)
          case res => assert(false, "expected a check sat status, but got: " + res)
        }
      }
    }

    if(isCVC4Available) {
      test(prefix + ": with CVC4") {
        val cvc4Interpreter = getCVC4Interpreter
        val assertion = Assert(formula)
        assert(cvc4Interpreter.eval(assertion) === Success)
        val res = cvc4Interpreter.eval(CheckSat())
        res match {
          case CheckSatStatus(status) => assert(status === expectedStatus)
          case res => assert(false, "expected a check sat status, but got: " + res)
        }
      }
    }

  }


  {
    import theories.Core.Equals
    import theories.Ints._
    val theoryString = "Theory of Ints"
    var counter = 0
    def uniqueName(): String = {
      counter += 1
      "%d - %s".format(counter, theoryString)
    }


    val f1 = GreaterEquals(NumeralLit(42), NumeralLit(12))
    mkTest(f1, SatStatus, uniqueName())

    val f2 = GreaterEquals(NumeralLit(42), NumeralLit(52))
    mkTest(f2, UnsatStatus, uniqueName())

    //divisible not supported by z3
    //val f3 = Divisible(2, NumeralLit(16))
    //mkTest(f3, SatStatus, uniqueName())

    val f4 = LessThan(NumeralLit(5), NumeralLit(10))
    mkTest(f4, SatStatus, uniqueName())

    val f5 = LessThan(Add(NumeralLit(6), NumeralLit(5)), NumeralLit(10))
    mkTest(f5, UnsatStatus, uniqueName())

    val f6 = LessThan(Neg(NumeralLit(5)), NumeralLit(2))
    mkTest(f6, SatStatus, uniqueName())

    val f7 = LessEquals(Sub(NumeralLit(5), NumeralLit(3)), NumeralLit(2))
    mkTest(f7, SatStatus, uniqueName())

    val f8 = LessEquals(Sub(NumeralLit(5), NumeralLit(3)), NumeralLit(1))
    mkTest(f8, UnsatStatus, uniqueName())

    val f9 = Equals(Add(NumeralLit(1), NumeralLit(2), NumeralLit(3), NumeralLit(4)), NumeralLit(10))
    mkTest(f9, SatStatus, uniqueName())

    val f10 = Equals(Sub(NumeralLit(1), NumeralLit(2), NumeralLit(3), NumeralLit(4)), NumeralLit(-8))
    mkTest(f10, SatStatus, uniqueName())

    val f11 = Equals(Mul(NumeralLit(1), NumeralLit(2), NumeralLit(3), NumeralLit(4)), NumeralLit(24))
    mkTest(f11, SatStatus, uniqueName())

    val f12 = Equals(Div(NumeralLit(80), NumeralLit(4), NumeralLit(2)), NumeralLit(10))
    mkTest(f12, SatStatus, uniqueName())
  }

  {
    import theories.Core.Equals
    import theories.Reals._
    val theoryString = "Theory of Reals"
    var counter = 0
    def uniqueName(): String = {
      counter += 1
      "%d - %s".format(counter, theoryString)
    }


    val f1 = GreaterEquals(NumeralLit(42), NumeralLit(12))
    mkTest(f1, SatStatus, uniqueName())

    val f2 = GreaterEquals(NumeralLit(42), NumeralLit(52))
    mkTest(f2, UnsatStatus, uniqueName())

    val f4 = LessThan(NumeralLit(5), NumeralLit(10))
    mkTest(f4, SatStatus, uniqueName())

    val f5 = LessThan(Add(NumeralLit(6), NumeralLit(5)), NumeralLit(10))
    mkTest(f5, UnsatStatus, uniqueName())

    val f6 = LessThan(Neg(NumeralLit(5)), NumeralLit(2))
    mkTest(f6, SatStatus, uniqueName())

    val f7 = LessEquals(Sub(NumeralLit(5), NumeralLit(3)), NumeralLit(2))
    mkTest(f7, SatStatus, uniqueName())

    val f8 = LessEquals(Sub(NumeralLit(5), NumeralLit(3)), NumeralLit(1))
    mkTest(f8, UnsatStatus, uniqueName())

    val f9 = Equals(Add(NumeralLit(1), NumeralLit(2), NumeralLit(3), NumeralLit(4)), NumeralLit(10))
    mkTest(f9, SatStatus, uniqueName())

    val f10 = Equals(Sub(NumeralLit(1), NumeralLit(2), NumeralLit(3), NumeralLit(4)), NumeralLit(-8))
    mkTest(f10, SatStatus, uniqueName())

    val f11 = Equals(Mul(NumeralLit(1), NumeralLit(2), NumeralLit(3), NumeralLit(4)), NumeralLit(24))
    mkTest(f11, SatStatus, uniqueName())

    val f12 = Equals(Div(NumeralLit(80), NumeralLit(4), NumeralLit(2)), NumeralLit(10))
    mkTest(f12, SatStatus, uniqueName())
  }

  {
    import theories.Core.Equals
    import theories.Ints.NumeralLit
    import theories.FixedSizeBitVectors._
    val theoryString = "Theory of Bit Vectors"
    var counter = 0
    def uniqueName(): String = {
      counter += 1
      "%d - %s".format(counter, theoryString)
    }

    val f1 = SGreaterEquals(BitVectorConstant(42, 32), BitVectorConstant(12, 32))
    mkTest(f1, SatStatus, uniqueName())

    val f2 = SGreaterEquals(BitVectorConstant(42, 32), BitVectorConstant(52, 32))
    mkTest(f2, UnsatStatus, uniqueName())

    val f3 = SGreaterEquals(BitVectorLit(List(false, true, false)), BitVectorLit(List(false, false, true)))
    mkTest(f3, SatStatus, uniqueName())

    val f4 = SLessThan(BitVectorConstant(5, 32), BitVectorConstant(10, 32))
    mkTest(f4, SatStatus, uniqueName())

    val f5 = SLessThan(Add(BitVectorConstant(6, 32), BitVectorConstant(5, 32)), BitVectorConstant(10, 32))
    mkTest(f5, UnsatStatus, uniqueName())

    val f6 = SLessThan(Neg(BitVectorConstant(5, 32)), BitVectorConstant(2, 32))
    mkTest(f6, SatStatus, uniqueName())

    val f7 = SLessEquals(Sub(BitVectorConstant(5, 32), BitVectorConstant(3, 32)), BitVectorConstant(2, 32))
    mkTest(f7, SatStatus, uniqueName())

    val f8 = SLessEquals(Sub(BitVectorConstant(5, 32), BitVectorConstant(3, 32)), BitVectorConstant(1, 32))
    mkTest(f8, UnsatStatus, uniqueName())

    val f9 = UGreaterThan(BitVectorLit(List(true, false)), BitVectorLit(List(false, true)))
    mkTest(f9, SatStatus, uniqueName())

    val f10 = UGreaterThan(BitVectorLit(List(true, false)), BitVectorLit(List(true, false)))
    mkTest(f10, UnsatStatus, uniqueName())

    val f11 = UGreaterEquals(BitVectorLit(List(true, false)), BitVectorLit(List(true, false)))
    mkTest(f11, SatStatus, uniqueName())

    val f12 = Equals(Int2BV(8, NumeralLit(42)), BitVectorConstant(42, 8))
    mkTest(f12, SatStatus, uniqueName())

    val f13 = Equals(BV2Nat(BitVectorConstant(42, 8)), NumeralLit(42))
    mkTest(f13, SatStatus, uniqueName())

    val f14 = Equals(Int2BV(8, NumeralLit(24)), BitVectorConstant(123, 8))
    mkTest(f14, UnsatStatus, uniqueName())

    val f15 = Equals(BV2Nat(BitVectorConstant(42, 8)), NumeralLit(123))
    mkTest(f15, UnsatStatus, uniqueName())

    // Testing that Int2BV wraps around for integer exceeding the BV size
    val f16 = Equals(Int2BV(8, NumeralLit(42 + 256*3)), BitVectorConstant(42, 8))
    mkTest(f16, SatStatus, uniqueName())

    // Int2BV does not care about the sign
    val f17 = Equals(Int2BV(8, NumeralLit(-214)), BitVectorConstant(42, 8))
    mkTest(f17, SatStatus, uniqueName())

    val f18 = Equals(Add(BitVectorConstant(1, 32), BitVectorConstant(2, 32), BitVectorConstant(3, 32), BitVectorConstant(4, 32)), BitVectorConstant(10, 32))
    mkTest(f18, SatStatus, uniqueName())

    val f20 = Equals(Mul(BitVectorConstant(1, 32), BitVectorConstant(2, 32), BitVectorConstant(3, 32), BitVectorConstant(4, 32)), BitVectorConstant(24, 32))
    mkTest(f20, SatStatus, uniqueName())
  }
}

package smtlib
package theories
package cvc

import Ints.{IntSort, NumeralLit}
import org.scalatest.funsuite.AnyFunSuite

trait SetsTests extends AnyFunSuite {

  override def suiteName = "Set theory test suite"

  val cvcSets: Sets

  import cvcSets._

  test("String sort correctly constructed and extracted") {
    SetSort(IntSort()) match {
      case SetSort(IntSort()) => assert(true)
      case _ => assert(false)
    }

    SetSort(IntSort()) match {
      case FixedSizeBitVectors.BitVectorSort(n) if n == 14 => assert(false)
      case FixedSizeBitVectors.BitVectorSort(n) if n == 32 => assert(false)
      case Ints.IntSort() => assert(false)
      case Reals.RealSort() => assert(false)
      case SetSort(IntSort()) => assert(true)
      case _ => assert(false)
    }
  }

  test("EmptySet is correctly constructed and extracted") {
    EmptySet(IntSort()) match {
      case EmptySet(IntSort()) => assert(true)
      case _ => assert(false)
    }

    EmptySet(IntSort()) match {
      case EmptySet(Reals.RealSort()) => assert(false)
      case NumeralLit(_) => assert(false)
      case EmptySet(IntSort()) => assert(true)
      case _ => assert(false)
    }
  }

  test("Singleton is correctly constructed and extracted") {
    Singleton(NumeralLit(42)) match {
      case Singleton(NumeralLit(n)) => assert(n === 42)
      case _ => assert(false)
    }

    Singleton(EmptySet(IntSort())) match {
      case EmptySet(IntSort()) => assert(false)
      case Singleton(NumeralLit(_)) => assert(false)
      case Singleton(EmptySet(IntSort())) => assert(true)
      case _ => assert(false)
    }
  }

  test("Union is correctly constructed and extracted") {
    Union(EmptySet(IntSort()), Singleton(NumeralLit(42))) match {
      case Union(EmptySet(IntSort()), Singleton(NumeralLit(n))) => assert(n === 42)
      case _ => assert(false)
    }
  }
}
class CVC4SetsTests extends SetsTests {
  override val cvcSets: Sets = CVC4Sets
}
class CVC5SetsTests extends SetsTests {
  override val cvcSets: Sets = CVC5Sets
}
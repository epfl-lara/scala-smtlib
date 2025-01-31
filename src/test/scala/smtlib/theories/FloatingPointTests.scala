package smtlib
package theories

import trees.Terms._

import FloatingPoint.{_, given}

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class FloatingPointTests extends AnyFunSuite with Matchers {

  test("RoundingModeSort is correctly constructed and extracted") {
    RoundingModeSort() match {
      case FixedSizeBitVectors.BitVectorSort(_) => assert(false)
      case RoundingModeSort()                   => assert(true)
      case _                                    => assert(false)
    }
  }

  test("FloatingPointSort is correctly constructed and extracted") {
    FloatingPointSort(8, 24) match {
      case FloatingPointSort(5, 11) => assert(false)
      case FloatingPointSort(8, 24) => assert(true)
      case _                        => assert(false)
    }
  }

  test("Float16 is correctly constructed and extracted") {
    Float16() match {
      case FloatingPointSort(8, 24) => assert(false)
      case Float16()                => assert(true)
      case _                        => assert(false)
    }

    Float16() match {
      case Float32()                => assert(false)
      case FloatingPointSort(5, 11) => assert(true)
      case _                        => assert(false)
    }
  }

  test("Float32 is correctly constructed and extracted") {
    Float32() match {
      case FloatingPointSort(5, 11) => assert(false)
      case Float32()                => assert(true)
      case _                        => assert(false)
    }

    Float32() match {
      case Float16()                => assert(false)
      case FloatingPointSort(8, 24) => assert(true)
      case _                        => assert(false)
    }
  }

  test("Float64 is correctly constructed and extracted") {
    Float64() match {
      case FloatingPointSort(8, 24) => assert(false)
      case Float64()                => assert(true)
      case _                        => assert(false)
    }

    Float64() match {
      case Float32()                 => assert(false)
      case FloatingPointSort(11, 53) => assert(true)
      case _                         => assert(false)
    }

  }

  test("Float128 is correctly constructed and extracted") {
    Float128() match {
      case FloatingPointSort(11, 53) => assert(false)
      case Float128()                => assert(true)
      case _                         => assert(false)
    }

    Float128() match {
      case Float64()                  => assert(false)
      case FloatingPointSort(15, 113) => assert(true)
      case _                          => assert(false)
    }
  }

  test("RoundNearestTiesToEven is correctly constructed and extracted") {
    RoundNearestTiesToEven() match {
      case RoundNearestTiesToAway() => assert(false)
      case RoundNearestTiesToEven() => assert(true)
      case _                        => assert(false)
    }

    RoundNearestTiesToEven() match {
      case RTP() => assert(false)
      case RNE() => assert(true)
      case _     => assert(false)
    }
  }

  test("RoundNearestTiesToAway is correctly constructed and extracted") {
    RoundNearestTiesToAway() match {
      case RoundNearestTiesToEven() => assert(false)
      case RoundNearestTiesToAway() => assert(true)
      case _                        => assert(false)
    }

    RoundNearestTiesToAway() match {
      case RTP() => assert(false)
      case RNA() => assert(true)
      case _     => assert(false)
    }
  }

  test("RoundTowardPositive is correctly constructed and extracted") {
    RoundTowardPositive() match {
      case RoundNearestTiesToAway() => assert(false)
      case RoundTowardPositive()    => assert(true)
      case _                        => assert(false)
    }

    RoundTowardPositive() match {

      case RTN() => assert(false)
      case RTP() => assert(true)
      case _     => assert(false)
    }
  }

  test("RoundTowardNegative is correctly constructed and extracted") {
    RoundTowardNegative() match {
      case RoundNearestTiesToAway() => assert(false)
      case RoundTowardNegative()    => assert(true)
      case _                        => assert(false)
    }

    RoundTowardNegative() match {
      case RTZ() => assert(false)
      case RTN() => assert(true)
      case _     => assert(false)
    }
  }

  test("RoundTowardZero is correctly constructed and extracted") {
    RoundTowardZero() match {
      case RoundNearestTiesToAway() => assert(false)
      case RoundTowardZero()        => assert(true)
      case _                        => assert(false)
    }

    RoundTowardZero() match {
      case RTN() => assert(false)
      case RTZ() => assert(true)
      case _     => assert(false)
    }
  }

  test("RNE is correctly constructed and extracted") {
    RNE() match {
      case RTZ() => assert(false)
      case RNE() => assert(true)
      case _     => assert(false)
    }

    RNE() match {
      case RoundNearestTiesToAway() => assert(false)
      case RoundNearestTiesToEven() => assert(true)
      case _                        => assert(false)
    }
  }

  test("RNA is correctly constructed and extracted") {
    RNA() match {
      case RTZ() => assert(false)
      case RNA() => assert(true)
      case _     => assert(false)
    }

    RNA() match {
      case RoundNearestTiesToEven() => assert(false)
      case RoundNearestTiesToAway() => assert(true)
      case _                        => assert(false)
    }
  }

  test("RTP is correctly constructed and extracted") {
    RTP() match {
      case RTZ() => assert(false)
      case RTP() => assert(true)
      case _     => assert(false)
    }
  }

  test("RTN is correctly constructed and extracted") {
    RTN() match {
      case RTZ() => assert(false)
      case RTN() => assert(true)
      case _     => assert(false)
    }

    RTN() match {
      case RoundTowardZero()     => assert(false)
      case RoundTowardNegative() => assert(true)
      case _                     => assert(false)
    }
  }

  test("RTZ is correctly constructed and extracted") {
    RTZ() match {
      case RTN() => assert(false)
      case RTZ() => assert(true)
      case _     => assert(false)
    }

    RTZ() match {
      case RoundTowardNegative() => assert(false)
      case RoundTowardZero()     => assert(true)
      case _                     => assert(false)
    }
  }

  // Representation of 89.2 in IEEE 754 single precision
  val bvlit_89_2 = (
    BitVectorLit(List(true)),
    BitVectorLit(List(true, false, false, false, false, true, false, true)),
    BitVectorLit(
      List(false, true, true, false, false, true, false, false, true, true,
        false, false, true, true, false, false, true, true, false, false, true,
        true, false)
    )
  )

  val fplit_89_2 = FPLit(bvlit_89_2._1, bvlit_89_2._2, bvlit_89_2._3)

  test("FloatingPointLit are correctly constructed and extracted") {
    fplit_89_2 match {
      case FPLit(bvlit_89_2._2, bvlit_89_2._2, bvlit_89_2._3) => assert(false)
      case FPLit(bvlit_89_2._1, bvlit_89_2._2, bvlit_89_2._3) => assert(true)
      case _                                                  => assert(false)
    }
  }

  test("PlusInfinity is correctly constructed and extracted") {
    PlusInfinity(8, 24) match {
      case PlusInfinity(5, 11) => assert(false)
      case PlusInfinity(8, 24) => assert(true)
      case _                   => assert(false)
    }
  }

  test("MinusInfinity is correctly constructed and extracted") {
    MinusInfinity(8, 24) match {
      case MinusInfinity(5, 11) => assert(false)
      case MinusInfinity(8, 24) => assert(true)
      case _                    => assert(false)
    }
  }

  test("NaN is correctly constructed and extracted") {
    NaN(8, 24) match {
      case NaN(5, 11) => assert(false)
      case NaN(8, 24) => assert(true)
      case _          => assert(false)
    }
  }

  test("Abs is correctly constructed and extracted") {
    Abs(fplit_89_2) match {
      case Abs(PlusZero(8, 24)) => assert(false)
      case Abs(`fplit_89_2`)    => assert(true)
      case _                    => assert(false)
    }
  }

  test("Neg is correctly constructed and extracted") {
    Neg(fplit_89_2) match {
      case Neg(PlusZero(8, 24)) => assert(false)
      case Neg(`fplit_89_2`)    => assert(true)
      case _                    => assert(false)
    }
  }

  test("Add is correctly constructed and extracted") {
    Add(RNE(), fplit_89_2, fplit_89_2) match {
      case Add(RNE(), PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Add(RNE(), `fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                            => assert(false)
    }
  }

  test("Sub is correctly constructed and extracted") {
    Sub(RNE(), fplit_89_2, fplit_89_2) match {
      case Sub(RNE(), PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Sub(RNE(), `fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                            => assert(false)
    }
  }

  test("Mul is correctly constructed and extracted") {
    Mul(RNE(), fplit_89_2, fplit_89_2) match {
      case Mul(RNE(), PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Mul(RNE(), `fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                            => assert(false)
    }
  }

  test("Div is correctly constructed and extracted") {
    Div(RNE(), fplit_89_2, fplit_89_2) match {
      case Div(RNE(), PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Div(RNE(), `fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                            => assert(false)
    }
  }

  test("FMA is correctly constructed and extracted") {
    FMA(RNE(), fplit_89_2, fplit_89_2, fplit_89_2) match {
      case FMA(RNE(), PlusZero(8, 24), PlusZero(8, 24), PlusZero(8, 24)) =>
        assert(false)
      case FMA(RNE(), `fplit_89_2`, `fplit_89_2`, `fplit_89_2`) => assert(true)
      case _                                                    => assert(false)
    }
  }

  test("Sqrt is correctly constructed and extracted") {
    Sqrt(RNE(), fplit_89_2) match {
      case Sqrt(RNE(), PlusZero(8, 24)) => assert(false)
      case Sqrt(RNE(), `fplit_89_2`)    => assert(true)
      case _                            => assert(false)
    }
  }

  test("RoundToIntegral is correctly constructed and extracted") {
    RoundToIntegral(RNE(), fplit_89_2) match {
      case RoundToIntegral(RNE(), PlusZero(8, 24)) => assert(false)
      case RoundToIntegral(RNE(), `fplit_89_2`)    => assert(true)
      case _                                       => assert(false)
    }
  }

  test("Min is correctly constructed and extracted") {
    Min(fplit_89_2, fplit_89_2) match {
      case Min(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Min(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                     => assert(false)
    }
  }

  test("Max is correctly constructed and extracted") {
    Max(fplit_89_2, fplit_89_2) match {
      case Max(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Max(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                     => assert(false)
    }
  }

  test("GreaterEquals is correctly constructed and extracted") {
    GreaterEquals(fplit_89_2, fplit_89_2) match {
      case GreaterEquals(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case GreaterEquals(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                               => assert(false)
    }
  }

  test("GreaterThan is correctly constructed and extracted") {
    GreaterThan(fplit_89_2, fplit_89_2) match {
      case GreaterThan(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case GreaterThan(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                             => assert(false)
    }
  }

  test("LessEquals is correctly constructed and extracted") {
    LessEquals(fplit_89_2, fplit_89_2) match {
      case LessEquals(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case LessEquals(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                            => assert(false)
    }
  }

  test("LessThan is correctly constructed and extracted") {
    LessThan(fplit_89_2, fplit_89_2) match {
      case LessThan(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case LessThan(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                          => assert(false)
    }
  }

  test("Equals is correctly constructed and extracted") {
    Eq(fplit_89_2, fplit_89_2) match {
      case Eq(PlusZero(8, 24), PlusZero(8, 24)) => assert(false)
      case Eq(`fplit_89_2`, `fplit_89_2`)       => assert(true)
      case _                                    => assert(false)
    }
  }

  test("IsNormal is correctly constructed and extracted") {
    IsNormal(fplit_89_2) match {
      case IsNormal(PlusZero(8, 24)) => assert(false)
      case IsNormal(`fplit_89_2`)    => assert(true)
      case _                         => assert(false)
    }
  }

  test("IsSubnormal is correctly constructed and extracted") {
    IsSubnormal(fplit_89_2) match {
      case IsSubnormal(PlusZero(8, 24)) => assert(false)
      case IsSubnormal(`fplit_89_2`)    => assert(true)
      case _                            => assert(false)
    }
  }

  test("IsZero is correctly constructed and extracted") {
    IsZero(fplit_89_2) match {
      case IsZero(PlusZero(8, 24)) => assert(false)
      case IsZero(`fplit_89_2`)    => assert(true)
      case _                       => assert(false)
    }
  }

  test("IsInfinite is correctly constructed and extracted") {
    IsInfinite(fplit_89_2) match {
      case IsInfinite(PlusZero(8, 24)) => assert(false)
      case IsInfinite(`fplit_89_2`)    => assert(true)
      case _                           => assert(false)
    }
  }

  test("IsNaN is correctly constructed and extracted") {
    IsNaN(fplit_89_2) match {
      case IsNaN(PlusZero(8, 24)) => assert(false)
      case IsNaN(`fplit_89_2`)    => assert(true)
      case _                      => assert(false)
    }
  }

  test("IsNegative is correctly constructed and extracted") {
    IsNegative(fplit_89_2) match {
      case IsNegative(PlusZero(8, 24)) => assert(false)
      case IsNegative(`fplit_89_2`)    => assert(true)
      case _                           => assert(false)
    }
  }

  test("IsPositive is correctly constructed and extracted") {
    IsPositive(fplit_89_2) match {
      case IsPositive(PlusZero(8, 24)) => assert(false)
      case IsPositive(`fplit_89_2`)    => assert(true)
      case _                           => assert(false)
    }
  }

  val bvlit_nan = BitVectorLit(List.fill(16)(true))

  test("ToFP is correctly constructed and extracted") {
    ToFP(5, 11, bvlit_nan) match {
      case ToFP(5, 11, Seq(RNE(), PlusZero(8, 24))) => assert(false)
      case ToFP(5, 11, Seq(bvlit_nan))              => assert(true)
      case _                                        => assert(false)
    }

    ToFP(5, 11, RNE(), PlusZero(8, 24)) match {
      case ToFP(5, 11, Seq(bvlit_nan))              => assert(false)
      case ToFP(5, 11, Seq(RNE(), PlusZero(8, 24))) => assert(true)
      case _                                        => assert(false)
    }
  }

  test("ToFPUnsigned is correctly constructed and extracted") {
    ToFPUnsigned(5, 11, RNE(), bvlit_nan) match {
      case ToFPUnsigned(5, 11, RNA(), bvlit_nan) => assert(false)
      case ToFPUnsigned(5, 11, RNE(), bvlit_nan) => assert(true)
      case _                                     => assert(false)
    }
  }

  test("ToUnsignedBitVector is correctly constructed and extracted") {
    ToUnsignedBitVector(32, RNE(), fplit_89_2) match {
      case ToUnsignedBitVector(32, RNE(), PlusZero(8, 24)) => assert(false)
      case ToUnsignedBitVector(32, RNE(), `fplit_89_2`)    => assert(true)
      case _                                               => assert(false)
    }
  }

  test("ToSignedBitVector is correctly constructed and extracted") {
    ToSignedBitVector(32, RNE(), fplit_89_2) match {
      case ToSignedBitVector(32, RNE(), PlusZero(8, 24)) => assert(false)
      case ToSignedBitVector(32, RNE(), `fplit_89_2`)    => assert(true)
      case _                                             => assert(false)
    }
  }

  test("ToReal is correctly constructed and extracted") {
    ToReal(fplit_89_2) match
      case ToReal(PlusZero(8, 24)) => assert(false)
      case ToReal(`fplit_89_2`)    => assert(true)
      case _                       => assert(false)
  }
}

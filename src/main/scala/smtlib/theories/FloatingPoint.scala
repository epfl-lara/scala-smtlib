package smtlib
package theories

import trees.Terms._
import common._

import Operations._

/** Floating point arithmetic theory as defined in the SMT-LIB standard
  * https://smt-lib.org/theories-FloatingPoint.shtml
  */
object FloatingPoint {

  // -------
  // Sorts
  // -------

  object RoundingModeSort {
    def apply(): Sort = {
      Sort(Identifier(SSymbol("RoundingMode")))
    }
    def unapply(sort: Sort): Boolean = sort match {
      case Sort(Identifier(SSymbol("RoundingMode"), Seq()), Seq()) => true
      case _                                                       => false
    }
  }

  export Reals.RealSort

  // Bit vector sorts, indexed by vector size
  export FixedSizeBitVectors.BitVectorSort

  /** Floating point sort, indexed by the length of the exponent and significand
    * components of the number.
    */
  object FloatingPointSort {
    def apply(ebits: BigInt, sbits: BigInt): Sort = {
      require(ebits > 1)
      require(sbits > 1)
      Sort(
        Identifier(
          SSymbol("FloatingPoint"),
          Seq(SNumeral(ebits), SNumeral(sbits))
        )
      )
    }
    def unapply(sort: Sort): Option[(BigInt, BigInt)] = sort match {
      case Sort(
            Identifier(SSymbol("FloatingPoint"), Seq(SNumeral(e), SNumeral(s))),
            Seq()
          ) if e > 1 && s > 1 =>
        Some(e, s)
      case Sort(Identifier(SSymbol("Float16"), Seq()), Seq())  => Some(5, 11)
      case Sort(Identifier(SSymbol("Float32"), Seq()), Seq())  => Some(8, 24)
      case Sort(Identifier(SSymbol("Float64"), Seq()), Seq())  => Some(11, 53)
      case Sort(Identifier(SSymbol("Float128"), Seq()), Seq()) => Some(15, 113)
      case _                                                   => None
    }
  }

  // Short name for common floating point sorts

  object Float16 {
    def apply(): Sort = {
      Sort(Identifier(SSymbol("Float16")))
    }
    def unapply(sort: Sort): Boolean = sort match {
      case Sort(
            Identifier(
              SSymbol("FloatingPoint"),
              Seq(SNumeral(5), SNumeral(11))
            ),
            Seq()
          ) =>
        true
      case Sort(Identifier(SSymbol("Float16"), Seq()), Seq()) => true
      case _                                                  => false
    }
  }

  object Float32 {
    def apply(): Sort = {
      Sort(Identifier(SSymbol("Float32")))
    }
    def unapply(sort: Sort): Boolean = sort match {
      case Sort(
            Identifier(
              SSymbol("FloatingPoint"),
              Seq(SNumeral(8), SNumeral(24))
            ),
            Seq()
          ) =>
        true
      case Sort(Identifier(SSymbol("Float32"), Seq()), Seq()) => true
      case _                                                  => false
    }
  }

  object Float64 {
    def apply(): Sort = {
      Sort(Identifier(SSymbol("Float64")))
    }
    def unapply(sort: Sort): Boolean = sort match {
      case Sort(
            Identifier(
              SSymbol("FloatingPoint"),
              Seq(SNumeral(11), SNumeral(53))
            ),
            Seq()
          ) =>
        true
      case Sort(Identifier(SSymbol("Float64"), Seq()), Seq()) => true
      case _                                                  => false
    }
  }

  object Float128 {
    def apply(): Sort = {
      Sort(Identifier(SSymbol("Float128")))
    }
    def unapply(sort: Sort): Boolean = sort match {
      case Sort(
            Identifier(
              SSymbol("FloatingPoint"),
              Seq(SNumeral(15), SNumeral(113))
            ),
            Seq()
          ) =>
        true
      case Sort(Identifier(SSymbol("Float128"), Seq()), Seq()) => true
      case _                                                   => false
    }
  }

  // ----------------
  // Rounding modes
  // ----------------

  // Constants for rounding modes, and their abbreviated version

  object RoundNearestTiesToEven {
    def apply(): Term = QualifiedIdentifier(
      Identifier(SSymbol("roundNearestTiesToEven"))
    )

    def unapply(t: Term): Boolean = t match {
      case QualifiedIdentifier(
            Identifier(SSymbol("roundNearestTiesToEven"), Seq()),
            None
          ) =>
        true
      case QualifiedIdentifier(Identifier(SSymbol("RNE"), Seq()), None) => true
      case _                                                            => false
    }
  }
  object RoundNearestTiesToAway {
    def apply(): Term = QualifiedIdentifier(
      Identifier(SSymbol("roundNearestTiesToAway"))
    )

    def unapply(t: Term): Boolean = t match {
      case QualifiedIdentifier(
            Identifier(SSymbol("roundNearestTiesToAway"), Seq()),
            None
          ) =>
        true
      case QualifiedIdentifier(Identifier(SSymbol("RNA"), Seq()), None) => true
      case _                                                            => false
    }
  }

  object RoundTowardPositive {
    def apply(): Term = QualifiedIdentifier(
      Identifier(SSymbol("roundTowardPositive"))
    )

    def unapply(t: Term): Boolean = t match {
      case QualifiedIdentifier(
            Identifier(SSymbol("roundTowardPositive"), Seq()),
            None
          ) =>
        true
      case QualifiedIdentifier(Identifier(SSymbol("RTP"), Seq()), None) => true
      case _                                                            => false
    }
  }

  object RoundTowardNegative {
    def apply(): Term = QualifiedIdentifier(
      Identifier(SSymbol("roundTowardNegative"))
    )

    def unapply(t: Term): Boolean = t match {
      case QualifiedIdentifier(
            Identifier(SSymbol("roundTowardNegative"), Seq()),
            None
          ) =>
        true
      case QualifiedIdentifier(Identifier(SSymbol("RTN"), Seq()), None) => true
      case _                                                            => false
    }
  }

  object RoundTowardZero {
    def apply(): Term = QualifiedIdentifier(
      Identifier(SSymbol("roundTowardZero"))
    )

    def unapply(t: Term): Boolean = t match {
      case QualifiedIdentifier(
            Identifier(SSymbol("roundTowardZero"), Seq()),
            None
          ) =>
        true
      case QualifiedIdentifier(Identifier(SSymbol("RTZ"), Seq()), None) => true
      case _                                                            => false
    }
  }

  lazy val RNE = RoundNearestTiesToEven
  lazy val RNA = RoundNearestTiesToAway
  lazy val RTP = RoundTowardPositive
  lazy val RTN = RoundTowardNegative
  lazy val RTZ = RoundTowardZero

  // --------------------
  // Value constructors
  // --------------------

  // Bitvector literals
  export FixedSizeBitVectors.BitVectorLit

  /** FP literals as bit string triples, with the leading bit for the
    * significand not represented (hidden bit)
    */
  object FPLit extends Operation3 { override val name = "fp" }

  // Plus and minus infinity

  object PlusInfinity {
    def apply(ebits: BigInt, sbits: BigInt): Term = {
      require(ebits > 1)
      require(sbits > 1)
      QualifiedIdentifier(
        Identifier(SSymbol("+oo"), Seq(SNumeral(ebits), SNumeral(sbits)))
      )
    }
    def unapply(sort: Term): Option[(BigInt, BigInt)] = sort match {
      case QualifiedIdentifier(
            Identifier(SSymbol("+oo"), Seq(SNumeral(e), SNumeral(s))),
            None
          ) if e > 1 && s > 1 =>
        Some(e, s)
      case _ => None
    }
  }

  object MinusInfinity {
    def apply(ebits: BigInt, sbits: BigInt): Term = {
      require(ebits > 1)
      require(sbits > 1)
      QualifiedIdentifier(
        Identifier(SSymbol("-oo"), Seq(SNumeral(ebits), SNumeral(sbits)))
      )
    }
    def unapply(sort: Term): Option[(BigInt, BigInt)] = sort match {
      case QualifiedIdentifier(
            Identifier(SSymbol("-oo"), Seq(SNumeral(e), SNumeral(s))),
            None
          ) if e > 1 && s > 1 =>
        Some(e, s)
      case _ => None
    }
  }

  // Plus and minus zero

  object PlusZero {
    def apply(ebits: BigInt, sbits: BigInt): Term = {
      require(ebits > 1)
      require(sbits > 1)
      QualifiedIdentifier(
        Identifier(SSymbol("+zero"), Seq(SNumeral(ebits), SNumeral(sbits)))
      )
    }
    def unapply(sort: Term): Option[(BigInt, BigInt)] = sort match {
      case QualifiedIdentifier(
            Identifier(SSymbol("+zero"), Seq(SNumeral(e), SNumeral(s))),
            None
          ) if e > 1 && s > 1 =>
        Some(e, s)
      case _ => None
    }
  }

  object MinusZero {
    def apply(ebits: BigInt, sbits: BigInt): Term = {
      require(ebits > 1)
      require(sbits > 1)
      QualifiedIdentifier(
        Identifier(SSymbol("-zero"), Seq(SNumeral(ebits), SNumeral(sbits)))
      )
    }
    def unapply(sort: Term): Option[(BigInt, BigInt)] = sort match {
      case QualifiedIdentifier(
            Identifier(SSymbol("-zero"), Seq(SNumeral(e), SNumeral(s))),
            None
          ) if e > 1 && s > 1 =>
        Some(e, s)
      case _ => None
    }
  }

  // Non-numbers

  object NaN {
    def apply(ebits: BigInt, sbits: BigInt): Term = {
      require(ebits > 1)
      require(sbits > 1)
      QualifiedIdentifier(
        Identifier(SSymbol("NaN"), Seq(SNumeral(ebits), SNumeral(sbits)))
      )
    }
    def unapply(sort: Term): Option[(BigInt, BigInt)] = sort match {
      case QualifiedIdentifier(
            Identifier(SSymbol("NaN"), Seq(SNumeral(e), SNumeral(s))),
            None
          ) if e > 1 && s > 1 =>
        Some(e, s)
      case _ => None
    }
  }

  // -----------
  // Operators
  // -----------

  // absolute value
  object Abs extends Operation1 { override val name = "fp.abs" }
  // negation (no rounding needed)
  object Neg extends Operation1 { override val name = "fp.neg" }
  // addition
  object Add extends Operation3 { override val name = "fp.add" }
  // subtraction
  object Sub extends Operation3 { override val name = "fp.sub" }
  // multiplication
  object Mul extends Operation3 { override val name = "fp.mul" }
  // division
  object Div extends Operation3 { override val name = "fp.div" }
  // fused multiplication and addition; (x * y) + z
  object FMA extends Operation4 { override val name = "fp.fma" }
  // square root
  object Sqrt extends Operation2 { override val name = "fp.sqrt" }
  // remainder: x - y * n, where n in Z is nearest to x/y
  object Rem extends Operation2 { override val name = "fp.rem" }
  // rounding to integral
  object RoundToIntegral extends Operation2 {
    override val name = "fp.roundToIntegral"
  }
  // minimum and maximum
  object Min extends Operation2 { override val name = "fp.min" }
  object Max extends Operation2 { override val name = "fp.max" }
  // comparison operators
  // Note that all comparisons evaluate to false if either argument is NaN
  object LessEquals extends Operation2 { override val name = "fp.leq" }
  object LessThan extends Operation2 { override val name = "fp.lt" }
  object GreaterEquals extends Operation2 { override val name = "fp.geq" }
  object GreaterThan extends Operation2 { override val name = "fp.gt" }
  // IEEE 754-2008 equality (as opposed to SMT-LIB =)
  object Eq extends Operation2 { override val name = "fp.eq" }
  // Classification of numbers
  object IsNormal extends Operation1 { override val name = "fp.isNormal" }
  object IsSubnormal extends Operation1 { override val name = "fp.isSubnormal" }
  object IsZero extends Operation1 { override val name = "fp.isZero" }
  object IsInfinite extends Operation1 { override val name = "fp.isInfinite" }
  object IsNaN extends Operation1 { override val name = "fp.isNaN" }
  object IsNegative extends Operation1 { override val name = "fp.isNegative" }
  object IsPositive extends Operation1 { override val name = "fp.isPositive" }

  lazy val + = Add
  lazy val - = Sub
  lazy val * = Mul
  lazy val / = Div
  lazy val > = GreaterThan
  lazy val >= = GreaterEquals
  lazy val < = LessThan
  lazy val <= = LessEquals

  // ------------------------------
  // Conversions from other sorts
  // ------------------------------

  object ToFP {
    def apply(ebits: BigInt, sbits: BigInt, arg: Term): Term = {
      require(ebits > 1)
      require(sbits > 1)
      FunctionApplication(
        QualifiedIdentifier(
          Identifier(SSymbol("to_fp"), Seq(SNumeral(ebits), SNumeral(sbits)))
        ),
        Seq(arg)
      )
    }

    def apply(
        ebits: BigInt,
        sbits: BigInt,
        roundingMode: Term,
        arg: Term
    ): Term = {
      require(ebits > 1)
      require(sbits > 1)
      FunctionApplication(
        QualifiedIdentifier(
          Identifier(SSymbol("to_fp"), Seq(SNumeral(ebits), SNumeral(sbits)))
        ),
        Seq(roundingMode, arg)
      )
    }

    def unapply(sort: Term): Option[(BigInt, BigInt, Seq[Term])] = sort match {
      case FunctionApplication(
            QualifiedIdentifier(
              Identifier(SSymbol("to_fp"), Seq(SNumeral(e), SNumeral(s))),
              None
            ),
            seq
          ) if e > 1 && s > 1 && 1 <= seq.length && seq.length <= 2 =>
        Some((e, s, seq))
      case _ => None
    }
  }

  object ToFPUnsigned {
    def apply(
        ebits: BigInt,
        sbits: BigInt,
        roundingMode: Term,
        bitvec: Term
    ): Term = {
      require(ebits > 1)
      require(sbits > 1)
      FunctionApplication(
        QualifiedIdentifier(
          Identifier(
            SSymbol("to_fp_unsigned"),
            Seq(SNumeral(ebits), SNumeral(sbits))
          )
        ),
        Seq(roundingMode, bitvec)
      )
    }

    def unapply(sort: Term): Option[(BigInt, BigInt, Term, Term)] = sort match {
      case FunctionApplication(
            QualifiedIdentifier(
              Identifier(
                SSymbol("to_fp_unsigned"),
                Seq(SNumeral(e), SNumeral(s))
              ),
              None
            ),
            Seq(roundingMode, bitvec)
          ) if e > 1 && s > 1 =>
        Some((e, s, roundingMode, bitvec))
      case _ => None
    }
  }

  // ----------------------------
  // Conversions to other sorts
  // ----------------------------

  // to unsigned machine integer, represented as a bit vector
  object ToUnsignedBitVector {
    def apply(length: BigInt, roundingMode: Term, arg: Term): Term = {
      require(length > 0)
      FunctionApplication(
        QualifiedIdentifier(
          Identifier(SSymbol("fp.to_ubv"), Seq(SNumeral(length))),
          None
        ),
        Seq(roundingMode, arg)
      )
    }

    def unapply(sort: Term): Option[(BigInt, Term, Term)] = sort match {
      case FunctionApplication(
            QualifiedIdentifier(
              Identifier(SSymbol("fp.to_ubv"), Seq(SNumeral(length))),
              None
            ),
            Seq(roundingMode, arg)
          ) if length > 0 =>
        Some((length, roundingMode, arg))
      case _ => None
    }
  }
  // to signed machine integer, represented as a 2's complement bit vector
  object ToSignedBitVector {
    def apply(length: BigInt, roundingMode: Term, arg: Term): Term = {
      require(length > 0)
      FunctionApplication(
        QualifiedIdentifier(
          Identifier(SSymbol("fp.to_sbv"), Seq(SNumeral(length))),
          None
        ),
        Seq(roundingMode, arg)
      )
    }

    def unapply(sort: Term): Option[(BigInt, Term, Term)] = sort match {
      case FunctionApplication(
            QualifiedIdentifier(
              Identifier(SSymbol("fp.to_sbv"), Seq(SNumeral(length))),
              None
            ),
            Seq(roundingMode, arg)
          ) if length > 0 =>
        Some((length, roundingMode, arg))
      case _ => None
    }
  }
  // to real
  object ToReal extends Operation1 { override val name = "fp.to_real" }

}

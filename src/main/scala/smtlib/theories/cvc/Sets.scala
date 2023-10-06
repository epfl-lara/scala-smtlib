package smtlib
package theories
package cvc

import trees.Terms._
import Operations._

sealed trait Sets {
  protected val SetUnion: String
  protected val SetIntersection: String
  protected val SetMinus: String
  protected val SetMember: String
  protected val SetSubset: String
  protected val SetEmptyset: String
  protected val SetSingleton: String
  protected val SetInsert: String
  protected val SetCard: String

  object SetSort {
    def apply(el : Sort): Sort = Sort(Identifier(SSymbol("Set")), Seq(el))

    def unapply(sort : Sort): Option[Sort] = sort match {
      case Sort(Identifier(SSymbol("Set"), Seq()), Seq(el)) => Some(el)
      case _ => None
    }
  }

  object Union extends Operation2 { override val name = SetUnion }
  object Intersection extends Operation2 { override val name = SetIntersection }
  object Setminus extends Operation2 { override val name = SetMinus }
  object Member extends Operation2 { override val name = SetMember }
  object Subset extends Operation2 { override val name = SetSubset }

  object EmptySet {
    def apply(s : Sort): Term = QualifiedIdentifier(Identifier(SSymbol(SetEmptyset)), Some(s))
    def unapply(t : Term): Option[Sort] = t match {
      case QualifiedIdentifier(Identifier(SSymbol(SetEmptyset), Seq()), Some(s)) =>
          Some(s)
      case _ => None
    }
  }

  object Singleton extends Operation1 { override val name = SetSingleton }

  object Insert extends OperationN2 { override val name = SetInsert }

  object Card extends Operation1 { override val name = SetCard }
}

object CVC5Sets extends Sets {
  override protected val SetUnion: String = "set.union"
  override protected val SetIntersection: String = "set.inter"
  override protected val SetMinus: String = "set.minus"
  override protected val SetMember: String = "set.member"
  override protected val SetSubset: String = "set.subset"
  override protected val SetEmptyset: String = "set.empty"
  override protected val SetSingleton: String = "set.singleton"
  override protected val SetInsert: String = "set.insert"
  override protected val SetCard: String = "set.card"
}

object CVC4Sets extends Sets {
  override protected val SetUnion: String = "union"
  override protected val SetIntersection: String = "intersection"
  override protected val SetMinus: String = "setminus"
  override protected val SetMember: String = "member"
  override protected val SetSubset: String = "subset"
  override protected val SetEmptyset: String = "emptyset"
  override protected val SetSingleton: String = "singleton"
  override protected val SetInsert: String = "insert"
  override protected val SetCard: String = "card"
}
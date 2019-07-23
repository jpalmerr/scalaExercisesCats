import cats._
import cats.implicits._

Monoid[String].empty
// String = ""

Monoid[String].combineAll(List("a", "b", "c"))
//String = abc

Monoid[String].combineAll(List())
// String = ""

// we can use monoids on more complex types

Monoid[Map[String, Int]].combineAll(List(Map("a" → 1, "b" → 2), Map("a" → 3)))
// Map(b -> 2, a -> 4)

Monoid[Map[String, Int]].combineAll(List())
// Map()

// FOLDABLE

val l = List(1, 2, 3, 4, 5)
l.foldMap(identity)
// 15
l.foldMap(i ⇒ i.toString)
// String = 12345 ... "12345"

// TUPLE

l.foldMap(i ⇒ (i, i.toString))
// (Int, String) = (15,12345)



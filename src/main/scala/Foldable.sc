/*
Foldable type class instances can be defined for data structures that can be folded to a summary value.
In the case of a collection (such as List or Set),
these methods will fold together (combine) the values contained in the collection to produce a single result.
Most collection types have foldLeft methods, which will usually be used by the associated Foldable[_] instance.
 */

import cats._
import cats.implicits._

Foldable[List].foldLeft(List(1, 2, 3), 0)(_ + _) // Int = 6
Foldable[List].foldLeft(List(1, 2, 3), 1)(_ + _) // Int = 7

Foldable[List].foldLeft(List("a", "b", "c"), "")(_ + _) // String = abc

// foldRight is of course lazy...

val lazyResult =
  Foldable[List].foldRight(List(1, 2, 3), Now(0))((x, rest) => Later(x + rest.value))

lazyResult.value // Int = 6

/*
fold, also called combineAll,
combines every value in the foldable using the given Monoid instance.
 */

Foldable[List].fold(List("a", "b", "c")) //  "abc"

Foldable[List].fold(List(1, 2, 3)) // 6

Foldable[List].foldMap(List("a", "b", "c"))(_.length) // 3
Foldable[List].foldMap(List(1, 2, 3))(_.toString) // "123"

Foldable[List].foldK(List(List(1, 2), List(3, 4, 5))) // List(1, 2, 3, 4, 5)
Foldable[List].foldK(List(None, Option("two"), Option("three"))) // Option[String] = Some(two)

Foldable[List].find(List(1, 2, 3))(_ > 2) // Some(3)

Foldable[List].exists(List(1, 2, 3))(_ > 2) // true

Foldable[List].forall(List(1, 2, 3))(_ <= 3) // true

// toList: Convert F[A] to List[A]

Foldable[List].toList(List(1, 2, 3)) // List(1, 2, 3)
Foldable[Option].toList(Option(42)) // List(42)
Foldable[Option].toList(None) // List()

Foldable[List].filter_(List(1, 2, 3))(_ < 3) // List(1, 2)
Foldable[Option].filter_(Option(42))(_ != 42) // List()

/*
Traverse

traverse the foldable mapping A values to G[B],
and combining them using Applicative[G]
and discarding the results.

This method is primarily useful when G[_] represents an action or effect,
and the specific B aspect of G[B] is not otherwise needed.
The B will be discarded and Unit returned instead.
 */

def parseInt(s: String): Option[Int] =
  Either.catchOnly[NumberFormatException](s.toInt).toOption

Foldable[List].traverse_(List("1", "2", "3"))(parseInt)
Foldable[List].traverse_(List("a", "b", "c"))(parseInt)


/*
Compose:

compose Foldable[F[_]] and Foldable[G[_]] instances to obtain Foldable[F[G]]
 */

val FoldableListOption = Foldable[List].compose[Option]

FoldableListOption.fold(List(Option(1), Option(2), Option(3), Option(4))) // Int = 10
FoldableListOption.fold(List(Option("1"), Option("2"), None, Option("3"))) // String = 123

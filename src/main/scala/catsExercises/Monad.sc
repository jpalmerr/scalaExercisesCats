/*
Monad extends the Applicative type class with a new function flatten
 */

Option(Option(1)).flatten // Some(1)
Option(None).flatten // None
List(List(1), List(2, 3)).flatten // List(1,2,3)

import cats._
import cats.implicits._

Monad[Option].pure(42) // Some(42)

Monad[List].flatMap(List(1, 2, 3))(x => List(x, x))
// List(1, 1, 2, 2, 3, 3)

/*
ifM

lifts an if statement into the monadic context
 */

  /**
   * `if` lifted into monad.
   */
 /* def ifM[B](fa: F[Boolean])(ifTrue: => F[B], ifFalse: => F[B]): F[B] =
    flatMap(fa)(if (_) ifTrue else ifFalse)
  */

Monad[Option].ifM(Option(true))(Option("truthy"), Option("falsy")) // Some(truthy)
Monad[List].ifM(List(true, false, true))(List(1, 2), List(3, 4)) // List(1, 2, 3, 4, 1, 2)

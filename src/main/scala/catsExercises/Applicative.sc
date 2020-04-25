import cats._
import cats.implicits._

/*
Applicative extends Apply by adding a single method, pure

This method takes any value and returns the value in the context of the functor
 */

Applicative[Option].pure(1) // Some(1)
Applicative[List].pure(1)   // List(1)

/*
Like Functor and Apply, Applicative functors also compose naturally with each other.
When you compose one Applicative with another, the resulting pure operation will lift the passed value into one context,
and the result into the other context
 */
(Applicative[List] compose Applicative[Option]).pure(1) // List(Some(1))

Monad[Option].pure(1)   // Some(1)

import cats.{Applicative, Comonad, Id}

/*
type Id[A] = A

pure: A => Id[A]
 */

Applicative[Id].pure(42) // 42

Comonad[Id].coflatMap(42)(_ + 1)


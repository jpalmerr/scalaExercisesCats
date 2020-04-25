import cats.Functor
import cats.implicits._

// MAP

Functor[Option].map(Option("Hello"))(_.length)
// Some(5)

Functor[Option].map(None: Option[String])(_.length)
// None

// LIFT

/*
we can use Functor to "lift"
a function A => B to F[A] => F[B]
 */

val lenOption: Option[String] ⇒ Option[Int] = Functor[Option].lift(_.length)
lenOption(Some("Hello"))
// Some(5)

// FPRODUCT

// pairs a value with the result of applying a function to that value

val source = List("Cats", "is", "awesome")
val product = Functor[List].fproduct(source)(_.length).toMap

product.get("Cats").getOrElse(0)
// 4
product.get("is").getOrElse(0)
// 2
product.getOrElse("awesome", 0)
// 7

// COMPOSE

/*
Given any functor F[_] and any functor G[_]
we can create a new functor F[G[_]] by composing them
 */

val listOpt = Functor[List] compose Functor[Option]
listOpt.map(List(Some(1), None, Some(3)))(_ + 1)
// List(Some(2), None, Some(4))
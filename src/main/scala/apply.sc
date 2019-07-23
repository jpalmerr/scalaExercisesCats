import cats.Apply
import cats.implicits._

val intToString: Int ⇒ String = _.toString
val double: Int ⇒ Int = _ * 2
val addTwo: Int ⇒ Int = _ + 2

// MAP

Apply[Option].map(Some(1))(intToString)
// Option[String] = Some(1) ... Some("1")

Apply[Option].map(Some(1))(double)
// Option[Int] = Some(2)

Apply[Option].map(None)(addTwo)
// None

// COMPOSE

val listOpt = Apply[List] compose Apply[Option]
val plusOne = (x: Int) ⇒ x + 1

listOpt.ap(List(Some(plusOne)))(List(Some(1), None, Some(3)))
// List[Option[Int]] = List(Some(2), None, Some(4))

// AP

Apply[Option].ap(Some(intToString))(Some(1))
// Option[String] = Some(1)
Apply[Option].ap(Some(double))(Some(1))
// Option[Int] = Some(2)
Apply[Option].ap(Some(double))(None)
// None
Apply[Option].ap(None)(Some(1))
// None
Apply[Option].ap(None)(None)
// None

val addArity2 = (a: Int, b: Int) ⇒ a + b

Apply[Option].ap2(Some(addArity2))(Some(1), Some(2))
//
Apply[Option].ap2(Some(addArity2))(Some(1), None)
//

val addArity3 = (a: Int, b: Int, c: Int) ⇒ a + b + c

Apply[Option].ap3(Some(addArity3))(Some(1), Some(2), Some(3))
// Some(6)

// MAPN
// similarly mapN functions are available

Apply[Option].map2(Some(1), Some(2))(addArity2)
// Some(3)
Apply[Option].map3(Some(1), Some(2), Some(3))(addArity3)
// Some(6)

// TUPLEN
Apply[Option].tuple2(Some(1), Some(2))
// Some((1,2))
Apply[Option].tuple3(Some(1), Some(2), Some(3))
// Some((1,2,3))
/*
Imagine case where you have entered multiple things wrong e.g. password too short and needs special characters...
annoying to have these reported one by one

=> validated goal is to report any and all errors across independent bits of data
 */

// example config


case class ConnectionParams(url: String, port: Int)

trait Read[A] {
  def read(s: String): Option[A]
}

object Read {
  def apply[A](implicit A: Read[A]): Read[A] = A

  implicit val stringRead: Read[String] =
    new Read[String] { def read(s: String): Option[String] = Some(s) }

  implicit val intRead: Read[Int] =
    new Read[Int] {
      def read(s: String): Option[Int] =
        if (s.matches("-?[0-9]+")) Some(s.toInt)
        else None
    }
}

sealed abstract class ConfigError
final case class MissingConfig(field: String) extends ConfigError
final case class ParseError(field: String)    extends ConfigError

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

// seen usages of this at work
case class Config(map: Map[String, String]) {
  def parse[A: Read](key: String): Validated[ConfigError, A] =
    map.get(key) match {
      case None => Invalid(MissingConfig(key))
      case Some(value) =>
        Read[A].read(value) match {
          case None    => Invalid(ParseError(key))
          case Some(a) => Valid(a)
        }
    }
}

/*
In the case where both have errors, we want to report both.
But we have no way of combining the two errors into one error!
How then do we abstract over a binary operation?
 */

import cats.Semigroup

def parallelValidate[E: Semigroup, A, B, C](v1: Validated[E, A], v2: Validated[E, B])(
  f: (A, B) => C): Validated[E, C] =
  (v1, v2) match {
    case (Valid(a), Valid(b))       => Valid(f(a, b))
    case (Valid(_), i @ Invalid(_)) => i
    case (i @ Invalid(_), Valid(_)) => i
    case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup[E].combine(e1, e2))
  }

import cats.SemigroupK
import cats.data.NonEmptyList
import cats.implicits._

implicit val nelSemigroup: Semigroup[NonEmptyList[ConfigError]] =
  SemigroupK[NonEmptyList].algebra[ConfigError]

implicit val readString: Read[String] = Read.stringRead
implicit val readInt: Read[Int]       = Read.intRead

// -------------------------------------------------------------------

println("Usage")

// => when no errors are present we get a ConnectionParams wrapped in a Valid instance

val config = Config(Map(("url", "127.0.0.1"), ("port", "1337")))

val valid = parallelValidate(
  config.parse[String]("url").toValidatedNel,
  config.parse[Int]("port").toValidatedNel)(ConnectionParams.apply)
// Valid(ConnectionParams(127.0.0.1,1337))


valid.getOrElse(ConnectionParams("", 0))

/* => But what happens when having one or more errors?
 They are accumulated in a NonEmptyList wrapped in an Invalid instance
 */

val badConfig = Config(Map(("endpoint", "127.0.0.1"), ("port", "not a number")))

val invalid = parallelValidate(
  badConfig.parse[String]("url").toValidatedNel,
  badConfig.parse[Int]("port").toValidatedNel)(ConnectionParams.apply)
// Invalid(NonEmptyList(MissingConfig(url), ParseError(port)))

// ---------------------------------------------------------------------

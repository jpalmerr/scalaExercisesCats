// Catching exceptions with either


sealed abstract class AppError1
final case object DatabaseError1 extends AppError
final case object DatabaseError2 extends AppError
final case object ServiceError1 extends AppError
final case object ServiceError2 extends AppError

trait ServiceValue1
trait DatabaseValue1

object Database1 {
  def databaseThings1(): Either[AppError1, DatabaseValue1] = ???
}

object Service1 {
  def serviceThings1(v: DatabaseValue1): Either[AppError, ServiceValue1] = ???
}

def doApp: Either[AppError1, ServiceValue1] = Database1.databaseThings1().flatMap(Service1.serviceThings1)


/*
But consider the case where another module wants to just use Database, and gets an Either[AppError, DatabaseValue] back.
Should it want to inspect the errors, it must inspect **all** the AppError cases,
even though it was only intended for Database to use DatabaseError1 or DatabaseError2
 */

/*
Instead of lumping all our errors into one big ADT,
we can instead keep them local to each module,
and have an application-wide error ADT that wraps each error ADT we need.
 */

import cats.implicits._

sealed abstract class DatabaseError
trait DatabaseValue

object Database {
  def databaseThings(): Either[DatabaseError, DatabaseValue] = ???
}

sealed abstract class ServiceError
trait ServiceValue

object Service {
  def serviceThings(v: DatabaseValue): Either[ServiceError, ServiceValue] = ???
}

sealed abstract class AppError
object AppError {
  final case class Database(error: DatabaseError) extends AppError
  final case class Service(error: ServiceError) extends AppError
}

def doApp: Either[AppError, ServiceValue] =
  Database.databaseThings().leftMap(AppError.Database).
    flatMap(dv => Service.serviceThings(dv).leftMap(AppError.Service))
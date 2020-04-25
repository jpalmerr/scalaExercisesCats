# Traverse

```
import scala.concurrent.Future

def parseInt(s: String): Option[Int] = ???

trait SecurityError
trait Credentials

def validateLogin(cred: Credentials): Either[SecurityError, Unit] = ???

trait Profile
trait User

def userInfo(user: User): Future[Profile] = ???

-------------------------------

def profilesFor(users: List[User]): List[Future[Profile]] = users.map(userInfo)
```

Notice our return type `List[Future[Profile]]`.
This makes sense but we'd have to call combinators (e.g. map) on `Future`
for every single one.

It would be nicer if we could wrap it in one single future:
`Future[List[Profile]]`

## The type class

```
trait Traverse[F[_]] {
  def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
}
```

In our above example, F is List, and G is Option, Either, or Future.

---------------------------- (best description below) -------------------------- 
 
Abstracting away the G (still imagining F to be List),

- traverse says given a collection of data, 
- and a function that takes a piece of data 
- and returns an effectful value

it will traverse the collection, applying the function
and aggregating the effectful values (in a List) as it goes.

---------------------------------------------------------------

In the most general form, `F[_]` is some sort of context which may contain a value (or several).

But there are instances for Option, Either, and Validated (among others).

## Sequencing

Sometimes you may find yourself with a collection of data, 
each of which is already in an effect, 
for instance a `List[Option[A]]`. 

To make this easier to work with, you want a `Option[List[A]]`

Given Option has an **Applicative instance**,
we can traverse over the list with the identity function.

```
import cats.implicits._

List(Option(1), Option(2), Option(3)).traverse(identity) should be(Some(List(1,2,3)))

List(Option(1), None, Option(3)).traverse(identity) should be(None)
```

Traverse provides a convenience method `sequence` that does exactly this.

```
List(Option(1), Option(2), Option(3)).sequence
List(Option(1), None, Option(3)).sequence
```

## Traversing for effects

Sometimes our effectful functions return a Unit value in cases where there is no interesting value to return.

We don't want to end up with something of type (e.g.) `List[Unit]`.

Foldable (superclass of Traverse) provides `traverse_` and `sequence_` methods that do the same thing as traverse
 and sequence but ignores any value produced along the way,
 returning Unit at the end.
 

 
// recursion

def fib(n: Int): Int = {
  @annotation.tailrec
  def loop(n: Int, prev: Int, cur: Int): Int =
    if (n <= 0) prev
    else loop(n - 1, cur, prev + cur)
  loop(n, 0, 1)
}

def isSorted[A](as: Array[A], ordering: (A, A) => Boolean): Boolean = {
  @annotation.tailrec
  def go(n: Int): Boolean =
    if (n >= as.length - 1) true
    else if (!ordering(as(n), as(n + 1))) false
    else go(n + 1)

  go(0)
}

isSorted(Array(1, 3, 5, 7), (x: Int, y: Int) => x < y)
isSorted(Array(7, 5, 1, 3), (x: Int, y: Int) => x > y)
isSorted(Array("Scala", "Exercises"), (x: String, y: String) => x.length < y.length)

// currying

def curry[A, B, C](f: (A, B) => C): A => (B => C) =
  a => b => f(a, b)

def f(a: Int, b: Int): Int = a + b
def g(a: Int)(b: Int): Int = a + b

curry(f)(1)(1)
curry(f)(1)(2)
val x: Int => Int => Int = curry(f)
val y: Int => Int = curry(f)(1)
val z: Int = y(1) // 2

curry(f)(1)(1) == g(1)(1) // true
curry(f)(1)(1) == f(1, 1) // true

def uncurry[A, B, C](f: A => B => C): (A, B) => C =
  (a, b) => f(a)(b)

uncurry(g)(1, 1) // 2

// Composing

def compose[A, B, C](f: B => C, g: A => B): A => C =
  a => f(g(a))

// does g then f

def halve(b: Int): Int = b / 2
def plus2(a: Int): Int = a + 2

compose(halve, plus2)(0) // 1
compose(plus2, halve)(0) // 2


package org.teckhooi.concurrentCE3.examples

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxApplicative

object ExampleOne extends IOApp {
  def repeat(letter: String): IO[Unit] =
    IO(print(letter)).replicateA(100).void

  override def run(args: List[String]): IO[ExitCode] =
    for {
      fa <- (repeat("A") *> repeat("B")).as("foo!").start
      fb <- (repeat("C") *> repeat("D")).as("bar!").start
      // joinAndEmbedNever is a variant of join that asserts
      // the fiber has an outcome of Succeeded and returns the
      // associated value.
      ra <- fa.join
      rb <- fb.join
      _ <- IO(println(s"\ndone: a says: $ra, b says: $rb"))
    } yield ExitCode.Success
}
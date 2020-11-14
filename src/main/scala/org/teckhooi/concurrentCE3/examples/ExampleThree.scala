package org.teckhooi.concurrentCE3.examples

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration._

object ExampleThree extends IOApp{
  def factorial(n: Long): Long =
    if (n == 0) 1 else n * factorial(n - 1)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      res <- IO.race(IO(factorial(20)) , IO.sleep(1.milli) *> IO(factorial(20)))
      _ <- res.fold(
        a => IO(println(s"Left hand side won: $a")),
        b => IO(println(s"Right hand side won: $b"))
      )
    } yield ExitCode.Success
}
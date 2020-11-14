package org.teckhooi.concurrentCE3.solutions

import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration.{FiniteDuration, _}

object TimeoutSolution extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    timeout(IO.sleep(30.millis).as("Success"), 32.millis).as(ExitCode.Success)

  def timeout[A](io: IO[A], duration: FiniteDuration): IO[A] =
    for {
      raceResult <- IO.race(io, IO.sleep(duration))
      result <- raceResult match {
        case Left(a)  => IO.pure(a)
        case Right(_) => IO.raiseError(new RuntimeException("Times up"))
      }
    } yield result
}

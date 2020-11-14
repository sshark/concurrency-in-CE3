package org.teckhooi.concurrentCE3.examples

import cats.effect.concurrent.Deferred
import cats.effect.{ExitCode, IO, IOApp}

import scala.concurrent.duration._

object ExampleFive extends IOApp {

  def countdown(n: Int, pause: Int, waiter: Deferred[IO, Unit]): IO[Unit] =
    IO(println(n)) *>
      (if (n == 0) IO.unit
      else if (n == pause) IO(println("paused...")) *> waiter.get *> countdown(n - 1, pause, waiter)
      else countdown(n - 1, pause, waiter))


  override def run(args: List[String]): IO[ExitCode] =
    for {
      waiter <- Deferred[IO, Unit]
      f      <- countdown(10, 5, waiter).start
      _      <- IO.sleep(3.seconds)
      _      <- waiter.complete(())
      _      <- IO(println("blast off!"))
    } yield ExitCode.Success
}

package org.teckhooi.concurrentCE3.examples

import cats.effect.concurrent.Ref
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object ExampleFour extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      state <- Ref.of[IO, Int](0)
      fibers <- state.update(_ + 1).start.replicateA(100)
      _ <- fibers.sequence.join
      value <- state.get
      _ <- IO(println(s"The final value is: $value"))
    } yield ExitCode.Success
}
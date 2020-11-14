package org.teckhooi.concurrentCE3.examples

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._

import scala.concurrent.duration._

object ExampleTwo extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    for {
      fiber <- IO(println("hello!")).foreverM.start
      _ <- IO.sleep(5.seconds)
      _ <- fiber.cancel
    } yield ExitCode.Success
}
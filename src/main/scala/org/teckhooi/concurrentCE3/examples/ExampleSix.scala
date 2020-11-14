package org.teckhooi.concurrentCE3.examples

import cats.effect.concurrent.{Deferred, Ref}
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

sealed trait State
final case class Awaiting(latches: Int, waiter: Deferred[IO, Unit]) extends State
case object Done                                                    extends State

object ExampleSix extends IOApp {

  trait Latch {
    def release: IO[Unit]
    def await: IO[Unit]
  }

  object Latch {

    def apply(latches: Int): IO[Latch] =
      for {
        waiter <- Deferred[IO, Unit]
        state  <- Ref.of[IO, State](Awaiting(latches, waiter))
      } yield
        new Latch {
          override def release: IO[Unit] =
            state
              .modify {
                case Awaiting(n, waiter) =>
                  if (n > 1) {
                    println(s"Remaining $n")
                    (Awaiting(n - 1, waiter), IO.unit)
                  } else
                    (Done, waiter.complete(()))
                case Done => (Done, IO.unit)
              }
              .flatten
              .void
          override def await: IO[Unit] =
            state.get.flatMap {
              case Done                => IO.unit
              case Awaiting(_, waiter) => waiter.get
            }
        }
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
      latch <- Latch(10)
      _ <- (1 to 10).toList.traverse { idx =>
        (IO(println(s"$idx counting down")) *> latch.release).start
      }
      _ <- latch.await
      _ <- IO(println("Got past the latch"))
    } yield ExitCode.Success
}

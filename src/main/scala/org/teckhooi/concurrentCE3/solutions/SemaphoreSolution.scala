package org.teckhooi.concurrentCE3.solutions

import cats.effect.concurrent.{Deferred, Ref}
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

import scala.concurrent.duration.DurationInt

final case class Counter(latches: Int, waiter: Deferred[IO, Unit])

object SemaphoreSolution extends IOApp {

  trait Semaphore {
    def acquire: IO[Unit]
    def release: IO[Unit]
  }

  object Semaphore {

    def apply(permits: Int): IO[Semaphore] =
      for {
        waiter <- Deferred[IO, Unit]
        state  <- Ref.of[IO, Counter](Counter(permits, waiter))
      } yield
        new Semaphore {
          override def acquire: IO[Unit] =
            state
              .modify {
                case Counter(c, w) =>
                  if (c > 0) {
                    (Counter(c - 1, w), IO.unit)
                  } else {
                    (Counter(0, w), w.get *> acquire)
                  }
              }
              .flatten
              .void

          override def release: IO[Unit] =
            state
              .modify {
                case Counter(c, w) => {
                  if (c == permits) (Counter(permits, w), IO.unit)
                  else if (c > 0) (Counter(c + 1, w), IO.unit)
                  else
                    (Counter(1, Deferred.unsafe[IO, Unit]), w.complete(()))
                }
              }
              .flatten
              .void
        }
  }

  override def run(args: List[String]): IO[ExitCode] =
    for {
      s     <- Semaphore(4)
      start <- IO.delay(System.currentTimeMillis())
      _ <- (1 to 10).toList.parTraverse(idx =>
        s.acquire *> IO(println(s"Taking $idx")) *> IO.sleep(1.seconds) *> IO(println(s"Releasing $idx")) *> s.release)
      _ <- IO.delay(println(s"Took ${System.currentTimeMillis() - start}ms"))
    } yield ExitCode.Success
}

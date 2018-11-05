package com.projectx.api

import java.nio.file.{Files, Path}

import cats.effect.concurrent.Ref
import cats.effect.{IO, Timer}
import fs2.Stream
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

object Storage {

  import Marshalling._

  private val logger = LoggerFactory.getLogger(getClass)

  def runPeriodicalSyncToFile(path: Path,
                              myResources: Ref[IO, MyResources],
                              interval: FiniteDuration)(implicit timer: Timer[IO]): IO[Unit] =
    Stream.eval(persistToFile(path, myResources)).delayBy(interval).repeat.compile.drain

  def restore(path: Path): IO[Ref[IO, MyResources]] = {
    for {
      bytes <- IO(Files.readAllBytes(path))
      myResources <- IO.fromEither(Marshalling.decode[MyResources](bytes))
      ref <- Ref.of[IO, MyResources](myResources)
    } yield ref
  }

  private def persistToFile(path: Path, myResourcesRef: Ref[IO, MyResources]): IO[Unit] = {
    for {
      myResources <- myResourcesRef.get
      _ <- IO(Files.write(path, Marshalling.encode(myResources).getBytes))
      _ <- IO(logger.info("File synchronized"))
    } yield ()
  }

}

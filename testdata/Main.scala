import java.nio.file.Paths

import cats.effect.{Blocker, ConcurrentEffect, ExitCode}
import fs2.io.stdout
import fs2.{Stream, io, text}
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import zio.console.{Console, putStrLn}
import zio.{Runtime, Task, ZIO}

import scala.concurrent.ExecutionContext.global
import zio.interop.catz._

object Main extends zio.App {


  ////http4s example/////
  def getSite(client: Client[Task]): Task[Unit] = Task {
    val page: Task[String] = client.expect[String](Uri.uri("https://statsapi.web.nhl.com/api/v1/teams"))

    val results = unsafeRun(page) //page.unsafeRunSync()

    println(results)

  }
  def fork: ZIO[Any, Throwable, ExitCode] = {
    Task.concurrentEffectWith { implicit CE =>
      BlazeClientBuilder[Task](global).resource
        .use(getSite)
        .as(ExitCode.Success)
    }
  }
  ////////////////////////////////

  ///fs2 using zio example/////////
  val converter: Stream[Task, Unit] = Stream.resource(Blocker[Task]).flatMap { blocker =>
    def fahrenheitToCelsius(f: Double): Double =
      (f - 32.0) * (5.0 / 9.0)

    io.file
      .readAll[Task](Paths.get("testdata/fahrenheit.txt"), blocker, 4096)
      .through(text.utf8Decode)
      .through(text.lines)
      .filter(s => !s.trim.isEmpty && !s.startsWith("//"))
      .mapAsync(4)(line => Task { fahrenheitToCelsius(line.toDouble).toString })
      .intersperse("\n")
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get("testdata/celsius.txt"), blocker))


  }
  ////////////////////
  def getCE = {
    ZIO.runtime.map { implicit r: Runtime[Any] =>
      val F: ConcurrentEffect[Task] = implicitly
    }
  }
  override def run(args: List[String]): ZIO[Console, Nothing, Int] =
    (for {
      //_ <- fork //Runs the http4s example
      _ <- converter.compile.drain //Runs the fs2 stream example

    } yield ()).foldM(err => putStrLn(s"Failed with $err").as(1), _ => Task.succeed(0))

}

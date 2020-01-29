import org.http4s._
import org.http4s.implicits._
import cats.effect._
import zio.console.{Console, putStrLn}
import zio.{Task, ZIO}
import zio.interop.catz._
import org.http4s.client.blaze._
import io.circe.generic.auto._
import fs2.Stream
import org.http4s.circe._

import scala.concurrent.ExecutionContext.global

case class Teams
(
    teams: List[Team]
)

class TWStream[Task[_]: ConcurrentEffect : ContextShift] {

  def jsonStream(req: Request[Task]): Stream[Task, Teams] =
  {
    BlazeClientBuilder[Task](global).stream.flatMap { httpClient =>
      // Decode a Hello response
      Stream.eval(httpClient.expect(req)(jsonOf[Task,Teams]))
    }
  }

  def stream(blocker: Blocker): Stream[Task, Teams] = {
    val req = Request[Task](Method.GET, uri"https://statsapi.web.nhl.com/api/v1/teams/25")
    val s   = jsonStream(req)
    s.map{
      c => {
        println(s"${c}")
        c
      }
    }
  }

  /** Compile our stream down to an effect to make it runnable */
  def run: Task[Unit] =
    Stream.resource(Blocker[Task]).flatMap { blocker =>
      stream(blocker)
    }.compile.drain
}

object TWStreamApp extends zio.App {

  def fork: ZIO[Any, Throwable, ExitCode] = {
    Task.concurrentEffectWith { implicit CE =>
      (new TWStream[Task]).run.as(ExitCode.Success)
    }
  }

  override def run(args: List[String]): ZIO[Console, Nothing, Int] =
    (for {
      _ <- fork //Runs the http4s example
    } yield ()).foldM(err => putStrLn(s"Failed with $err").as(1), _ => Task.succeed(0))
}
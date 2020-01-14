import org.http4s._
import org.http4s.client.blaze._
import org.http4s.client.oauth1
import org.http4s.implicits._
import cats.effect._
import cats.implicits._
import fs2.{Pipe, Stream}
import fs2.io.stdout
import fs2.text.{lines, utf8Encode}
import io.circe.Json
import jawnfs2._
import java.util.concurrent.{ExecutorService, Executors}

import zio.console.{Console, putStrLn}
import zio.{Task, ZIO}
import zio.interop.catz._

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.global

class TWStream[Task[_]: ConcurrentEffect : ContextShift] {
  // jawn-fs2 needs to know what JSON AST you want
  implicit val f = new io.circe.jawn.CirceSupportParser(None, false).facade

  /* These values are created by a Twitter developer web app.
   * OAuth signing is an effect due to generating a nonce for each `Request`.

  def sign(consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
          (req: Request[F]): F[Request[F]] = {
    val consumer = oauth1.Consumer(consumerKey, consumerSecret)
    val token    = oauth1.Token(accessToken, accessSecret)
    oauth1.signRequest(req, consumer, callback = None, verifier = None, token = Some(token))
  }

   */

  /* Create a http client, sign the incoming `Request[F]`, stream the `Response[IO]`, and
   * `parseJsonStream` the `Response[F]`.
   * `sign` returns a `F`, so we need to `Stream.eval` it to use a for-comprehension.
   */
  def jsonStream(req: Request[Task]): Stream[Task, Json] =
    for {
      client <- BlazeClientBuilder(global).stream
     // sr  <- Stream.eval(sireq)
      res <- client.stream(req).flatMap(_.body.chunks.parseJsonStream)
    } yield res

  /* Stream the sample statuses.
   * Plug in your four Twitter API values here.
   * We map over the Circe `Json` objects to pretty-print them with `spaces2`.
   * Then we `to` them to fs2's `lines` and then to `stdout` `Sink` to print them.
   */
  def stream(blocker: Blocker): Stream[Task, Unit] = {
    val req = Request[Task](Method.GET, uri"https://statsapi.web.nhl.com/api/v1/teams")
    val s   = jsonStream(req)
    s.map {
      _.spaces2
    }.through(lines).through(utf8Encode).through(stdout(blocker))
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
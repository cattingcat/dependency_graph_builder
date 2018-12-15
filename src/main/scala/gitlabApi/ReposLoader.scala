package gitlabApi

import cats.SemigroupK
import cats.effect.ConcurrentEffect
import cats.implicits._
import io.circe.Decoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext


object ReposLoader {
  def loadRepos[F[_], I](host: String, token: String)(implicit ec: ExecutionContext, ce: ConcurrentEffect[F], d: Decoder[I]): F[List[I]] = {
    val builder = BlazeClientBuilder[F](ec)

    def loadPages(client: Client[F], page: Int = 1): F[List[I]] = {
      val uri = s"$host/api/v4/projects?private_token=$token&per_page=100&page=$page&simple=true"
      val f = client.expect[List[I]](uri)
      ce.flatMap(f){a =>
        if(a.length < 100) ce.pure(a)
        else combineE(ce.pure(a), loadPages(client, page + 1))
      }
    }

    builder.resource.use { loadPages(_) }
  }

  private def combineE[F[_], A[_], B](a: F[A[B]], b: F[A[B]])(implicit ce: ConcurrentEffect[F], m: SemigroupK[A]): F[A[B]] = {
    a.flatMap((ar: A[B]) => b.flatMap((br: A[B]) => ce.pure(ar <+> br)))
  }
}

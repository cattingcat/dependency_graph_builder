package gitlabApi

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import cats.effect.ConcurrentEffect
import cats.implicits._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.{EntityDecoder, Status}

import scala.concurrent.ExecutionContext


object FileLoader {
  def loadRaw[F[_]](host: String, token: String, projectId: String, filePath: String)(implicit ec: ExecutionContext, ce: ConcurrentEffect[F]): F[Option[Array[Byte]]] = {
    val enc: String => String = URLEncoder.encode(_, StandardCharsets.UTF_8.name)
    val p = s"$host/api/v4/projects/${enc(projectId)}/repository/files/${enc(filePath)}/raw?ref=master&private_token=$token"
    val b = BlazeClientBuilder[F](ec)

    b.resource.use { c =>
      c.get(p)(r => if(r.status == Status.Ok) {
        val arr = EntityDecoder.byteArrayDecoder.decode(r, strict = false)
        arr.value.map({
          case Right(d) => Some(d)
          case _ => None
        })

      } else {
        ce.pure(None)
      })
    }
  }
}

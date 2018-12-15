package gitlabApi.pubspec

import cats.effect.ConcurrentEffect
import gitlabApi.FileLoader
import io.circe.yaml.{parser => yamlParser}
import io.circe.{Decoder, Json}

import scala.concurrent.ExecutionContext

object PubspecLoader {
  import FileLoader._

  def loadRawPubspec[F[_]](host: String, token: String, projectId: String, filePath: String = "pubspec.yaml")(implicit ec: ExecutionContext, ce: ConcurrentEffect[F]): F[Option[Json]] = {
   val f = loadRaw[F](host, projectId, token, filePath)
    ce.map(f)({
      case Some(arr) =>
        val s = new String(arr)
        val r = yamlParser.parse(s)
        r match {
          case Right(value) => Some(value)
          case _ => None
        }
      case _ => None
    })
  }

  def loadPubspec[F[_], O](host: String, token: String, projectId: String, filePath: String = "pubspec.yaml")(implicit ec: ExecutionContext, ce: ConcurrentEffect[F], d: Decoder[O]): F[Option[O]] = {
    val f = loadRawPubspec[F](host, projectId, token)
    ce.map(f)({
      case Some(json) => json.as[O] match {
        case Right(o) => Some(o)
        case _ => None
      }
      case _ => None
    })
  }
}

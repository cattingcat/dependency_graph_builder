package gitlabApi

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class RepoInfo(id: Int, name: String, sshUrl: String, ns: String)

object RepoInfo {
  implicit val repoInfoDecoder: Decoder[RepoInfo] = Decoder.forProduct4(
    "id",
    "name",
    "ssh_url_to_repo",
    "path_with_namespace")(RepoInfo.apply)

  implicit val repoInfoEncoder: Encoder[RepoInfo] = deriveEncoder
}

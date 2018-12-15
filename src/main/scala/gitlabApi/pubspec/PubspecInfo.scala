package gitlabApi.pubspec

import io.circe.{Decoder, DecodingFailure, HCursor}


case class PubspecInfo(name: String, version: String, deps: List[PubspecDependency])

object PubspecInfo {
  implicit val pubspecInfoDecoder: Decoder[PubspecInfo] = (c: HCursor) => {
    val projName = c.downField("name").as[String]
    val projVer = c.downField("version").as[String]
    val deps = c.downField("dependencies")

    (projName, projVer, deps.keys) match {
      case (Right(n), Right(v), Some(d)) =>
        val depsList = d
          .map(depName => (depName, deps.downField(depName).as[String]))
          .toList
          .flatMap({
            case (name, Right(ver)) => List(PubspecDependency(name, ver))
            case _ => Nil
          })

        Right(PubspecInfo(n, v, depsList))

      case _ => Left(DecodingFailure("invalid json format", List()))
    }
  }
}
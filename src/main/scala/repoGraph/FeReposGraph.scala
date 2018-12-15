package repoGraph

import cats.effect._
import cats.implicits._

import gitlabApi.RepoInfo
import gitlabApi.ReposLoader.loadRepos
import gitlabApi.pubspec.PubspecInfo
import gitlabApi.pubspec.PubspecLoader.loadPubspec
import graph.{Edge, Graph}

import scala.concurrent.ExecutionContext.Implicits.global

object FeReposGraph {
  import Config._

  val pubspecPath = "pubspec.yaml"

  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val t: Timer[IO] = IO.timer(global)

  def load: IO[Graph[String, String]] = {
    val ioRepoInfos: IO[List[RepoInfo]] = for (
      allRepos <- loadRepos[IO, RepoInfo](host, token);
      feRepos = allRepos.filter(_.ns.contains("frontend"));
      _ <- IO {
        feRepos.foreach(r => println(r))
      }
    ) yield feRepos


    val ioPubspecs: IO[List[(RepoInfo, Option[PubspecInfo])]] = for (
      repos <- ioRepoInfos;
      pubspecReqs <- repos.map(repo => loadPubspec[IO, PubspecInfo](host, token, repo.ns, pubspecPath)).sequence[IO, Option[PubspecInfo]];
      _ <- IO {
        println(s"loaded pubspecs count: ${pubspecReqs.length}")
      };
      _ <- IO {
        pubspecReqs.foreach(println)
      };
      t = repos.zip(pubspecReqs)
    ) yield t


    val ioGrpah = for (
      pubspecs <- ioPubspecs;
      g = buildFullDependencyGraph(pubspecs)
    ) yield g

    ioGrpah
  }


  private def buildDependencyGraph(pubspecs: List[(RepoInfo, Option[PubspecInfo])]): Graph[String, String] = {
    val nodes = pubspecs.flatMap({
      case (_, Some(info)) => List(info.name)
      case _ => List()
    }).toSet

    val edges = pubspecs.flatMap({
      case (_, Some(info)) =>
        info.deps
          .filter(d => nodes.contains(d.name))
          .map(d => Edge(info.name, d.name, ""))
      case _ => List()
    }).toSet

    Graph(nodes, edges)
  }

  private def buildFullDependencyGraph(pubspecs: List[(RepoInfo, Option[PubspecInfo])]): Graph[String, String] = {
    val nodes = pubspecs.flatMap({
      case (_, Some(info)) => List(info.name) ::: info.deps.map(_.name)
      case _ => List()
    }).toSet

    val edges = pubspecs.flatMap({
      case (_, Some(info)) =>
        info.deps.map(d => Edge(info.name, d.name, ""))
      case _ => List()
    }).toSet

    Graph(nodes, edges)
  }
}

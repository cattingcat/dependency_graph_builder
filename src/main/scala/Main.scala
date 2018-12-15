import java.io.File
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Calendar

import cats.effect._
import graph.rw.GraphReaderWriter
import repoGraph.FeReposGraph


object Main {
  val home = System.getProperty("user.home")
  val dir = Paths.get(home, "Documents", "depgr")

  def main(args: Array[String]): Unit = {
    val file = createFile

    if(file.createNewFile()) {
      import GraphReaderWriter.write
      import graph.binser.GraphSer._

      val ioGraph = FeReposGraph.load
      val io = for (
        g <- ioGraph;
        _ <- IO { write(file, g) }
      ) yield g

      val g = io.unsafeRunSync()
      println(g)

    } else {
      import graph.binser.GraphSer._
      import graph.rw.GraphReaderWriter._

      val g = read[String, String](file)
      println(g)
    }
  }


  def createFile: File = {
    val format = new SimpleDateFormat("dd-MMM-yyyy")
    val date = Calendar.getInstance.getTime
    val dateStr = format.format(date)
    val outFilePath = Paths.get(dir.toString, s"$dateStr-graph").toString
    val file = new File(outFilePath)
    file.getParentFile.mkdirs()
    file
  }
}

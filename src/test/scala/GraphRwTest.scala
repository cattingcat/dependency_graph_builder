import java.io.File

import graph._

object GraphRwTest {
  import graph.rw.GraphReaderWriter._
  import graph.binser.GraphSer._

  def main(args: Array[String]): Unit = {
    val file = File.createTempFile("graph-rw", "graph")
    val path = file.getAbsolutePath
    val g = Graph(Set(1, 2, 3), Set(Edge(1, 2, "qweqweqw"), Edge(2, 3, "asdasdasd")))

    write(file, g)
    val gg = read[Int, String](file)

    println(s"graphFile ser-deser: ${g == gg}")
    println(s"file path: $path")
  }
}
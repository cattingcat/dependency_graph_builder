import graph._

object GraphWriterTest {
  def main(args: Array[String]): Unit = {
    import graph.binser._
    import graph.binser.GraphSer._

    val i = 1200
    val r1 = i.bin
    val j = r1.unbin[Int]
    println(s"int test: ${i == j}")


    val s = "qweqwe"
    val r2 = s.bin
    val k = r2.unbin[String]
    println(s"string test: ${s == k}")


    val g = Graph(Set(1, 2, 3), Set(Edge(1, 2, "qweqweqw"), Edge(2, 3, "asdasdasd")))
    val r3 = g.bin
    val h = r3.unbin[Graph[Int, String]]
    println(s"graphs test: ${g == h}")


    println("!")
  }
}
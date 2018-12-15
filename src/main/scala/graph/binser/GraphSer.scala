package graph.binser

import java.nio.ByteBuffer

import graph.{Edge, Graph}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object GraphSer {
  implicit def graphBinaryfier[T, M](implicit bt: Binaryfier[T], bm: Binaryfier[M]): Binaryfier[Graph[T, M]] = new Binaryfier[Graph[T, M]] {
    override def bin(g: Graph[T, M]): Array[Byte] = {
      val headerSize = 10
      val m = mutable.HashMap[T, Int]()
      val b = ArrayBuffer[Byte]()


      var offset = headerSize // reserve first 10 bytes

      // Nodes:   |bytes|
      g.nodes.foreach(n => {
        val bytes = bt.bin(n)
        b.appendAll(bytes)
        m.put(n, offset)
        offset += bytes.length
      })

      val offsetBuf = ByteBuffer.allocate(4)
      offsetBuf.putInt(offset)
      val offsetArr = offsetBuf.array()
      val header = Array.fill[Byte](headerSize)(0)
      offsetArr.copyToArray(header, 0)

      b.prependAll(header)

      // Edges:   |offsetA|offsetB|bytes meta|
      g.edges.foreach(e => {
        val aOffset = m(e.a)
        val bOffset = m(e.b)
        val bytes = bm.bin(e.meta)

        val buf = ByteBuffer.allocate(8)
        buf.putInt(aOffset)
        buf.putInt(bOffset)

        b.appendAll(buf.array())
        b.appendAll(bytes)
      })

      b.toArray
    }
  }

  implicit def graphUnbinaryfier[T, M](implicit bt: Unbinaryfier[T], bm: Unbinaryfier[M]): Unbinaryfier[Graph[T, M]] = new Unbinaryfier[Graph[T, M]] {
    import Unbinaryfier._

    def unbin(b: Array[Byte], offset: Int): (Graph[T, M], Int) = {
      val m = mutable.HashMap[Int, T]()
      val headerSz = 10
      val arr = b.slice(offset, b.length)
      val header = arr.take(headerSz)
      val (edgesOffset, _) = intUnbinaryfier.unbin(header.take(4), 0)

      val nodes = new mutable.HashSet[T]()
      var off = headerSz
      while(off < edgesOffset) {
        val (r, l) = bt.unbin(b, off)
        nodes.add(r)

        m.put(off, r)
        off += l
      }

      off = edgesOffset

      val edges = new mutable.HashSet[Edge[T, M]]()
      while(off < arr.length) {
        // TODO: Replace with unbinaryfier
        val nodeOffsetsArr = arr.slice(off, off + 8)
        val nodeOffsets = ByteBuffer.wrap(nodeOffsetsArr).asIntBuffer()
        val from = m(nodeOffsets.get(0))
        val to = m(nodeOffsets.get(1))
        val (meta, l) = bm.unbin(arr, off + 8)
        edges.add(Edge(from, to, meta))

        off += (8 + l)
      }

      (Graph[T, M](nodes.toSet, edges.toSet), off)
    }
  }
}

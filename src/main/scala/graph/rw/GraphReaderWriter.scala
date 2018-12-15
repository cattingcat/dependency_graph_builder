package graph.rw

import java.io._

import graph.Graph
import graph.binser.{Binaryfier, Unbinaryfier}

import scala.collection.mutable.ArrayBuffer

object GraphReaderWriter {
  def write[T, M](file: File, g: Graph[T, M])(implicit bt: Binaryfier[Graph[T, M]]): Unit = {
    val writer = new FileOutputStream(file)
    try {
      val bytes = bt.bin(g)
      writer.write(bytes)
    } finally {
      writer.close()
    }
  }

  def read[T, M](file: File)(implicit ut: Unbinaryfier[Graph[T, M]]): Graph[T, M] = {
    val reader = new FileInputStream(file)
    try {
      //val buf = ArrayBuffer[Byte]

      val chunkSz = 1024 * 1024 * 5 // 5mb

      val arr = Array.fill[Byte](chunkSz)(0)
      val readBytes = reader.read(arr)

      if(readBytes < chunkSz) {
        val (g, _) = ut.unbin(arr.slice(0, readBytes), 0)
        g
      } else {
        ???
      }

    } finally {
      reader.close()
    }
  }
}

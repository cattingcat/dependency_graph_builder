package graph.binser

import java.nio.ByteBuffer
import java.nio.charset.Charset

import scala.collection.mutable.ArrayBuffer

trait Binaryfier[T] {
  def bin(a: T): Array[Byte]
}

object Binaryfier {
  implicit val intBinaryfier: Binaryfier[Int] = new Binaryfier[Int] {
    def bin(a: Int): Array[Byte] = {
      val buf = ByteBuffer.allocate(4)
      buf.putInt(a)
      buf.array()
    }
  }

  implicit val stringBinaryfier: Binaryfier[String] = new Binaryfier[String] {
    def bin(a: String): Array[Byte] = {
      val b = ArrayBuffer[Byte]()

      val charset = Charset.forName("UTF-8")
      val bytes = a.getBytes(charset)

      val buf = ByteBuffer.allocate(4)
      buf.putInt(bytes.length)
      val bufArr = buf.array()

      b.appendAll(bufArr)
      b.appendAll(bytes)

      b.toArray
    }
  }
}
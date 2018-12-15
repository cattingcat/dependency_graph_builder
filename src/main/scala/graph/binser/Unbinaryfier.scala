package graph.binser

import java.nio.ByteBuffer
import java.nio.charset.Charset

trait Unbinaryfier[T] {
  def unbin(b: Array[Byte], offset: Int): (T, Int)
}


object Unbinaryfier {
  implicit val intUnbinaryfier: Unbinaryfier[Int] = new Unbinaryfier[Int] {
    def unbin(b: Array[Byte], offset: Int): (Int, Int) = {
      val arr = b.slice(offset, offset + 4)
      (ByteBuffer.wrap(arr).getInt, 4)
    }
  }

  implicit val stringUnbinaryfier: Unbinaryfier[String] = new Unbinaryfier[String] {
    def unbin(b: Array[Byte], offset: Int): (String, Int) = {
      val charset = Charset.forName("UTF-8")
      val intBytes = b.slice(offset, offset + 4)
      val strLen = ByteBuffer.wrap(intBytes).getInt
      val strBytes = b.slice(offset + 4, offset + 4 + strLen)
      (new String(strBytes, charset), 4 + strBytes.length)
    }
  }
}
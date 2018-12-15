package graph

package object binser {
  implicit class ByteArrOps(a: Array[Byte]) {
    def unbin[T](implicit tu: Unbinaryfier[T]): T = tu.unbin(a, 0)._1
  }

  implicit class BinOps[T](a: T) {
    def bin(implicit tb: Binaryfier[T]): Array[Byte] = tb.bin(a)
  }

  def bin[T](a: T)(implicit tb: Binaryfier[T]): Array[Byte] = tb.bin(a)
}

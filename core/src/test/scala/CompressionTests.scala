package test

import f2m.{Repeat, RLECompression, Single}
import org.scalatest.FunSuite
import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object RLESpecification extends Properties("RLE") {

  property("decompress of compress equals to input") = forAll { (a: List[Int]) =>
    RLECompression.decompress(RLECompression.compress(a)) == a
  }

  property("compress doesn't bigger than input") = forAll { (a: List[Int]) =>
    RLECompression.compress(a).size <= a.size
  }

  property("compress doesn't contain 2 items with the same inner element") = forAll { (a: List[Int]) =>
    val compress = RLECompression.compress(a)

    val items = compress.map(x => x.element)
    val consequetiveItemsDoesntEqual = for (i <- 0 until items.size - 1) yield {
      items(i) != items(i + 1)
    }

    consequetiveItemsDoesntEqual.forall(x => x)
  }
}

class CompressionTests extends FunSuite {
  test("CompressionTest - 1") {
    val res = RLECompression.compress(List())
    assert(res == List())
  }

  test("CompressionTest - 2") {
    val res = RLECompression.compress(List(1))
    assert(res == List(Single(1)))
  }

  test("CompressionTest - 3") {
    val res = RLECompression.compress(List(1, 2))
    assert(res == List(Single(1), Single(2)))
  }

  test("CompressionTest - 4") {
    val res = RLECompression.compress(List(1, 2, 2))
    assert(res == List(Single(1), Repeat(2, 2)))
  }

  test("CompressionTest - 5") {
    val res = RLECompression.compress(List(1, 1, 2, 2))
    assert(res == List(Repeat(2, 1), Repeat(2, 2)))
  }

  test("CompressionTest - 6") {
    val res = RLECompression.compress(List(1, 1, 3, 2, 2, 3, 3, 3))
    assert(res == List(Repeat(2, 1), Single(3), Repeat(2, 2), Repeat(3, 3)))
  }
}

package f2m

/**
  * Basic compression trait
  */
trait Compression {
  def compress[A](values: Seq[A]): Seq[Compressed[A]]

  def decompress[A](values: Seq[Compressed[A]]): Seq[A]
}

/**
  * Marker trait for Compression classes
  *
  * @tparam A type
  */
sealed trait Compressed[+A] {
  def element: A
}

/**
  * Single compressed item
  *
  * @param element item
  * @tparam A type
  */
case class Single[A](element: A) extends Compressed[A]

/**
  * Repeat compressed item
  *
  * @param count   count
  * @param element item
  * @tparam A type
  */
case class Repeat[A](count: Int, element: A) extends Compressed[A]

/**
  * Implementation for RLE compression
  */
object RLECompression extends Compression {
  type State[A] = (Option[Compressed[A]], Seq[Compressed[A]])

  override def compress[A](values: Seq[A]): Seq[Compressed[A]] = {
    val res: (Option[Compressed[A]], Seq[Compressed[A]]) = values.foldLeft[State[A]]((None, Nil))(fold)

    res._1.map(tail => res._2 :+ tail).getOrElse(List())
  }

  def fold[A](state: State[A], value: A): State[A] = {
    state match {
      case (None, seq) => (Some(Single(value)), seq)
      case (Some(Single(item)), seq) if item == value => (Some(Repeat(2, value)), seq)
      case (Some(x@Single(_)), seq) => (Some(Single(value)), seq :+ x)
      case (Some(Repeat(count, item)), seq) if item == value => (Some(Repeat(count + 1, item)), seq)
      case (Some(x@Repeat(_, _)), seq) => (Some(Single(value)), seq :+ x)
    }
  }

  override def decompress[A](values: Seq[Compressed[A]]): Seq[A] = values flatMap {
    case Single(a) => List(a)
    case Repeat(count, a) => List.fill(count)(a)
  }
}

import org.scalatest.FunSuite

class SequenceQuestionsTest extends FunSuite {

  /**
    * Increments by 1 the number represented by the Sequence.
    *
    * If an Int in the Seq is greater than 9, it counts as 9.
    * e.g. Seq(9,9,10) produces the same result with Seq(9,9,9)
    *
    * Negative numbers are not considered in the scope of this task
    *
    * @param number
    * @return
    */
  def incrementByOne(number: Seq[Int]): Seq[Int] = {

    def helper(n: Seq[Int], acc: Seq[Int]): Seq[Int] = n match {
      case x :: xs if (x >= 9) => helper(xs, acc :+ 0)
      case x :: xs             => ((x + 1) +: xs).reverse ++ acc
      case Nil                 => 1 +: acc
    }

    number match {
      case Nil => Nil
      case _   => helper(number.reverse, Seq.empty)
    }
  }


  test("increment by 1 - Nil") {
    assert(incrementByOne(Nil) == Nil)
  }

  test("increment by 1 - Seq(0)") {
    assert(incrementByOne(Seq(0)) == Seq(1))
  }

  test("increment by 1 - Seq(1, 2, 3)") {
    assert(incrementByOne(Seq(1, 2, 3)) == Seq(1, 2, 4))
  }

  test("increment by 1 - Seq(9, 9, 9)") {
    assert(incrementByOne(Seq(9, 9, 9)) == Seq(1, 0, 0, 0))
  }
}

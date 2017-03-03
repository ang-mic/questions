import org.scalatest.FunSuite

class OptionQuestionsTest extends FunSuite {

  /**
    * Use 'map' to access the content of the Option
    */
  test("Provide 2 examples of how you can safely use the string in this option - EXAMPLE 1") {
    val myStringOption = Some("test")
    myStringOption.map(s => assert(s == "test"))
  }

  /**
    * Use 'if - isDefined' to access the content of the Option
    */
  test("how you can safely use the string in this option - EXAMPLE 2") {
    val myStringOption = Some("test")
    if (myStringOption.isDefined) assert(myStringOption.get == "test")
    else fail
  }

  /**
    * Use 'getOrElse' to access the content of the Option
    */
  test("how you can safely use the string in this option - EXAMPLE 3") {
    val myStringOption = Some("test")
    assert("test" == myStringOption.getOrElse("fail test"))
  }

  /**
    * Use 'pattern matching' to access the content of the Option
    */
  test("how you can safely use the string in this option - EXAMPLE 4") {
    val myStringOption: Option[String] = Some("test")
    val myString = myStringOption match {
      case Some(s) => s
      case None    => "fail test"
    }

    assert("test" == myString)
  }
}

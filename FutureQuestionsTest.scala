package onTrackRetail

import org.scalatest.FunSuite
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

/**
  * The purpose of the tests in this class is to demonstrate functionality and not to test it
  */
class FutureQuestionsTest extends FunSuite with ScalaFutures {

  implicit val ec = ExecutionContext.global

  def f1: Future[Unit] = Future {println("f1: DONE")}
  def f2: Future[Unit] = Future {throw new Exception("f2 failed")}
  def f3: Future[Unit] = Future {println("f3: DONE")}
  def f4: Future[Unit] = Future {println("f4: DONE")}

  /**
    * Just call the methods. The order does not matter since the methods are non-blocking no blocking
    */
  test("No dependencies between the functions - EXAMPLE 1") {
    f1 recover {case ex => println(s"Failure ${ex.getMessage}")}
    f2 recover {case ex => println(s"Failure ${ex.getMessage}")}
    f3 recover {case ex => println(s"Failure ${ex.getMessage}")}
    f4 recover {case ex => println(s"Failure ${ex.getMessage}")}
  }

  /**
    * In this example, the futures are executed in parallel but if there is failure, we handle the error. Doesn't
    * make much sense to collect the results in this example because the methods return Unit. The order does not matter
    */
  test("No dependencies between the functions - EXAMPLE 2") {
    Future.sequence(Seq(f1, f2, f3, f4)) map { _ =>
      println("Success")
    } recover { case ex =>
      println(s"Failure ${ex.getMessage}")
    }
  }

  /**
    * Use 'map - flatMap' to access the result of each future sequentially while the whole operation is non-blocking.
    * This is an nice approach but it becomes messy and difficult to read when there is too much logic in each step.
    *
    * Also, we can use just 'map' and use the 'flatten' method introduced in 2.12
    */
  test("f4 depends on f3 which depends on f2 which depends on f1 - EXAMPLE 1") {
    f1 flatMap { _ =>
      // f1 result can be used here
      f2 flatMap { _ =>
        // f2 result can be used here
        f3 flatMap { _ =>
          // f3 result can be used here
          f4 map { _ =>
            println("Success")
          }
        }
      }
    } recover { case ex =>
      println(s"Failure ${ex.getMessage}")
    }
  }

  /**
    * Use 'for-comprehensions' to access the result of each future sequentially while the whole operation is
    * non-blocking. Easy to ready.
    */
  test("f4 depends on f3 which depends on f2 which depends on f1 - EXAMPLE 2") {
    val f = for {
      result1 <- f1
      result2 <- f2 // f1 result can be used here
      result3 <- f3 // f2 result can be used here
      result4 <- f4 // f3 result can be used here
    } yield println("Success")

    f recover {case ex => println(s"Failure ${ex.getMessage}")}
  }

  /**
    * Use 'onComplete' to access the result of each future sequentially while the whole operation is
    * non-blocking. Too much writing and it returns Unit, however I am sure it has its use cases.
    */
  test("f4 depends on f3 which depends on f2 which depends on f1 - EXAMPLE 3") {
    f1 onComplete {
      case Success(res1) => f2 onComplete {
        // res1 can be used here
        case Success(res2) => f3 onComplete {
          // res2 can be used here
          case Success(res3) => f4 onComplete {
            // res3 can be used here
            case Success(res4) => println("Success")
            case Failure(ex)   => println(s"Failure ${ex.getMessage}")
          }
          case Failure(ex)   => println(s"Failure ${ex.getMessage}")
        }
        case Failure(ex)   => println(s"Failure ${ex.getMessage}")
      }
      case Failure(ex)   => println(s"Failure ${ex.getMessage}")
    }
  }

  /**
    * Use 'zipWith' to combine the results of two futures. When the result of f1 is available, we combine the f2 with
    * f3. When the combinations is available we execute the f4 to produce the final result
    */
  test("f4 depends on f3 and f2, and f3 and f2 both depend on f1 - EXAMPLE 1") {
    val f = f1 flatMap { res1 =>
      // res1 can be used here
      f2.zipWith(f3) { (res2, res3) =>
        // res2 and res3 can be used here
        f4.map(res4 => println("Success"))
      } flatten
    }

    f recover { case ex => println(s"Failure ${ex.getMessage}") }
  }

  /**
    * Use 'zip - map' to combine the results of two futures. When the result of f1 is available, we combine the f2 with
    * f3. When the combinations is available we execute the f4 to produce the final result
    */
  test("f4 depends on f3 and f2, and f3 and f2 both depend on f1 - EXAMPLE 2") {
    val f = f1 flatMap { res1 =>
      // res1 can be used here
      f2.zip(f3) map {case (res2, res3) =>
        // res2 and res3 can be used here
        f4.map(res4 => println("Success"))
      } flatten
    }

    f recover { case ex => println(s"Failure ${ex.getMessage}") }
  }
}

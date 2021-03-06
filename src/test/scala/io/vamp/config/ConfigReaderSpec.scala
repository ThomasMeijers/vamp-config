package io.vamp.config

import org.scalatest._
import scala.concurrent.duration.{FiniteDuration, MILLISECONDS, SECONDS}
import io.vamp.config.ConfigReader._
import cats.data.Validated._
import com.typesafe.config.ConfigFactory

import scala.util.{Left, Right}

class ConfigReaderSpec extends FlatSpec with Matchers {

  "A ConfigReader" should "Read a value of type String" in {
    ConfigReader.stringConfigReader.read("string.test").shouldBe(valid("string-test"))
  }

  it should "Read a value of type List[String]" in {
    ConfigReader.stringListConfigReader.read("string.list").shouldEqual(valid(List("1", "2", "3")))
  }

  it should "Read a value of type Boolean" in {
    ConfigReader.booleanConfigReader.read("boolean.test").shouldBe(valid(true))
  }

  it should "Read a value of type List[Boolean]" in {
    ConfigReader.booleanListConfigReader.read("boolean.list").shouldBe(valid(List(true, true, false, false)))
  }

  it should "Read a value of type Int" in {
    ConfigReader.intConfigReader.read("int.test").shouldBe(valid(7))
  }

  it should "Read a value of type List[Int]" in {
    ConfigReader.intListConfigReader.read("int.list").shouldBe(valid(List(1, 2, 3)))
  }

  it should "Read a value of type Double" in {
    ConfigReader.doubleConfigReader.read("double.test").shouldBe(valid(7.77))
  }

  it should "Read a value of type List[Double]" in {
    ConfigReader.doubleListConfigReader.read("double.list").shouldBe(valid(List(1.1, 2.2, 3.3)))
  }

  it should "Read a value of type Long" in {
    ConfigReader.longConfigReader.read("long.test").shouldBe(valid(7.toLong))
  }

  it should "Read a value of type List[Long]" in {
    ConfigReader.longListConfigReader.read("long.list").shouldBe(valid(List[Long](1L, 2L, 3L)))
  }

  it should "Read a value of type FiniteDuration" in {
    ConfigReader.durationConfigReader
      .read("duration.test")
      .shouldBe(valid(FiniteDuration(1, MILLISECONDS)))
  }

  it should "Read a value of type Option[FiniteDuration]" in {
    ConfigReader.durationListConfigReader
      .read("duration.list")
      .shouldBe(valid(List(FiniteDuration(1, MILLISECONDS), FiniteDuration(2, MILLISECONDS))))
  }

  it should "Read a value of type Option[A], for Some of any A" in {
    ConfigReader
      .optionalConfigReader[String].read("option.test")
      .shouldBe(valid(Some("exists")))
  }

  it should "Read a value of type Option[A], for None of any A" in {
    ConfigReader
      .optionalConfigReader[String].read("none.test")
      .shouldBe(valid(None))
  }

  it should "Read any case class that has it data types defined" in {
    case class ConfigTest(test: String, dude: Boolean)

    val outcome = ConfigTest("test", true)

    val config = ConfigReader[ConfigTest].read("config")

    config.shouldEqual(valid(outcome))
  }

  it should "Read a cameled cased field as a dash split identifier" in {
    case class Split(splitIdentifier: String)

    Config.read[Split]("string").shouldEqual(Right(Split("split-identifier")))
  }

  it should "Read a cameled cased field as a underscore split identifier" in {
    case class SplitUnder(splitIdentifier: String)

    implicit val configSettings: ConfigSettings = new ConfigSettings {

      override val config = ConfigFactory.load()

      override val timeUnit = MILLISECONDS

      override val separator = "_"
    }

    Config.read[SplitUnder]("string").shouldEqual(Right(SplitUnder("split_identifier")))
  }

  it should "Read a double nested object with correct path" in {
    case class SqlExample(user: String, password: String, connection: Connection, timeOut: FiniteDuration)
    case class Connection(url: String)

    val sqlExample = SqlExample("vamp", "pswd", Connection("jdbc://urlto:1000"), FiniteDuration(3, SECONDS))

    Config.read[SqlExample]("vamp.persistence.database").shouldEqual(Right(sqlExample))
  }

  it should "Read a coproduct / adt that have different parse possibilities" in {
//    sealed trait DriverConfig
//    case class MarathonConfig(url: String, port: Int, streamEnabled: Boolean) extends DriverConfig
//    case class KubernetesConfig(url: String, timeOut: FiniteDuration) extends DriverConfig
//    case class RancherConfig(url: String, user: String, password: String) extends DriverConfig
//
//    val kubeConfig = KubernetesConfig("http://kube", FiniteDuration(1, SECONDS))
//    val marathonConfig = MarathonConfig("http", 9090, streamEnabled = true)
//
//    val result: Either[NonEmptyList[String], DriverConfig] = Config.read[DriverConfig]("vamp.driver.config")
//
//    result match {
//      case Left(value) => value.toList.foreach(println)
//      case Right(value) => println(value)
//    }
//    result.shouldEqual(Right(marathonConfig))
  }

}

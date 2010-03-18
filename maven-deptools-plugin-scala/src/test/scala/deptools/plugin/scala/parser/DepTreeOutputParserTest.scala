package deptools.plugin.scala.parser

import org.junit.Test
import deptools.plugin.scala.utils.{MyLogger, File2QueueReader}
import java.lang.String
import org.apache.maven.plugin.MojoFailureException

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 15.mar.2010
 * Time: 21:11:48
 * To change this template use File | Settings | File Templates.
 */

@Test
class DepTreeOutputParserTest {
  object MyTestLogger extends MyLogger {
    def debug(msg: String) = println("debug: "+msg)
    def info(msg: String) = println("INFO: " + msg)    
    def error(msg: String) = println("ERROR: "+ msg)
  }

  @Test{ val expected = classOf[ MojoFailureException] }
  def testParsing() {
    val filename = "src/test/resources/dep_tree2.txt"
    println("testing " + filename)
    new DepTreeOutputParser(MyTestLogger).parse(File2QueueReader.readFile(filename))
  }

  @Test{ val expected = classOf[ MojoFailureException] }
  def testParsing2() {
    val filename = "src/test/resources/dependency_tree.txt"
    println("testing " + filename)
    new DepTreeOutputParser(MyTestLogger).parse(File2QueueReader.readFile(filename))
  }

  @Test
  def testParsing3() {
    val filename = "src/test/resources/dependency_tree2.txt"
    println("testing " + filename)
    new DepTreeOutputParser(MyTestLogger).parse(File2QueueReader.readFile(filename))
  }

  @Test{ val expected = classOf[ MojoFailureException] }
  def testParsing4() {
    val filename = "src/test/resources/dependency_tree3.txt"
    println("testing " + filename)
    new DepTreeOutputParser(MyTestLogger).parse(File2QueueReader.readFile(filename))
  }

}
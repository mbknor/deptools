package deptools.plugin.scala.parser

import org.junit.Test
import deptools.plugin.scala.utils.File2QueueReader

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 15.mar.2010
 * Time: 21:11:48
 * To change this template use File | Settings | File Templates.
 */

@Test
class DepTreeOutputParserTest{

  @Test
  def testParsing(){
    DepTreeOutputParser.parse( File2QueueReader.readFile("maven-deptools-plugin-scala/src/test/resources/dep_tree2.txt") )
  }

  @Test
  def testParsing2(){
    DepTreeOutputParser.parse( File2QueueReader.readFile("maven-deptools-plugin-scala/src/test/resources/dependency_tree.txt") )
  }
  
}
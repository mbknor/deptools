package deptools.plugin.scala

import org.apache.maven.plugin.AbstractMojo

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 12.mar.2010
 * Time: 22:00:01
 * To change this template use File | Settings | File Templates.
 */

class ScalaMojo extends AbstractMojo{

  private var testString : String = null

  def setTestString(v : String) = testString = v
  def getTestString() = testString


  def execute = {
    getLog.info("from scala!!")
    getLog.info("testString: " + testString)

  }
}
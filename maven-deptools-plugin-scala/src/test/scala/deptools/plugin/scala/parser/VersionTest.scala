package deptools.plugin.scala.parser

import org.junit.{Assert, Test}

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 17.mar.2010
 * Time: 21:19:01
 * To change this template use File | Settings | File Templates.
 */

class VersionTest{

  @Test
  def test(){

    Assert.assertEquals( 1,
      new Version("1.1").compareTo(
      new Version("1")))

    

    Assert.assertEquals( 0,
      VersionPartParser.parse("1").compareTo(
      VersionPartParser.parse("1")))

    Assert.assertEquals( -1,
      VersionPartParser.parse("1").compareTo(
      VersionPartParser.parse("2")))

    Assert.assertEquals( 1,
      VersionPartParser.parse("2").compareTo(
      VersionPartParser.parse("1")))

    Assert.assertEquals( -1,
      new Version("1").compareTo(
      new Version("1.1")))

    Assert.assertEquals( 0,
      new Version("1.2.3.4.A").compareTo(
      new Version("1.2.3.4.A")))

    Assert.assertEquals( 1,
      new Version("1.2.3.4.B").compareTo(
      new Version("1.2.3.4.A")))

    Assert.assertEquals( -1,
      new Version("1.2.3.4.A").compareTo(
      new Version("1.2.3.4.B")))

    Assert.assertEquals( 0,
      new Version("1.2-SNAPSHOT").compareTo(
      new Version("1.2-SNAPSHOT")))

    Assert.assertEquals( -1,
      new Version("1.2-SNAPSHOT").compareTo(
      new Version("1.3-SNAPSHOT")))

    Assert.assertEquals( 1,
      new Version("1.3-SNAPSHOT").compareTo(
      new Version("1.2-SNAPSHOT")))
  }
}
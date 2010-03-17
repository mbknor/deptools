package deptools.plugin.scala.parser

import collection.mutable.ListBuffer

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 17.mar.2010
 * Time: 00:01:19
 * To change this template use File | Settings | File Templates.
 */

//TODO: must specialcase on  snaphot-versions

trait VersionPart {
  def compareTo(other : VersionPart) : Int
}

class IntVersionPart(val intValue: Int) extends VersionPart {
  def compareTo(other: VersionPart) : Int = {
    other match {
      case o : IntVersionPart  => return intValue compare o.intValue
      case _ => return 1 //all other than int is larger by def
      
    }
  }
}

class StringVersionPart(val stringValue: String) extends VersionPart {
  def compareTo(other: VersionPart) : Int = {
    other match {
      case o : StringVersionPart => return stringValue compare o.stringValue
      case _ => return -1 //all other than int is smaller by def

    }
  }
}

object VersionPartParser {
  def parse(versionPart: String): VersionPart = {
    try {
      val intValue = versionPart.toInt
      return new IntVersionPart(intValue)
    } catch {
      case _: java.lang.NumberFormatException => None
    }

    //treat everyting else as string
    return new StringVersionPart( versionPart)

  }
}

class Version(version: String) {
  private val parts = new ListBuffer[VersionPart]
  //must parse the versionString into parts devided by .
  version.split("\\:").foreach {
    parts += VersionPartParser.parse(_)
  }

  def compareTo(other : Version ) : Int = {

    val maxCompareCount = Math.min(parts.size, other.parts.size)

    for( i <- 0 until maxCompareCount ){
      val c = parts(i).compareTo( other.parts(i))
      if( c != 0 ) return c
    }
    //all partst to this point have been equal - must decide on partsSize..
    if( other.parts.size > parts.size ) return 1
    if( other.parts.size < parts.size ) return -1

    //equal
    return 0

  }


}
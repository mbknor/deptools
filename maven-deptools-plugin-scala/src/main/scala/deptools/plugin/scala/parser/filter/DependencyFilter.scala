package deptools.plugin.scala.parser.filter

import collection.immutable.Queue
import deptools.plugin.scala.parser.Dependency
import util.matching.Regex

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 23.mar.2010
 * Time: 23:43:58
 * To change this template use File | Settings | File Templates.
 */

trait DependencyFilter{
  /**
   * the regexp is matched against both the string 'groupId:artifactId' for both the dependency with the error,
   * AND for the complete dependency-path that leaded to that faulty dependency.
   * The topmost dependency is not checked against the patterns, since it will always be the project you are building.
   */
  def matches( dependencyPath: Array[Dependency]) : Boolean

  /**
   * returns true if filter is configured and can be used
   */
  def configured : Boolean
}

class DependencyFilterIncludeExcludeImpl(
        includePattern : String,
        excludePattern : String) extends DependencyFilter{


  val include = new DependencyMatcher( includePattern)
  val exclude = new DependencyMatcher( excludePattern)

  def configured = true

  def matches(dependencyPath: Array[Dependency]):Boolean = {
    if( include.configured ){
      if( !include.matches( dependencyPath)){
        return false
      }
    }

    if( exclude.configured ){
      if( exclude.matches(dependencyPath)){
        return false
      }
    }

    return true
  }


  override def toString = "include: " + include + " exclude: "+exclude
}


class DependencyMatcher ( patternString : String ) extends DependencyFilter {

  val pattern = createPattern( patternString)


  override def toString = {
    pattern match {
      case Some(x) => x.toString
      case None => "all"
    }
  }

  private def createPattern( patternString : String ) : Option[Regex] = {
    if( patternString == null ){
      None
    }else{
      
      Some( (patternString).r )
    }
  }


  def configured : Boolean = {
    pattern match {
      case Some(x) => return true
      case None => return false
    }
  }

  def matches(dependencyPath: Array[Dependency]):Boolean = {

    var i = 0
    dependencyPath.foreach{
      dep =>
      if( i > 0 ){
        if( depMatches( dep )){
          return true
        }
      }
      i = i + 1
    }
    return false
  }

  private def depMatches( dep : Dependency ) : Boolean = {
    pattern match {
      case None => false
      case Some(x) => {
        val group_artifact = dep.groupId + ":" + dep.artifactId
        group_artifact match {
          case x() => return true
          case _ => return false
        }
      }
    }


  }

}
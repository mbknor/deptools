package deptools.plugin.scala.parser

import collection.mutable.{Queue, ListBuffer}

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 15.mar.2010
 * Time: 21:05:36
 * To change this template use File | Settings | File Templates.
 */

class Dependency(
        val groupId: String,
        val artifactId: String,
        val packaging: String,
        val version: String,
        val scope: String) {
  override def toString = "dep: groupId: '" + groupId + "' artifactId: '" + artifactId + "' packaging: '" + packaging + "' version: '" + version + "' scope: '" + scope+"'"
}

object DepTreeOutputParser {
  def parse(linesLeft: Queue[String]) {
    parse(linesLeft, new collection.immutable.Queue[Dependency](), 1)
  }

  private def parse(linesLeft: Queue[String], parents: collection.immutable.Queue[Dependency], currentDepthLength: Int) {

    val depthExpression = """([ \\\+\-|]*)- (.+)""".r


    var previousDependency: Dependency = null

    //pop first line from linesLeft
    while (!linesLeft.isEmpty) {
      val line = linesLeft.front
      //(commons-logging:commons-logging:jar:1.1.1:compile - omitted for conflict with 1.0.1)
      line match {
      /*case regularDep(children, gid, aid, t, v, scope) => {
        val dep = new Dependency(gid, aid, t, v, scope)

        println(children + " " + dep)
      }*/
        case depthExpression(depth, depString) => {
          val depthLength = depth.length
          //when depthLength is larger than currentDepthLength, then this is the first
          //child of the previous line..
          //when this happens:
          //   - add the previous dependency to the parents-list
          //   - call this method recursiv

          //if depthLength is smaller than currentDepthLength, the we have gone
          //past the last child.. when this happens:
          //   - return from the method...

          // if depthLength is equal to currentDepthLength
          // pop line from queue and parse it.
          if (depthLength > currentDepthLength) {
            //this is the first child
            //println("child")
            parse(linesLeft, parents.enqueue(previousDependency), depthLength)

          } else if (depthLength < currentDepthLength) {
            //past last child
            //println("past last child")
            return;
          } else {
            //pop the line from the queue
            linesLeft.dequeue;
            var dependency = parseDep(parents, depString)
            println(parents.size + " > " + "dependency: " + dependency)

            previousDependency = dependency;
          }

        }
        case _ => {
          //line did not match any regexp.. just pop the line
          linesLeft.dequeue;
          //println("ignoring line: " + line)
        }
      }
    }

  }

  private def parseDep(parents: collection.immutable.Queue[Dependency], depString: String): Dependency = {
    val artifactExpressionPart = """(.+):(.+):(.+):(.+):(.+)"""

    val okArtifactExpression = (artifactExpressionPart).r
    val errorArtifactExpression = ("\\("+artifactExpressionPart+" - (.+)\\)").r
    val duplicateArtifactExpression = """\(.+ - omitted for duplicate\)""".r


    depString match {
      case errorArtifactExpression( gid, aid, t, v, scope, errorMsg ) => {
        val dependency = new Dependency(gid, aid, t, v, scope)
        handleError( parents, dependency, errorMsg)
        return dependency
      }
      case okArtifactExpression(gid, aid, t, v, scope) => {
        return new Dependency(gid, aid, t, v, scope)
      }
      case _ => {
        throw new Exception("error parsing dependency: " + depString)
      }
    }
  }

  private def handleError( parents: collection.immutable.Queue[Dependency], dependency : Dependency, errorMsg : String) {
    println( "errormsg: "+ errorMsg)
  }
}
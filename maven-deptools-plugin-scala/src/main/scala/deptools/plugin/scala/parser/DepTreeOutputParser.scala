package deptools.plugin.scala.parser

import deptools.plugin.scala.utils.MyLogger
import collection.mutable.{HashMap, Queue, ListBuffer}
import org.apache.maven.plugin.MojoFailureException

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

  def key = groupId + ":" + artifactId


  override def toString = groupId + ":" + artifactId + ":" + ":" + version

  def extendedToString = "dep: groupId: '" + groupId + "' artifactId: '" + artifactId + "' packaging: '" + packaging + "' version: '" + version + "' scope: '" + scope+"'"
}

class DepTreeOutputParser(logger: MyLogger) {

  val dependencies = new HashMap[String, collection.immutable.Queue[Dependency]]



  def parse(linesLeft: Queue[String]) {
    //first parse output and ignore all errors just to build map
    //of all known dependencies...
    parseLines( linesLeft.clone, true )

    //then parse again and check for errors
    parseLines( linesLeft, false )
  }

  /**
   * When buildDependencyMap is true, we ignore all erros and just build dependency map.
   * When buildDependencyMap is false, we look for errors and use dependency map already built
   */
  private def parseLines( linesLeft: Queue[String], buildDependencyMap : Boolean ){
    //pop first line from queue - it only contains the name for the processed maven project
    val firstLine = linesLeft.dequeue
    val parents = parseFirstLine( firstLine )
    parse(buildDependencyMap, linesLeft, parents, 1)
  }

  private def parseFirstLine( line : String ) : collection.immutable.Queue[Dependency] = {
    //first line looks like this: com.kjetland:testproject4-2:jar:1.0-SNAPSHOT

    val artifactExpression = """(.+):(.+):(.+):(.+)""".r
    line match{
      case artifactExpression(gid, aid, t, v) => {
        val rootDependency = new Dependency(gid, aid, t, v, "compile")
        return new collection.immutable.Queue(rootDependency)
      }
      case _ => throw new Exception("Error parsing first line: '"+line+"'");
    }
  }

  private def parse( buildDependencyMap:Boolean, linesLeft: Queue[String], parents: collection.immutable.Queue[Dependency], currentDepthLength: Int) {

    //when runing dependency:tree on module-project in parent structure
    //we get some weired output...
    //specialModulesDepIgnores is a regexp that finds and ignores these lines..
    //See this file for example:
    //maven-deptools-plugin-scala/src/test/resources/dependency_tree2.txt
    //TODO: must undertand artifact output as seen in maven-deptools-plugin-scala/src/test/resources/dependency_tree2.txt
    //and discover the dependency it refers to
    val specialModulesDepIgnores = """.+(?:- active project artifact:|artifact = |project: MavenProject: ).*""".r
    val depthExpression = """([ \\\+\-|]*)- (.+)""".r


    var previousDependency: Dependency = null

    //pop first line from linesLeft
    while (!linesLeft.isEmpty) {
      val line = linesLeft.front
      //(commons-logging:commons-logging:jar:1.1.1:compile - omitted for conflict with 1.0.1)
      line match {
        //see comment above declaration of val specialModulesDepIgnores for info
        case specialModulesDepIgnores() => {
          //ignoring this line
          linesLeft.dequeue;
          //logger.debug("special ignorin: " + line)
        }
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
            parse(buildDependencyMap, linesLeft, parents.enqueue(previousDependency), depthLength)

          } else if (depthLength < currentDepthLength) {
            //past last child
            //println("past last child")
            return;
          } else {
            //pop the line from the queue
            linesLeft.dequeue;
            var dependency = parseDep(buildDependencyMap, parents, depString)


            if( dependency != null ){
              if( buildDependencyMap ){
                dependencies.update(dependency.key, parents enqueue dependency)
              }
              previousDependency = dependency;
            }
          
          }

        }
        case _ => {
          //line did not match any regexp.. just pop the line
          linesLeft.dequeue;
          //logger.debug("ignoring line: " + line)
        }
      }
    }

  }

  private def parseDep(ignoreErrors:Boolean, parents: collection.immutable.Queue[Dependency], depString: String): Dependency = {
    val artifactExpressionPart = """(.+):(.+):(.+):(.+):(.+)"""

    val okArtifactExpression = (artifactExpressionPart).r
    val errorArtifactExpression = ("\\("+artifactExpressionPart+" - (.+)\\)").r
    val duplicateArtifactExpression = """\(.+ - omitted for duplicate\)""".r


    depString match {
      case errorArtifactExpression( gid, aid, t, v, scope, errorMsg ) => {
        if( ignoreErrors ) return null
        
        val dependency = new Dependency(gid, aid, t, v, scope)
        handleError( parents enqueue dependency, errorMsg)
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

  private def handleError( dependencyHierarchy: collection.immutable.Queue[Dependency], errorMsg : String) {
    val duplicateExpr = "omitted for duplicate".r
    val omittedExpr = "omitted for conflict with (.+)".r
    errorMsg match {
      case omittedExpr(version) => {
        handleOmittedError( dependencyHierarchy, version )
      }
      case duplicateExpr() => {
        //ignoring
        logger.debug("ignoring error: " + errorMsg + " for dependency: " + dependencyHierarchy.last);
      }
      case _ => {
        logger.debug("unknown error: " + errorMsg + " for dependency: " + dependencyHierarchy.last);
      }
    }
  }

  private def handleOmittedError( dependencyHierarchy: collection.immutable.Queue[Dependency], version : String ){

    //get the last element from dependencyHierarchy - this is the dependency that is excluded
    val lastDep = dependencyHierarchy.last

    logger.debug("The dependnecy '"+lastDep+"' is omitted since artifact is already included with version '"+version+"'")

    //find existing occurance of this dependency
    dependencies.get( lastDep.key) match {
      case Some(depPath ) => {
        //logger.debug("refered dep: " + dep)

        val dep = depPath.last

        //check if omitted version is older than included one.
        val thisVersion = new Version( lastDep.version)
        val existingVersion = new Version( dep.version )
        if( thisVersion.compareTo(existingVersion) > 0 ){
          val mainErrorMsg = "Newer dependency '"+lastDep+"' is omitted in favor of an old version '"+dep.version+"'"
          logger.error(mainErrorMsg)
          logger.error("Path to omitted dependency:")
          printDependencyHierarchy( dependencyHierarchy)
          logger.error("Path to used(overriding) dependency:")
          printDependencyHierarchy( depPath)

          //fail the build
          throw new MojoFailureException(mainErrorMsg)
        }

      }
      case None => throw new Exception("Omitted error referes to dependency not found..")
    }


    //logger.debug("Handling omitted error. version: " + version)
    //printDependencyHierarchy( dependencyHierarchy)
  }

  private def printDependencyHierarchy(dependencyHierarchy: collection.immutable.Queue[Dependency]){
    var indent = ""
    dependencyHierarchy.toArray.foreach{
      x => 
      val pre = if(indent.length==0){
        ""
      }else{
        indent+"""|-"""
      }
      logger.error( pre + "" +x)
      indent = indent + "  "
    }
  }
}
package deptools.plugin.scala.parser

import deptools.plugin.scala.utils.MyLogger
import collection.mutable.{HashMap, Queue, ListBuffer}
import filter.DependencyFilter
import org.apache.maven.plugin.MojoFailureException

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 15.mar.2010
 * Time: 21:05:36
 * To change this template use File | Settings | File Templates.
 */



/**
 * includePattern and excludePattern is optional(can be null).
 * If not null, they should be regexp.
 *
 * When DepTreeOutputParser finds an error, includePattern and excludePattern can
 * be used to decide if the plugin should fail the build.
 *
 * the patterns is used like this:
 *
 * if includePattern is declared - we only proceed with the build-fail  if the include matches.
 * if excludePattern is declared - we only proceed with the build-fail if the exclude does not match.
 *
 * Both patterns is checked in the order specified above.
 *
 * This is how the pattern-matching is done.
 *
 * the regexp is matched against both the string 'groupId:artifactId' for both the dependency with the error,
 * AND for the complete dependency-path that leaded to that faulty dependency.
 * The topmost dependency is not checked against the patterns, since it will always be the project you are building.
 */
class DepTreeOutputParser(
        dependencyFilter : DependencyFilter,
        logger: MyLogger) {


  def this( logger: MyLogger ){
    this( null, logger)
  }



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

    val depthExpression = """([ \\\+\-|]*)- (.+)""".r


    var previousDependency: Dependency = null

    //pop first line from linesLeft
    while (!linesLeft.isEmpty) {
      val line = linesLeft.front
      //(commons-logging:commons-logging:jar:1.1.1:compile - omitted for conflict with 1.0.1)
      line match {
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
            var dependency = parseDep(buildDependencyMap, parents, depString, linesLeft)


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

  private def parseDep(
          ignoreErrors:Boolean,
          parents: collection.immutable.Queue[Dependency],
          depString: String,
          linesLeft: Queue[String]): Dependency = {
    val artifactExpressionPart = """(.+):(.+):(.+):(.+):(.+)"""

    val okArtifactExpression = (artifactExpressionPart).r
    val errorArtifactExpression = ("\\("+artifactExpressionPart+" - (.+)\\)").r
    val duplicateArtifactExpression = """\(.+ - omitted for duplicate\)""".r
    var activeProjectArtifactExpression = ("""\(?active project artifact:""").r


    depString match {

    /**
     * If the 'active project artifact' is omitted or have any other error,
     * the dependency:tree output could look like this (with the start parantece..):
|  +- (active project artifact:
	      artifact = com.company.nlp.SomeSystem:SomeSystemGatewayModel:jar:1.11.1-SNAPSHOT:compile;
	      project: MavenProject: com.company.nlp.SomeSystem:SomeSystemGatewayModel:1.11.1-SNAPSHOT @ /home/morten/projects/SomeSystemGateway-parent/SomeSystemGatewayModel/pom.xml - omitted for duplicate)

      Must match with this optional paranthese, but we can ignore the errors
      since we can assume that all paren-pom-and-modules-project-structure
      uses the same version...
     */

      case activeProjectArtifactExpression() => return handleActiveProjectArtifact(linesLeft)
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

  private def handleActiveProjectArtifact(linesLeft: Queue[String]): Dependency = {
    //when runing dependency:tree on module-project in parent structure
    //we get some weired output...
    //specialModulesDepIgnores is a regexp that finds and ignores these lines..
    //See this file for example:
    //maven-deptools-plugin-scala/src/test/resources/dependency_tree2.txt
    //TODO: must undertand artifact output as seen in maven-deptools-plugin-scala/src/test/resources/dependency_tree2.txt
    //and discover the dependency it refers to

    //we must read multiple lines and use this line:
    //[tab]artifact = com.kjetland:testproject4-1:jar:1.0-SNAPSHOT:compile;

    //we must remove all lines starting with tab from the linesLeft-queue

    var dependency : Dependency = null


    while( !linesLeft.isEmpty && linesLeft.front.startsWith("\t") ){
      //this is an active project artifact line since it starts with tab
      //pop it from linesLeft
      val line = linesLeft.dequeue

      val artifactExpr = """\tartifact = (.+):(.+):(.+):(.+):(.+);""".r

      line match {
        case artifactExpr(gid, aid, t, v, scope) => {
          //found an active project artifact line.
          dependency = new Dependency(gid, aid, t, v, scope)
        }
        case _ => None //just ignore this line
      }

    }

    if( dependency == null ) throw new Exception("Error parsing Active Project Artifact syntax")

    return dependency
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

          //must check dependencyPath against filter to know
          //if we should fail the build or not
          if( dependencyFilter == null || dependencyFilter.matches( dependencyHierarchy.toArray )){

            val mainErrorMsg = "Newer dependency '"+lastDep+"' is omitted in favor of an old version '"+dep.version+"'"
            logger.info(mainErrorMsg)
            logger.info("")
            logger.info("Path to omitted dependency:")
            printDependencyHierarchy( dependencyHierarchy)
            logger.info("")
            logger.info("Path to used(overriding) dependency:")
            printDependencyHierarchy( depPath)
            logger.info("")

            //fail the build
            throw new MojoFailureException(mainErrorMsg)
          }else{
            logger.info("Ignoring omitted error due to filter: Newer dependency '"+lastDep+"' is omitted in favor of an old version '"+dep.version+"'")
          }
        }

      }
      case None => throw new Exception("Omitted error referes to dependency not found..")
    }


    //logger.debug("Handling omitted error. version: " + version)
    //printDependencyHierarchy( dependencyHierarchy)
  }

  private def printDependencyHierarchy(dependencyHierarchy: collection.immutable.Queue[Dependency]){
    var count = 0
    var indent = "  "
    dependencyHierarchy.toArray.foreach{
      x => 
      val pre = if(count==0){
        ""
      }else{
        """|-"""
      }
      logger.info( indent + pre + "" +x)
      indent = indent + "  "
      count += 1
    }
  }
}
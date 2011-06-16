package deptools.plugin.scala

import org.apache.maven.project.MavenProject
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.{PluginManager, AbstractMojo}
import collection.mutable.{Queue, ListBuffer}
import parser.filter.DependencyFilterIncludeExcludeImpl
import parser.{DepTreeOutputParser}
import utils.{MyLogger, File2QueueReader}
import java.lang.String
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.dependency.TreeMojo
import org.apache.maven.shared.dependency.tree.DependencyNode

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 12.mar.2010
 * Time: 22:00:01
 * To change this template use File | Settings | File Templates.
 */

abstract class ScalaMojo extends AbstractMojo {

  def getProject: MavenProject
  def getSession: MavenSession
  def getBuildDir: String
  def getPluginManager: PluginManager

  //Optional Regex pattern used to find groupId/ArtifactId's to run version-check on
  def getIncludePattern : String

  //Optional Regex pattern used to exclude groupId/ArtifactId's to run version-check on
  def getExcludePattern : String


  def getLocalRepository : ArtifactRepository 
  def getArtifactFactory : ArtifactFactory
  def getArtifactMetadataSource : ArtifactMetadataSource
  def getArtifactCollector : ArtifactCollector
  def getDependencyTreeBuilder : DependencyTreeBuilder


  override def getLog = {
    super.getLog
  }

  def execute  {
	
    val lines = retrieveDependencyLines()

    object MyMojoLogger extends AnyRef with MyLogger{
      def debug(msg: String) {
        getLog().debug( msg )
      }

      def info(msg: String) {
        getLog().info( msg )
      }

      def error(msg: String) {
        getLog().error( msg )
      }
    }

    val dependencyFilter = new DependencyFilterIncludeExcludeImpl(fixEmptyString(getIncludePattern), fixEmptyString(getExcludePattern))

    MyMojoLogger.info("Using filter: " + dependencyFilter)

    new DepTreeOutputParser(dependencyFilter, MyMojoLogger).parse( lines )


    return
  }

  def retrieveDependencyLines() : Queue[String] = {

	val artifactFilter : ArtifactFilter = null

	//build the dependency tree the same way as done in maven-dependency-plugin
	val rootNode : DependencyNode =
	    getDependencyTreeBuilder.buildDependencyTree( getProject, getLocalRepository, getArtifactFactory,
	                                               getArtifactMetadataSource, artifactFilter, getArtifactCollector )
	//now we must convert the tree into string
	val lines = convertDependencyNodeToString( rootNode )
	return lines
  }

  /* To simulate the text output from maven-dependency-plugin
 	 we must call a private method (TreeMojo.serialiseDependencyTree) in the maven-dependency-plugin
  */
  def convertDependencyNodeToString( rootNode : DependencyNode ) : Queue[String] = {
	
	val treeMojo = new TreeMojo
	
	//we need to set the private field 'verbose' to true
	val verboseField = treeMojo.getClass.getDeclaredField("verbose")
	//make it accessable
	verboseField.setAccessible( true )
	//set its value
	verboseField.setBoolean( treeMojo, true)
	
	//find the method..
	val serialiseDependencyTreeMethod = treeMojo.getClass.getDeclaredMethod("serialiseDependencyTree", rootNode.getClass)
		
	//invoke the private method
	//must first make the private method accesable
	serialiseDependencyTreeMethod.setAccessible(true)
	val stringOutput : String = serialiseDependencyTreeMethod.invoke( treeMojo, rootNode ).toString
	
	//must convert the string into Queue[String]
	val lines = stringOutput.split("\\r?\\n")
	
	val linesQueue = new Queue[String]
	linesQueue ++= lines
	
	return linesQueue
  }

  def fixEmptyString( str : String ) : String = {
    if( str == null || str.trim.isEmpty ){
      null
    }else{
      str
    }
  }

}
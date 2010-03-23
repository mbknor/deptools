package deptools.plugin.scala

import org.apache.maven.project.MavenProject
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.{PluginManager, AbstractMojo}
import org.twdata.maven.mojoexecutor.MojoExecutor._
import collection.mutable.{Queue, ListBuffer}
import parser.DepTreeOutputParser
import utils.{MyLogger, File2QueueReader}
import java.lang.String

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 12.mar.2010
 * Time: 22:00:01
 * To change this template use File | Settings | File Templates.
 */

abstract class ScalaMojo extends AbstractMojo {

  private val depTreeFilename = "/dependency_tree/dependency_tree.txt"


  def getProject: MavenProject
  def getSession: MavenSession
  def getBuildDir: String
  def getPluginManager: PluginManager


  override def getLog = {
    super.getLog
  }

  def execute  {

    executeDepTree()
    val lines = File2QueueReader.readFile( outputFilename )

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

    new DepTreeOutputParser(MyMojoLogger).parse( lines )


    return
  }



  def outputFilename = getBuildDir + depTreeFilename



  def executeDepTree() {
    val pm = getPluginManager
    
    executeMojo(
      plugin(
        groupId("org.apache.maven.plugins"),
        artifactId("maven-dependency-plugin"),
        version("2.0")
        ),
      goal("tree"),
      configuration(
        element(name("outputFile"), "${project.build.directory}" + depTreeFilename),
        element(name("verbose"), "true")
        )
      ,
      executionEnvironment(
        getProject,
        getSession,
        pm
        )
      )

  }
}
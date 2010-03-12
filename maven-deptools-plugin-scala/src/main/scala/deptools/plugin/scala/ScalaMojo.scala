package deptools.plugin.scala

import org.apache.maven.project.MavenProject
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.{PluginManager, AbstractMojo}
import org.twdata.maven.mojoexecutor.MojoExecutor._

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 12.mar.2010
 * Time: 22:00:01
 * To change this template use File | Settings | File Templates.
 */

abstract class ScalaMojo extends AbstractMojo {
  private var _testString: String = null

  private var _project: MavenProject = null
  private var _session: MavenSession = null

  def setTestString(v: String) = _testString = v

  def setProject(project: MavenProject) = _project = project

  def setSession(session: MavenSession) = _session = session

  def getPluginManager():PluginManager


  def execute = {

    try {

      val pm = getPluginManager()

      getLog.info("from scala!!")
      getLog.info("testString: " + _testString)

      executeMojo(
        plugin(
          groupId("org.apache.maven.plugins"),
          artifactId("maven-dependency-plugin"),
          version("2.0")
          ),
        goal("tree"),
        configuration(
          element(name("outputDirectory"), "${project.build.directory}/foo")
          )
        ,
        executionEnvironment(
          _project,
          _session,
          pm
          )
        )

    } catch {
      case unknown => getLog.error(unknown)
    }
  }
}
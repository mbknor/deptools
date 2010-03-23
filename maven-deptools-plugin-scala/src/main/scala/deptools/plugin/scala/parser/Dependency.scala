package deptools.plugin.scala.parser

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 23.mar.2010
 * Time: 23:42:35
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
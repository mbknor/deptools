package deptools.plugin.scala.utils

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 16.mar.2010
 * Time: 22:56:15
 * To change this template use File | Settings | File Templates.
 */

trait MyLogger{
  def debug( msg: String )
  def info( msg: String)
  def error(msg: String)
}
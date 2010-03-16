package deptools.plugin.scala.utils

import collection.mutable.Queue
import java.io.{FileReader, BufferedReader}

/**
 * Created by IntelliJ IDEA.
 * User: morten
 * Date: 15.mar.2010
 * Time: 21:13:14
 * To change this template use File | Settings | File Templates.
 */

object File2QueueReader{
  def readFile(filename : String): Queue[String]={
    val lines = new Queue[String]
    var in : BufferedReader = null
    try{
      in = new BufferedReader( new FileReader(filename))
      var line : String = null
      while( {line = in.readLine; line != null}){
        lines.enqueue(line)
      }
      return lines
    }finally{
      try{
        in.close
      }catch{
        case unknown => None
      }
    }
  }
}
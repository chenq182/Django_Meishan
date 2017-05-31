import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf


object SimpleApp {
  def main(args: Array[String]) {
    if(args.length!=5) {
      println("Usage: *.jar <input> <output> 10 30 1.6")
      return
    }
    val rawFile = args(0)
    val outFile = args(1)
    val hourThreshold : Int = args(2).toInt
    val dayThreshold : Int = args(3).toInt
    val overThreshold : Double = args(4).toDouble

    val conf = new SparkConf().setAppName("Ycyx")
    val sc = new SparkContext(conf)
    val rawData = sc.textFile(rawFile).filter(line => line.contains(",限值"))
    val overKey = rawData.map(Trim.bay).filter(t => t._3 < t._2*overThreshold)
    val key = overKey.map(t => "%s".format(t._1)).distinct
    val hourKey = key.map(Trim.hour).reduceByKey(_ + _)
    hourKey.cache()
    val dayKey = hourKey.map(Trim.day).reduceByKey(_ + _).filter(t => t._2 >= dayThreshold)
    val result = hourKey.filter(t => t._2 >= hourThreshold).union(dayKey)
    result.map(Trim.divid).sortBy(_._1, false).map(t => "%s,%s,%s".format(t._1,t._2,t._3)).saveAsTextFile(outFile)
  }
}

object Trim {
  def bay(s: String): (String, Double, Double) = {
    val RE = """.+,(\d{4}年.+\d{2}秒)\s+(.+)\s+(线电压幅值|电流值|[ABC]相电压幅值|[ABC]相电流|有功值|无功值|实测值).+,限值:\s*([^\s]+)\s+([-\w\.]+).+""".r
    s match {
      case RE(time, name, other, v2, v3) => (s"""$time$name""", v2.toDouble, v3.toDouble)
      case _ => (s"[FAIL] $s", 0.0, 0.0)
    }
  }
  def hour(s: String): (String, Int) = {
    val RE = """(.+)\d{2}分\d{2}秒(.+)""".r
    s match {
      case RE(time, name) => (s"""$time$name""", 1)
      case _ => (s"[FAIL] $s", 1)
    }
  }
  def day(s: (String, Int)): (String, Int) = {
    val RE = """(.+)\d{2}时(.+)""".r
    s._1 match {
      case RE(time, name) => (s"""$time$name""", s._2)
      case _ => (s"[FAIL]", s._2)
    }
  }
  def divid(s: (String, Int)): (String, String, Int) = {
    val RE1 = """(.+\d{2}时)(.+)""".r
    val RE2 = """(.+\d{2}日)(.+)""".r
    s._1 match {
      case RE1(time, name) => (time, name, s._2)
      case RE2(time, name) => (time, name, s._2)
      case _ => (s"[FAIL]", s._1, s._2)
    }
  }
}
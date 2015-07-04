package com.hacks.tvdb
import scala.sys.process._
import java.io.File
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * @author tleuser
 */
object SeriesLookup {
  val utilsDir = new File("../utils/")
  val tvdbCmd = new File(utilsDir, "tvdb_data.py").getAbsolutePath
  val youtubeCmd = new File(utilsDir, "youtube_data.py").getAbsolutePath

  case class SeriesLookupResult(summary: Option[String], youtubeId: Option[String])

  def fromTvDb(name: String) = {
    Future { Seq(tvdbCmd, "-n", name) !! }.map(Option.apply).recover {
      case t => None
    }
  }

  def fromYoutube(name: String) = {
    Future { Seq(youtubeCmd, "-q", name) !! }.map(Option.apply).recover {
      case t => None
    }
  }

  def lookupSeries(name: String) = {
    for {
      (yid, summary) <- fromYoutube(name).zip(fromTvDb(name))
    } yield SeriesLookupResult(summary, yid)
  }
}
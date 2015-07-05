package com.hacks.tvdb

import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.client.Client
import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.common.Classes
import org.elasticsearch.action.ListenableActionFuture
import scala.concurrent.Promise
import scala.concurrent.Future
import org.elasticsearch.action.ActionListener
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.elasticsearch.action.bulk.BulkProcessor
import org.elasticsearch.common.unit.ByteSizeValue
import org.elasticsearch.common.unit.ByteSizeUnit
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.common.xcontent.XContentFactory
import scala.collection.convert.WrapAsScala._
import scala.collection.convert.WrapAsJava._
import java.util.Collection
import java.security.DigestInputStream
import java.security.MessageDigest
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.JsonNode
import ElasticClient._
import org.apache.commons.lang3.text.WordUtils

/**
 * @author tleuser
 */

object NullListener extends BulkProcessor.Listener {
  def afterBulk(x$1: Long, x$2: BulkRequest, x$3: Throwable): Unit = {
    System.out.println(s"Error: ${x$3}")
  }

  def afterBulk(x$1: Long, x$2: BulkRequest, x$3: BulkResponse): Unit = {
    System.out.println(s"Success: ${!x$3.hasFailures()}")

  }

  def beforeBulk(x$1: Long, x$2: BulkRequest): Unit = {

  }
}

object LoadJson extends App {

  val states = Map(
    "TAS" -> Seq("Tasmania State", "Tasmania Regionals", "Tasmania Hobart", "Hobart"),
    "NSW" -> Seq("NSW Sydney", "NSW Regionals", "NSW State", "Sydney"),
    "QLD" -> Seq("Queensland Brisbane", "Queensland Regionals", "Queensland State", "Brisbane"),
    "VIC" -> Seq("Victoria Melbourne", "Victoria Regionals", "Victoria State", "Melbourne"),
    "ACT" -> Seq("Australian Capital Territory"),
    "SA" -> Seq("South Australia Adelaide", "South Australia Regionals", "South Australia State", "Adelaide"),
    "NT" -> Seq("Northern Territory State", "Northern Territory Regionals", "Northern Territory Darwin", "Darwin"),
    "WA" -> Seq("Western Australia Perth", "Western Australia State", "Western Australia Regionals", "Perth"))

  val reverseMap = (states.map {
    case (st, channels) => channels.map(c => (c, st))
  }).flatten.toMap

  val mapper = new ObjectMapper
  val indexName = "govhack3"

  implicit def convertUnit(unit: Unit): Mapping = Mapping.apply()
  implicit def convertString(str: String): Mapping = Mapping(ftype = str)

  case class Mapping(ftype: String = "string", analyzed: Boolean = false, format: String = "", multi: Map[String, Mapping] = Map.empty)
  val mappings: Map[String, Mapping] = Map("Series" -> Mapping(analyzed = true, multi = Map("raw" -> ())),
    "ProgEpisodeName" -> Mapping(analyzed = true, multi = Map("raw" -> ())),
    "VersionCensorship" -> (),
    "ChildChannel" -> (),
    "StartTime" -> Mapping(ftype = "date", format = "HH:mm:ss"),
    "Duration" -> Mapping(ftype = "date", format = "HH:mm:ss:SS"),
    "EpNo" -> (),
    "ParentChannel" -> (),
    "SeriesNo" -> (),
    "Source" -> (),
    "Date" -> Mapping(ftype = "date", format = "yyyy/MM/dd"))

  def writeFields(fields: Map[String, Mapping], parent: ObjectNode): Unit = {
    fields.foreach {
      case (name, Mapping(ftype, analyzed, format, multi)) =>
        val fprops = parent.putObject(name)
        fprops.put("type", ftype)
        if (!analyzed) fprops.put("index", "not_analyzed")
        if (!format.isEmpty()) fprops.put("format", format)
        if (!multi.isEmpty) {
          writeFields(multi, fprops.putObject("fields"))
        }
    }
  }

  def makeMapping = {
    val start = mapper.createObjectNode()
    val topLevel = start.`with`("tv")
    val props = topLevel.`with`("properties")
    writeFields(mappings, props)
    Await.result(client.admin().indices().preparePutMapping(indexName).setType("tv").setSource(start.toString).execute(), Duration.Inf)
  }

  val jsonFile = new File("/home/tleuser/TVDBA.json")
  makeMapping
  val parser = mapper.getFactory.createParser(jsonFile)
  System.err.println(parser.nextToken())
  System.err.println(parser.nextToken())
  val processor = BulkProcessor.builder(client, NullListener).setBulkActions(20000)
    .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
    .setFlushInterval(TimeValue.timeValueSeconds(10))
    .setConcurrentRequests(1).build()
  var finished = false
  while (!finished) {
    val index = client.prepareIndex()
    index.setIndex(indexName)
    index.setType("tv")
    parser.readValueAsTree[JsonNode]() match {
      case r: ObjectNode =>
        val childChannel = r.get("ChildChannel").asText()
        Option(r.get("Series")).foreach(s => r.put("Series", WordUtils.capitalizeFully(s.asText())))
        r.remove(asJavaCollection(r.fields().filter(_.getValue.asText() == "NULL").map(_.getKey).toIterable))
        val foundStates = reverseMap.get(childChannel).map(Seq(_)).getOrElse(states.keys)
        foundStates.foreach { st =>
          r.put("ChildChannel", st)
          val digest = MessageDigest.getInstance("MD5")
          digest.update(r.toString.getBytes)
          val id = digest.digest().map("%02X".format(_)).mkString
          index.setId(id)
          index.setSource(r.toString)
          processor.add(index.request())
        }
      case _ => finished = true
    }
  }
  processor.flush()
}
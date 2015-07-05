package com.hacks.tvdb

import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.actor.ActorSystem
import ElasticClient._
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import java.util.concurrent.TimeUnit
import org.elasticsearch.action.search.SearchResponse
import scala.concurrent.Future
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.elasticsearch.search.SearchHit
import scala.collection.convert.WrapAsScala._
import java.util.Date
import akka.http.scaladsl.model.DateTime
import org.elasticsearch.search.facet.FacetBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregator
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.sort.SortOrder
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.HttpResponse
import com.hacks.tvdb.SeriesLookup.SeriesLookupResult
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods._
import org.apache.commons.lang3.text.WordUtils
/**
 * @author tleuser
 */
object TvQueries extends App {
  val indexName = "govhack2"
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  def query(series: Option[String], year: Option[Int], state: Option[String], startTime: Option[Int], endTime: Option[Int]): Future[AiringResults] = {
    import scala.concurrent.ExecutionContext.Implicits._
    val search = client.prepareSearch(indexName)
    search.addFields("StartTime", "Date")
    search.setFetchSource(true)
    search.addSort("Date", SortOrder.ASC)
    search.addSort("StartTime", SortOrder.ASC)
    search.setSize(50)

    val startQ = startTime.orElse(endTime).map { _ =>
      val rq = QueryBuilders.rangeQuery("StartTime")
      startTime.foreach { start => rq.gte(TimeUnit.HOURS.toMillis(start)) }
      endTime.foreach { end => rq.lt(TimeUnit.HOURS.toMillis(end)) }
      rq
    }
    val queries: Iterable[QueryBuilder] = startQ ++
      series.map(QueryBuilders.matchPhraseQuery("Series", _)) ++
      year.map(y => TVNormalizing.yearQuery(y)) ++
      state.map(TVNormalizing.channelQuery)

    val combined = queries.foldLeft(QueryBuilders.boolQuery())((b, q) => b.must(q))
    search.setQuery(combined)
    search.addAggregation(AggregationBuilders.terms("series").field("Series.raw"))
    println(search)
    search.execute().map(searchToAggs)
  }

  def searchToAggs(searchResp: SearchResponse): AiringResults = {
    val searchAggs = searchResp.getAggregations
    val airs = searchResp.getHits.map(hitToAiring).toIterable
    val series: Terms = searchResp.getAggregations().get("series")
    val shows = series.getBuckets.map(b => (WordUtils.capitalizeFully(b.getKey), b.getDocCount)).filter { !_._1.isEmpty }
    AiringResults(airs, shows.groupBy(_._1).map {
      case (name, counts) => Show(name, counts.map(_._2).reduce(_ + _))
    })
  }

  def hitToAiring(searchHit: SearchHit): Airing = {
    val map = searchHit.getSource.toMap
    val series = map.get("Series").map(_.toString).getOrElse("")
    val channel = map.get("ChildChannel").map(_.toString).getOrElse("")
    val episode = map.get("ProgEpisodeName").map(_.toString).getOrElse("")
    val date = searchHit.getFields.get("Date").value[String]()
    val startTime = searchHit.getFields.get("StartTime").value[String]()
    Airing(series, episode, channel, s"$date $startTime")
  }

  case class Airing(series: String, episode: String, channel: String, startTime: String)

  implicit val airingFormat = jsonFormat4(Airing)

  case class Show(name: String, count: Long)
  implicit val showFormat = jsonFormat2(Show)

  case class AiringResults(airings: Iterable[Airing], shows: Iterable[Show])
  implicit val airingResultsFormat = jsonFormat2(AiringResults)
  implicit val seriesLookupFormat = jsonFormat2(SeriesLookupResult)

  val route =
    path("airings") {
      get {
        parameters("series".?,
          "year".as[Int].?,
          "state".?,
          "starthour".as[Int].?,
          "endhour".as[Int].?) {
            (series, year, state, startHour, endHour) =>
              parameterMap { params =>
                val extra = params -- List("series", "year", "state", "starthour", "endhour")
                if (!extra.isEmpty) complete {
                  HttpResponse(StatusCodes.BadRequest,
                    entity = """invalid parameter, can use "series", "year", "state", "starthour", "endhour"""")
                }
                else complete { query(series, year, state, startHour, endHour) }
              }
          }
      }
    } ~
      {
        path("details") {
          get {
            parameters("series") { (name) =>
              complete(SeriesLookup.lookupSeries(name))
            }
          }
        }
      }

  val optionsSupport = {
    options { complete("") }
  }

  val corsHeaders = List(`Access-Control-Allow-Origin`.*, `Access-Control-Allow-Methods`(GET, POST, PUT, OPTIONS, DELETE),
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Authorization"))

  val corsRoutes = {
    respondWithHeaders(corsHeaders) { route ~ optionsSupport }
  }
  val bindingFuture = Http().bindAndHandle(corsRoutes, "0.0.0.0", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine()

  import system.dispatcher // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.shutdown()) // and shutdown when done

}
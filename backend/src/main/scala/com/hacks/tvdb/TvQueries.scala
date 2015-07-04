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

/**
 * @author tleuser
 */
object TvQueries extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  def query(show: Option[String], year: Option[Int], state: Option[String], startTime: Option[Int], endTime: Option[Int]): Future[Iterable[Airing]] = {
    import scala.concurrent.ExecutionContext.Implicits._
    val search = client.prepareSearch("govhack")
    search.addFields("StartTime", "Date")
    search.setFetchSource(true)

    val startQ = startTime.orElse(endTime).map { _ =>
      val rq = QueryBuilders.rangeQuery("StartTime")
      startTime.foreach { start => rq.gte(TimeUnit.HOURS.toMillis(start)) }
      endTime.foreach { end => rq.lt(TimeUnit.HOURS.toMillis(end)) }
      rq
    }
    val queries: Iterable[QueryBuilder] = startQ ++ 
    year.map(y => TVNormalizing.yearQuery(y)) ++ 
    state.map(TVNormalizing.channelQuery)
    
    val combined = queries.foldLeft(QueryBuilders.boolQuery())((b, q) => b.must(q))
    search.setQuery(combined)
    println(search)
    search.execute().map(_.getHits.map(hitToAiring).toIterable)
  }

  def hitToAiring(searchHit: SearchHit): Airing = {
    val map = searchHit.getSource.toMap
    val series = map.get("Series").map(_.toString).getOrElse("")
    val episode = map.get("ProgEpisodeName").map(_.toString).getOrElse("")
    val date = searchHit.getFields.get("Date").value[String]()
    val startTime = searchHit.getFields.get("StartTime").value[String]()
    Airing(series, episode, s"$date $startTime")
  }

  case class Airing(series: String, episode: String, startTime: String)

  implicit val airingFormat = jsonFormat3(Airing)

  val route =
    path("query") {
      get {
        parameters("show".?,
          "year".as[Int].?,
          "state".?,
          "starthour".as[Int].?,
          "endhour".as[Int].?) {
            (show, year, state, startHour, endHour) =>
              complete { query(show, year, state, startHour, endHour) }
          }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine()

  import system.dispatcher // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.shutdown()) // and shutdown when done

}
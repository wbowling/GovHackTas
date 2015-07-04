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

/**
 * @author tleuser
 */
object TvQueries extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()

  def query(show: Option[String], timeRange: Option[(Int, Int)]): Future[SearchResponse] = {
    val search = client.prepareSearch("govhack")
    val startQ = timeRange.map {
      case (start, end) => QueryBuilders.rangeQuery("StartTime").gte(TimeUnit.HOURS.toMillis(start)).lt(TimeUnit.HOURS.toMillis(end))
    }
    val queries: Iterable[QueryBuilder] = startQ
    val combined = queries.foldLeft(QueryBuilders.boolQuery())((b, q) => b.must(q))
    search.setQuery(combined)
    search.execute()
  }

  val route =
    path("hello") {
      get {
        parameters("show", "starthour".as[Int], "endhour".as[Int]) { (show, startHour, endHour) =>
          complete { "Hi" }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  Console.readLine()

  import system.dispatcher // for the future transformations
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ ⇒ system.shutdown()) // and shutdown when done

}
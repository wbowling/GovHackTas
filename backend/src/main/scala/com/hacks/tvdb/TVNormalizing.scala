package com.hacks.tvdb

import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import akka.http.scaladsl.model.DateTime
import java.util.Date

object TVNormalizing {
  private val states = Map(
    "TAS" -> Seq("Tasmania State", "Tasmania Regionals", "Tasmania Hobart", "Hobart"),
    "NSW" -> Seq("NSW Sydney", "NSW Regionals", "NSW State", "Sydney"),
    "QLD" -> Seq("Queensland Brisbane", "Queensland Regionals", "Queensland State", "Brisbane"),
    "VIC" -> Seq("Victoria Melbourne", "Victoria Regionals", "Victoria State", "Melbourne"),
    "ACT" -> Seq("Australian Capital Territory"),
    "SA" -> Seq("South Australia Adelaide", "South Australia Regionals", "South Australia State", "Adelaide"),
    "NT" -> Seq("Northern Territory State", "Northern Territory Regionals", "Northern Territory Darwin", "Darwin"),
    "WA" -> Seq("Western Australia Perth", "Western Australia State", "Western Australia Regionals", "Perth"))

  def channelQuery(state: String): QueryBuilder = {
    val bool = QueryBuilders.boolQuery()
    states.get(state.toLowerCase).map(_ :+ "Network").foreach {
      name => bool.should(QueryBuilders.termQuery("ChildChannel", name))
    }
    bool
  }

  def yearQuery(year: Int, span: Int = 1): QueryBuilder = {
    val yearFromDate = DateTime(year, 1, 1)
    val yearToDate = DateTime(year + span, 1, 1)
    QueryBuilders.rangeQuery("Date").from(yearFromDate.clicks).to(yearToDate.clicks)
  }
}
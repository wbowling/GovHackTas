package com.hacks.tvdb

import org.elasticsearch.node.NodeBuilder
import org.elasticsearch.common.Classes
import org.elasticsearch.action.ListenableActionFuture
import org.elasticsearch.common.settings.ImmutableSettings
import scala.concurrent.Future
import org.elasticsearch.client.Client
import scala.concurrent.Promise
import org.elasticsearch.action.ActionListener


/**
 * @author tleuser
 */
object ElasticClient {
  implicit def toFuture[A](listenable: ListenableActionFuture[A]): Future[A] = {
    val p = Promise[A]
    listenable.addListener(
      new ActionListener[A]() {
        def onResponse(response: A) = p.success(response)
        def onFailure(e: Throwable) = p.failure(e)
      })
    p.future
  }

  lazy val client: Client = {
    val originalLoader = Thread.currentThread().getContextClassLoader
    Thread.currentThread().setContextClassLoader(classOf[Classes].getClassLoader)
    val s = ImmutableSettings.settingsBuilder()
    s.classLoader(classOf[Classes].getClassLoader)
    s.put("cluster.name", "elasticsearch")
    s.put("discovery.zen.ping.unicast.hosts", "192.168.15.202:9300")
    s.put("http.enabled", true)
    val node = NodeBuilder.nodeBuilder().client(true).settings(s).node()
    val client = node.client()
    Thread.currentThread().setContextClassLoader(originalLoader)
    client
  }

}
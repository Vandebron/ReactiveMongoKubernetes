package nl.vandebron.mongo

import reactivemongo.api.MongoConnection.ParsedURI
import reactivemongo.api.{AsyncDriver, DB, MongoConnection}
import reactivemongo.api.bson.collection.BSONCollection


import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, Future}

object Main extends App {
  val database = "reactive-mongo-k8s"
  val userName = "reactive-mongo-k8s-user"
  val password = "7UXA94W34b4wd5Pw"
  val hostName = "testcluster.bkyvc.mongodb.net"
  val collectionName = "testcollection"
  val mongoURi = s"mongodb+srv://$userName:$password@$hostName/$database?connectTimeoutMS=30000"

  import scala.concurrent.ExecutionContext.Implicits.global

  val driver = AsyncDriver()
  implicit val timeout: FiniteDuration = Duration(20, TimeUnit.SECONDS)

  def parsedUri: Future[ParsedURI] = MongoConnection.fromString(mongoURi)

  def futureConnection: Future[MongoConnection] = parsedUri.map(x => driver.connect(x)).flatten

  def db: Future[DB] = futureConnection.flatMap(_.database(database))

  def collection: Future[BSONCollection] = db.map(_.collection(collectionName))

  def eventualEventualLong: Future[Long] = collection.map(_.count()).flatten

  println("Opening connection")
  val result = Await.result(eventualEventualLong, timeout)
  println(s"Found $result items in $collectionName collection")

}
import akka.actor.{ActorRef, ActorSystem, Props}

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {



  if (args.length != 4) {
    println(
      """
        |Please supply the following arguments:
        | 0: path to AWS Credentials file
        | 1: name of the S3 bucket to connect to
        | 2: the AWS region the S3 bucket resides in
        | 3: the name of the Object to stream from the bucket.
      """.stripMargin)
    sys.exit(-1)
  }


  val system: ActorSystem = ActorSystem("AkkaS3Streamer")

  val destProps: Props = Props[DestActor]
  val destination: ActorRef = system.actorOf(destProps, "destination")

  val sourceProps: Props =
    Props(classOf[SourceActor], destination)
  val source: ActorRef = system.actorOf(sourceProps)

  source ! SourceActor.Start(
    credentialsPath = args(0),
    bucketName = args(1),
    region =  args(2),
    fileName = args(3)
  )

  Await.result(system.whenTerminated,2 minutes)

}

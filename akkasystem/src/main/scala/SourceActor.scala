import SourceActor.{ Message, Start }
import akka.actor.{ Actor, ActorRef }
import com.kainos.s3streamer.S3Client

object SourceActor {

  case class Message(value: String)
  case class Start(credentialsPath: String,
                   bucketName: String,
                   region: String,
                   fileName: String)

}

class SourceActor(destActor: ActorRef) extends Actor {

  override def receive: Receive = {
    case Start(credentialsPath, bucketName, region, fileName) =>
      val client = new S3Client(credentialsPath, bucketName, region, true)
      val fileStream = client.getAsStream(fileName)
      val buffer = new Array[Byte](500)
      //just reads 500 bytes of data, messages will not match lines in file.
      Stream.continually(fileStream.read(buffer)).takeWhile(_ != -1).foreach {
        _ =>
          println("SourceActor: Sending message to destination!")
          destActor ! Message(new String(buffer))
      }
  }
}

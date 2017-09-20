import akka.actor.Actor

object DestActor {}

class DestActor extends Actor {
  override def receive: Receive = {
    case SourceActor.Message(string) =>
      println(s"DestActor: got message: ${string.length}")
  }
}

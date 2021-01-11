import colossus._
import core._
import service._
import protocols.http._
import UrlParsing._
import HttpMethod._
import akka.actor
import com.datastax.driver.core._
import org.zeromq.ZMQ

// Top-level object extending trait App - entry point of program
object Main extends App {
    // ZeroMQ setup
    class ZMQActor extends actor.Actor {

        // Initialization and binding of the ZeroMQ socket
        // Publishers are created with ZMQ.PUB socket types
        val context = ZMQ.context()
        val socket = context.socket(ZMQ.PUB)
        val port = "5556"
        socket.bind("tcp://*:%s" % port)

        def receive = {
            // Send message to the socket
            case QueueMsg(message) => socket.send(message.getBytes(), 0)
        }
    }

    // Case class to talk to actor instance
    case class QueueMsg(message: String)

    // Initialize actor system and I/O
    implicit val actorSystem = actor.ActorSystem()
    implicit val ioSystem = IOSystem()
    // Actor that push messages to ZeroMQ safely
    val actor = actorSystem.actorOf(actor.Props[ZMQActor], "zmqActor")

    Server.start("cassandra-http-request", 9000){initContext => new Initializer(initContext) {
        // Multiple cassandra sessions per app instance
        private val session = Cluster.builder().addContactPoint("localhost").withPort(9042).build().connect()
        // PreparedStatement of Cassandra lowers network traffic and CPU utilization and therefore, is fast
        private val prepared: PreparedStatement = session.prepare("SELECT * FROM accounts.accounts WHERE accountId = ?;");
        override def onConnect : RequestHandlerFactory = context => new HttpService(context) {
            override def handle: PartialHandler[Http] = {
                // API example: BASE_URL/<accountId>?data=”<data>”
                case request @ Get on Root / accId ? data => {

                    val accountId: Integer = accId.toInt;
                    val results = session.execute(prepared.bind(accountId)).all();
                    // Validate account id
                    if (results.size() > 0) {
                        // If Account Id is active and present in database
                        if (results.get(0).getBool("isActive")) {
                            //  Propagate event to the pub/sub system by adding timestamp
                            val msg = "trackingservice %d %d %s\u0000".format(accountId, System.currentTimeMillis, data);
                            actor ! QueueMsg(msg)
                            Callback.successful(request.ok("Request propagated to the subscribers!"))
                        } else {
                            // Inactive account
                            Callback.successful(request.badRequest("Inactive account!"))
                        }
                    } else {
                        // Account Id doesn't exist
                        Callback.successful(request.badRequest("Account Id does not exist!"))
                    }
                }
            }
        }
    }
}
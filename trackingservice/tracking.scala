import colossus._
import core._
import service._
import protocols.http._
import UrlParsing._
import HttpMethod._
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import com.datastax.driver.core._
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory


object Main extends App {

    object RabbitMQConnection {

        private val connection: Connection = null;

        /**
        * Return a connection if one doesn't exist. Else create
        * a new one
        */
        def getConnection(): Connection = {
            connection match {
                case null => {
                    val factory = new ConnectionFactory(
                        HostName = "localhost",
                        UserName = "guest",
                        Password = "guest",
                        Port = 5672,
                        RequestedConnectionTimeout = 3000, // milliseconds
                    );
                    factory.setHost(ConfigFactory.load().getString("rabbitmq.host"));
                    factory.newConnection();
                }
                case _ => connection
            }
        }
    }

    object Sender {

        def startSending = {
            // create the connection
            val connection = RabbitMQConnection.getConnection();
            // create the channel we use to send
            val sendingChannel = connection.createChannel();
            // make sure the queue exists we want to send to
            sendingChannel.queueDeclare(ConfigFactory.load().getString("rabbitmq.queue"), false, false, false, null);

            Akka.system.scheduler.schedule(2 seconds, 1 seconds
                , Akka.system.actorOf(Props(
                    new SendingActor(channel = sendingChannel, 
                                                queue = ConfigFactory.load().getString("rabbitmq.queue"))))
                , "MSG to Queue");
        }
    }

    class SendingActor(channel: Channel, queue: String) extends Actor {

    def receive = {
      case QueueMsq(msq) => {
        channel.basicPublish("", queue, null, msg.getBytes());
        Logger.info(msg);
      }
      case _ => {}
    }
  }
  case class QueueMsq(msg: String)

  Server.start("cassandra-http", 9000){ new Initializer(_) {

    // Multiple cassandra sessions per app instance
    private val session = Cluster.builder().addContactPoint("localhost").withPort(9042).build().connect()
    // PreparedStatement of Cassandra lowers network traffic and CPU utilization and therefore, is fast
    private val prepared: PreparedStatement = session.prepare("SELECT * FROM accounts.accounts WHERE accountId = ?;");

   
  class ListeningActor(channel: Channel, queue: String, f: (String) => Any) extends Actor {

        // called on the initial run
        def receive = {
            case _ => startReceving
        }

        def startReceving = {

            val consumer = new QueueingConsumer(channel);
            channel.basicConsume(queue, true, consumer);

            while (true) {
                // wait for the message
                val delivery = consumer.nextDelivery();
                val msg = new String(delivery.getBody());

                // send the message to the provided callback function
                // and execute this in a subactor
                context.actorOf(Props(new Actor {
                    def receive = {
                        case some: String => f(some);
                    }
                })) ! msg
            }
        }   
    }
}

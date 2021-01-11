import sys
import zmq

def get_socket():
    """ Returns:
            ZeroMQ socket, already connected and subscribed to 'trackingservice' topic
    """
    # Subscribers are created with ZMQ.SUB socket types
    # Socket to talk to server
    socket = zmq.Context().socket(zmq.SUB)
    socket.connect("tcp://localhost:5556")
    # zmq supports filtering of messages based on topics at subscriber side
    socket.setsockopt_string(zmq.SUBSCRIBE, u'trackingservice')
    return socket


def init_service(socket, aggregation_batch=500, msg_limit=None, filter_account_id=None):
    """ Args:
            socket: ZeroMQ socket which receive messages from the queue
            aggregation_batch: frequency of receiving agg updates
            msg_limit: service serving request threshold
            filter_account_id: filter messages based on specific account_id
    """
    counters = dict()
    # Number of messages received
    msg_counter = 0

    while True:
        topic, account_id, timestamp, data = socket.recv_string().split(' ', 3)
        msg_counter += 1

        # Filter by account id
        if not (filter_account_id and filter_account_id != account_id):
            print(data)

            # Display aggregation
            counter = counters.get(account_id, 0) + 1
            if not counter % aggregation_batch:
                print('Aggregation for accountId {}: {} messages received.'.format(account_id, counter))
            counters[account_id] = counter

        # Total amount of messages received
        if msg_limit and msg_counter >= msg_limit:
            print('Initiate service shutdown, limit of {} messages reached.'.format(msg_limit))
            return

# Entry point of program
if __name__ == "__main__":
    socket = get_socket()
    filter_account_id = sys.argv[1] if len(sys.argv) > 1 else None
    # CLI Client subsribed to Pub/Sub system and supports filtering
    # Initialization
    init_service(socket, filter_account_id=filter_account_id)
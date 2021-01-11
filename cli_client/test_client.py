# -*- coding: utf-8 -*-
from pytest import fixture
from cli_client import init_service

@fixture
def socket():
    class Socket(object):
        def recv_string(self):
            return 'trackingservice 101 0123456789 pytest data string'
    return Socket()


def test_client_output_1(capfd, socket):
    """ 
        Case 1: No account id filter is provided
    """
    init_service(socket, aggregation_batch=10, msg_limit=10)
    out, err = capfd.readouterr()

    outcome = ['pytest data string']*10
    outcome.extend([
        'Aggregation for accountId 101: 10 messages received.',
        'Initiate service shutdown, limit of 10 messages reached.'
    ])
    assert out == '\n'.join(outcome) + '\n'

def test_client_output_2(capfd, socket):
    """ 
        Case 2: Neither account id filter not aggregation batch is provided
    """
    init_service(socket, msg_limit=5)
    out, err = capfd.readouterr()

    outcome = ['pytest data string']*5
    outcome.extend(['Initiate service shutdown, limit of 5 messages reached.'])
    assert out == '\n'.join(outcome) + '\n'

def test_client_output_3(capfd, socket):
    """ 
        Case 3: Filter on account id is provided, but no aggregation batch
    """
    init_service(socket, msg_limit=5, filter_account_id=105)
    out, err = capfd.readouterr()

    outcome = [
        'Initiate service shutdown, limit of 5 messages reached.'
    ]
    assert out == '\n'.join(outcome) + '\n'
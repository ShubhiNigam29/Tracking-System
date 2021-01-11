# -*- coding: utf-8 -*-
from cassandra.cluster import Cluster
import time

while True:
    try:
        # Create a cluster instance on local machine
        cluster = Cluster(['127.0.0.1'], port=9042)
        # Session object is required to establish connections and begin executing queries
        session = cluster.connect()
        # Create and set a keyspace for all queries made through this session
        session.execute('CREATE KEYSPACE "accounts" WITH replication = {\'class\':\'SimpleStrategy\', \'replication_factor\' : \'1\'};')
        session.set_keyspace('accounts')
        break
    except:
        time.sleep(5)

# Create table accounts
session.execute('CREATE TABLE accounts(accountId int PRIMARY KEY, accountName text, isActive boolean);')
# Insert into table accounts
if not session.execute('SELECT * FROM accounts;')._current_rows:
    session.execute("INSERT INTO accounts (accountId, accountName, isActive) VALUES (101, 'account1', True);")
    session.execute("INSERT INTO accounts (accountId, accountName, isActive) VALUES (102, 'account2', False);")
    session.execute("INSERT INTO accounts (accountId, accountName, isActive) VALUES (103, 'account3', True);")
    session.execute("INSERT INTO accounts (accountId, accountName, isActive) VALUES (104, 'account4', True);")
    session.execute("INSERT INTO accounts (accountId, accountName, isActive) VALUES (105, 'account5', False);")
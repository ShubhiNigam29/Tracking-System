#!/bin/bash
set -e
cmd="$@"
python cassandra.py
exec $cmd
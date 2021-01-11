#!/bin/bash
set -e
cmd="$@"
python runcassandra.py
exec $cmd
#!/usr/bin/env bash
PIDFILE=/tmp/.mydaemon.pid
COMMAND=/home/alban/lab/port_watch.sh
if [ "$1" = start ]; then
  if test -f "$PIDFILE"; then echo "[!] pid exists, not running.";exit; fi
  echo "[x] Running command $COMMAND"
  echo $$ > "$PIDFILE"
  trap "rm '$PIDFILE'" EXIT SIGTERM
  while true; do
    #launch your app here
    $COMMAND
    wait # needed for trap to work
  done
elif [ "$1" = stop ]; then
  echo "[x] Killing $COMMAND"
  if [ -f $PIDFILE ]; 
	then 
  	kill -9 `cat "$PIDFILE"`
	rm -f $PIDFILE ; 
  fi
#else
#  echo "[x] Starting daemon for $0"
#  nohup "$0" -daemon
fi

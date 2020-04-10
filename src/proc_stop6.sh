#!/usr/bin/ksh
BASE_DIR=/app

HOME_DIR=${BASE_DIR}/cyber_ap

pid=`${HOME_DIR}/classes/proc_check6.sh v|awk '{print $2}'`

echo $pid

kill -9 $pid

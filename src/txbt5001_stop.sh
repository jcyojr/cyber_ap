#!/usr/bin/ksh
BASE_DIR=/app

HOME_DIR=${BASE_DIR}/cyber_ap

pid=`${HOME_DIR}/classes/txbt5001_check.sh v|awk '{print $2}'`

echo $pid

kill -9 $pid

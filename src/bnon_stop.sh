#!/usr/bin/ksh
	KEY=PROD
	BASE_DIR=/app
	

HOME_DIR=${BASE_DIR}/cyber_ap

pid=`${HOME_DIR}/classes/bnon_check.sh v|awk '{print $2}'`

echo $pid

kill -9 $pid

#!/usr/bin/ksh
	KEY=PROD
	BASE_DIR=/app
	
    echo "+===========================================================+"
    echo "+  [$id] is not permited user!!"
    echo "+===========================================================+"

HOME_DIR=${BASE_DIR}/cyber_ap

pid=`${HOME_DIR}/bin/proc_check.sh v|awk '{print $2}'`

echo $pid

kill -9 $pid

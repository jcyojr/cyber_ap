#!/usr/bin/ksh

for KSK in `ps -ef|grep txbt | grep -v grep | awk '{print $2}'`

do
 kill -9 $KSK
done

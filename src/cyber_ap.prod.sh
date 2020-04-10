#!/usr/bin/ksh

BASE_DIR=/app

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=$BASE_DIR/log

${APP_HOME}/classes/proc_stop.sh

CP=$APP_HOME/classes

LIB="$APP_HOME/lib/*.jar"

for LNM in `ls -1 $LIB`
do
        # echo "$LNM"
        CP="$CP:$LNM"
done

echo $CP

nohup /usr/java6_64/bin/java -DPROC=CYBER_AP -cp $CP -Xms256m -Xmx512m com.uc.bs.cyber.starter.Codm_DaemonStart 1>$LOG_DIR/cyber_ap.out 2>&1 &

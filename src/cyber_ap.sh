#!/usr/bin/ksh

BASE_DIR=/app

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=/log/etax

JEUSDIR=/tmax/jeus6

${APP_HOME}/classes/proc_stop.sh


CP=$APP_HOME/classes

LIB="$APP_HOME/lib/*.jar"

for LNM in `ls -1 $LIB`
do
        # echo "$LNM"
        CP="$CP:$LNM"
done

######################################################
## 2011.07.07 --±è´ë¿Ï Ãß°¡
######################################################
LIB="$JEUSDIR/lib/endorsed/*.jar"

for LNM in `ls -1 $LIB`
do
        # echo "$LNM"
        CP="$CP:$LNM"
done
#######################################################


echo $CP

nohup /usr/java6_64/bin/java -DPROC=CYBER_AP -cp $CP -Xms256m -Xmx512m com.uc.bs.cyber.starter.Codm_DaemonStart 1>$LOG_DIR/cyber_ap.out 2>&1 &

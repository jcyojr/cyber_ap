#!/usr/bin/ksh

BASE_DIR=/app

export LANG=ko_KR

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=/log/etax

JEUSDIR=/tmax/jeus6

${APP_HOME}/classes/txbt2411_stop.sh

CP=$APP_HOME/classes

LIB="$APP_HOME/lib/*.jar"

for LNM in `ls -1 $LIB`
do
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

CP="$CP:$APP_HOME/lib/endorsed/ojdbc14.jar"

echo $CP

nohup /usr/java6_64/bin/java -DPROC=TXBT2411_BATCH_AP -cp $CP -Xms512m -Xmx2048m com.uc.bs.cyber.batch.txbt2411.Txbt2411 1>$LOG_DIR/txbt2411_batch_ap.out 2>&1 &

#tail -f $LOG_DIR/txbt2411_batch_ap.out
# /usr/java6_64/bin/java -DPROC=TXBT2411_BATCH_AP -cp $CP -Xms256m -Xmx512m com.uc.bs.cyber.batch.txbt2411.Txbt2411

#!/usr/bin/ksh

BASE_DIR=/app

export LANG=ko_KR

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=/log/etax

JEUSDIR=/tmax/jeus6

${APP_HOME}/classes/txbt2412_stop.sh


CP=$APP_HOME/classes

LIB="$APP_HOME/lib/*.jar"

for LNM in `ls -1 $LIB`
do
        CP="$CP:$LNM"
done

######################################################
## 2011.07.07 --���� �߰�
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

nohup /usr/java6_64/bin/java -DPROC=TXBT2412_BATCH_AP -cp $CP -Xms1024m -Xmx2048m com.uc.bs.cyber.batch.txbt2412.Txbt2412 1>$LOG_DIR/txbt2412_batch_ap.out 2>&1 &
#/usr/java6_64/bin/java -DPROC=TXBT2412_BATCH_AP -cp $CP -Xms256m -Xmx512m com.uc.bs.cyber.batch.txbt2412.Txbt2412
#tail -f $LOG_DIR/txbt2412_batch_ap.out

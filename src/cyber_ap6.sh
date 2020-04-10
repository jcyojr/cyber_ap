#!/usr/bin/ksh

BASE_DIR=/app

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=/log/etax

JEUSDIR=/tmax/jeus6

${APP_HOME}/classes/proc_stop6.sh


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

CP="$CP:$APP_HOME/lib/endorsed/ojdbc14.jar"

echo $CP

export LANG="ko_KR"

nohup /usr/java6_64/bin/java -DPROC=CYBER_AP6 -cp $CP -Xms1024m -Xmx2048m com.uc.bs.cyber.starter.Codm_DaemonBSVIRStart 1>$LOG_DIR/cyber_ap6.out 2>&1 &

tail -f $LOG_DIR/cyber_ap6.out
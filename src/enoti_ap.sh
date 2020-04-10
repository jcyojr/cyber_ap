#!/usr/bin/ksh

BASE_DIR=/app

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=/log/etax

JEUSDIR=/tmax/jeus6

${APP_HOME}/classes/enoti_stop.sh


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

nohup /usr/java6_64/bin/java -DPROC=ENOTI_AP -cp $CP -Xms1024m -Xmx2048m -Xbootclasspath/p:/tmax/jennifer/agent/jennifer.boot.jar:/tmax/jennifer/agent/lwst.jdk.jar:/tmax/jennifer/agent/lwst.boot.jar -Djennifer.config=/tmax/jennifer/agent/D02.conf -javaagent:/tmax/jennifer/agent/lwst.javaagent.jar com.uc.bs.cyber.starter.Codm_EnotiStart 1>$LOG_DIR/cyber_enoti.out 2>&1 &

#tail -f $LOG_DIR/enoti_ap.out

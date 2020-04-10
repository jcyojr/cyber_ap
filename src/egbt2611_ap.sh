#!/usr/bin/ksh

######################################################
## 2014.06.13 --ÀÓÃ¢¼· Ãß°¡
######################################################
if [ $# -ne 4 ] ; then
    echo "Usage: $0 Tax_YYYY(4) Tax_MM(2) Enoti_GB(1) Enoti_SNO(seq)"
    exit 0
else
    TY=$1
    TM=$2
    GB=$3
    SNO=$4
fi
#######################################################

BASE_DIR=/app

APP_HOME=$BASE_DIR/cyber_ap
LOG_DIR=/log/etax

JEUSDIR=/tmax/jeus6

export LANG=ko_KR

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

echo "Tax_YYYY : " $TY " Tax_MM: " $TM "Enoti_GB : "$GB "Enoti_SNO : "$SNO

nohup /usr/java6_64/bin/java -DPROC=EGBT2611_AP -cp $CP -Xms256m -Xmx512m com.uc.bs.cyber.batch.egbt2610.Egbt2611 $TY $TM $GB $SNO 1>$LOG_DIR/egbt2611_ap.out 2>&1 &

#tail -f $LOG_DIR/egbt2611_ap.out

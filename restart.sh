echo '================= check running application ================='
CURRENT_PID=$(pgrep -f okky-copy-*.jar)
if  [ -z "$CURRENT_PID" ]; then
	echo 'application is not running.'
else
	echo  'suspend running application.'
	kill -15 "$CURRENT_PID"
	sleep 10
fi

echo '=================== run application newly ==================='
JAR_NAME='okky-copy-0.0.1-SNAPSHOT.jar'
PROFILE="$1"
KEY="$2"

if  [ -z "$PROFILE" ]; then
  PROFILE='dev'
fi

nohup java -jar -Dspring.profiles.active="$PROFILE" -Djasypt.encryptor.key=$KEY $JAR_NAME > logs/log_$(date +\%Y-\%m-\%d_\%H:\%M).txt 2> errors/error_$(date +\%Y-\%m-\%d_\%H:\%M).txt &
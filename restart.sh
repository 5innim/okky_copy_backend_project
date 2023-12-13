echo '================= check running application ================='
CURRENT_PID=$(pgrep -f okky-copy-*.jar)
if  [ -z "$CURRENT_PID" ]; then
	echo 'application is not running.'
else
	echo  'suspend running application.'
	kill -15 "$CURRENT_PID"
	sleep 5
fi

echo '=================== run application newly ==================='
JAR_NAME='okky-copy-0.0.1-SNAPSHOT.jar'
nohup java -jar -Dspring.profiles.active="$1" $JAR_NAME 2>errors/error_$(date +'%Y-%m-%d_%H:%M').txt &
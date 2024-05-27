#!/bin/bash

#필요한 변수정리
: <<'END'
1.proxy_ip : nginx 22포트와 연결된 Ip
2.target_ip : 배포 대상 ip
3.blue_ports : 블루 컨테이너 포트 2개 (공백으로 구분) 큰따옴표 포함.
4.green_ports : 그린 컨테이너 포트 2개 (공백으로 구분) 큰따옴표 포함.

-- 미확실
5. workspace 경로
END


#현재 가동중인 컨테이너의 색상 확인, 만약 없을시 blue로 진행.
response=$(curl -s http://${proxy_ip}/server)
if [ "$response" = "blue" ]; then
    target_container=green
    current_container=blue
elif [ "$response" = "green" ]; then
    target_container=blue
    current_container=green
else
    target_container=blue
    current_container=black
fi

echo "target_container = ${target_container}"
echo "current_container = ${current_container}"

#서비스 실행에 필요한 .env파일과 docker-compose.yml 전달.
scp -o StrictHostKeyChecking=no /var/lib/jenkins/.env root@${target_ip}:/deploy
scp -o StrictHostKeyChecking=no /var/lib/jenkins/docker-compose-${target_container}.yml root@${target_ip}:/deploy
sh root@${target_ip} "nohup docker compose -f docker-compose-${target_container}.yml up > /dev/null &" &
echo "target_container run"




: <<'END'
target_container에 포함되어있는
포트들을 환경변수로부터 동적으로 받아와야함.

ex)
target_container -> blue
ports --> from {(blue)_ports}
END


# target_container에 해당하는 환경 변수 읽어오기
target_ports_var="${target_container}_ports"

# target_ports_var 변수의 값 읽어오기
target_ports="${!target_ports_var}"

IFS=' ' read -ra ports <<< "${target_ports}"
target_container=blue

#컨테이너 health check 배포서버 두개를 가정하고 있으므로 서버 두개 health check 성공을 확인 해야함.
for retry_count in $(seq 10);do
  server_completed=0
  for port in "${ports[@]}";do
    response=$(curl -s http://${target_ip}:${port}/server)
    address=http://${target_ip}:${port}/server
    echo "${address}"
    if [ "$response" = "$target_container" ] ; then
      echo "${address} server up completed"
      ((server_completed++))
    else
      echo "${address} server not completed yet"
    fi
  done

  echo "${server_completed}"
  if [ $server_completed -eq 2 ] ; then
      echo "container run completed"
      break
  fi

  if [ $retry_count -eq 10 ]
  then
    echo "Health check failed ❌"
    exit 1
  fi

  echo "The server is not alive yet. Retry health check in 5 seconds..."
  sleep 5
done

#nginx switch
echo "set \\\$service_url ${target_container}" | sudo tee /etc/nginx/conf.d/service-url.inc


#현재 컨테이너 다운
if [ "$current_container" != "black" ] ; then
  sh root@${target_ip} "nohup docker compose -f docker-compose-${current_container}.yml down > /dev/null &" &
fi






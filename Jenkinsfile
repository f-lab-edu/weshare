pipeline {
    agent any

    tools {
        jdk("java17")
    }

    stages {
        stage('Build') {
            steps {
                script {
                    // 현재 브랜치 이름을 가져옵니다.
                    def branchName = env.BRANCH_NAME
                    runCI()
                    runCD()
                    // main 브랜치와 나머지 브랜치에 대한 조건을 설정합니다.
//                    if (branchName == 'main') {
//                        echo "Running CI/CD for main branch"
//                        // main 브랜치에 대해 CI/CD 스테이지를 실행합니다.
//                        runCI()
//                        runCD()
//                    } else {
//                        echo "Running CI for branch ${branchName}"
//                        // main 브랜치를 제외한 브랜치에 대해 CI 스테이지만 실행합니다.
//                        runCI()
//                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build 성공'
        }
        failure {
            echo 'Build 실패'
        }
    }
}

def runCI() {
    stage('Checkout Git') {
        steps {
            checkout scm
        }
    }

    stage('Inject .env') {
        steps {
            script {
                def workspace = pwd()
                def envFilePath = "${workspace}/.env"

                def jenkinsEnvFilePath = '/var/lib/jenkins/.env'
                sh "cp ${jenkinsEnvFilePath} ${envFilePath}"
            }
        }
    }

    stage('build') {
        steps {
            echo 'build 수행'
            sh "./gradlew --gradle-user-home=/home/jenkins/gradle clean build"
        }
    }
}

def runCD() {
    stage('configure deploy variables') {
        steps {
            script {
                // BASIC
                PROJECT_NAME = 'weshare'

                // DOCKER
                DOCKER_HUB_URL = 'registry.hub.docker.com'
                DOCKER_HUB_FULL_URL = 'https://' + DOCKER_HUB_URL
                DOCKER_HUB_CREDENTIAL_ID = 'dockerhub-token'
                DOCKER_IMAGE_NAME = PROJECT_NAME
            }
        }
    }

    stage('build') {
        steps {
            echo 'build 수행'
            sh "./gradlew --gradle-user-home=/home/jenkins/gradle clean build"
        }
    }

    stage('Build & Push Docker Image') {
        steps {
            echo 'Build & Push Docker Image'
            withCredentials([usernamePassword(
                    credentialsId: DOCKER_HUB_CREDENTIAL_ID,
                    usernameVariable: 'DOCKER_HUB_ID',
                    passwordVariable: 'DOCKER_HUB_PW')]) {

                script {
                    docker.withRegistry(DOCKER_HUB_FULL_URL,
                            DOCKER_HUB_CREDENTIAL_ID) {
                        app = docker.build(DOCKER_HUB_ID + '/' + DOCKER_IMAGE_NAME)
                        echo 'docker build 완료'

                        app.push(env.BUILD_ID)
                        echo 'docker image push by weshare ${env.BUILD_ID}'

                        app.push('latest')
                        echo 'docker image push by weshare latset'
                    }
                }
            }
        }
    }

    stage('Server Run') {
        steps {
            script {
                def workspace = pwd()
                def jenkinsEnvFilePath = '/var/lib/jenkins/.env'

                sshagent(credentials: ['weshareSSH']) {
                    sh '''#!/bin/bash
                    
                    #필요한 변수정리
                    : <<'END\'
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
                    scp -o StrictHostKeyChecking=no ${jenkinsEnvFilePath} root@${target_ip}:/deploy
                    scp -o StrictHostKeyChecking=no ${workspace}/docker-compose-${target_container}.yml root@${target_ip}:/deploy
                    sh root@${target_ip} "nohup docker compose -f docker-compose-${target_container}.yml up > /dev/null &" &
                    echo "target_container run"
                    
                    : <<'END\'
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
                '''
                }
            }
        }
    }
}


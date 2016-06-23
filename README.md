# jenkins docker
Build Jenkins docker image with needed tools for ChatOps project:
  - nodejs + npm
  - coffee-script
  - mocha
  - jshint
  - coffeelint

## usage
1. run `sudo ./build.sh` to build the docker image
2. edit `init.d/jenkins-docker` file to change the following params (if needed)
  - `JENKINS_NAME=jenkins-master`
  - `DATA_DIR="/data/jenkins_chatops/jenkins"``
  - `SSH_DIR="/root/.ssh"``
  - `DOCKER_IMAGE="jenkins_chatops"``
  - `PORT=80`
  - `DOCKER_PORT=9090`
  - `SLAVE_PORT=50000`
  - `USER_ID=104` user id jenkins data folder belongs to (`id <username>`)
  - `RESTART_OPT="--restart=always"``
3. `sudo cp init.d/jenkins-docker /etc/init.d/`
4. `sudo chmod +x /etc/init.d/jenkins-docker`
4. manage service using `service jenkins-docker start/stop/status`

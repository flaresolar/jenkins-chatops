# jenkins chatops docker
Build Jenkins docker image with needed tools for ChatOps project:
  - nodejs + npm
  - coffee-script
  - mocha
  - jshint
  - coffeelint

## Prerequisites:
1. Github account (github.com or enterprise)
2. Github project with pipeline (from https://github.com/eedevops/he-jenkins-ci)
3. (optional) slack account
4. Host: Docker installed and fully configured

  ### Get Github API token
  1. Verify that you can manage the organization that you want to make CI for
  2. on github, go to your profile `settings`
  3. "Personal access tokens"
  4. "Generate new token"
  5. give token description
  6. Select all `repo` scopes
  7. Click "Generate token" and save the token on your computer

  ### Get SLACK app token
  1. Create slack team (https://slack.com/)
  2. In slack chat ui click on the team name(top left)
  3. Click on "Apps and Integrations"
  4. Search for "Jenkins CI" app
  5. Follow the wizard to add the app
  6. Write down app API token, your team name, and rooms jenkins will report to

## build
1. run `sudo ./build.sh` to build the docker image

## run
1. First run (or run without local jenkins folder)
  1. (optional) create folder for jenkins to store data
   1. create jenkins user `sudo adduser jenkins`
   2. chown the folder to user jenkins

     ```bash
     sudo chmod g+s <LOCAL_DIR>
     sudo chown -R jenkins: <LOCAL_DIR>
     ```

   3. get jenkins **group** id `id jenkins`
  2. run (replace `${*}` with real values):

    ```bash
      docker run -p 80:9090 -p 50000:50000 -v <LOCAL_DIR>:/var/jenkins_home -u :<GID> \
      -e "ADMIN_PW=<ADMIN_PW>" \
      -e "GH_TOKEN=<GH_TOKEN>" \
      -e "GHE_TOKEN=<GHE_TOKEN>" \
      -e "GIT_KEY=$(cat ~/.ssh/id_rsa)" \
      -e "SLACK_CONF=<TEAM>|<TOKEN>|<ROOMS>" \
      -e "http_proxy=$http_proxy" -e "https_proxy=$http_proxy" \
      -e "no_proxy=$no_proxy"  \
      -e "ORGANIZATION=<ORG_OR_USERNAME}><^+ENTERPRISE_URL>" \
      -e 'REGEX=<REGEX>' -e "HOST=http://<HOSTNAME||IP>:<PORT_IF_NOT_80>" \
      -e JAVA_OPTS="-Dhttp.proxyHost=<proxy_host without http[s]:// prefix>  -Dhttp.proxyPort=<proxy_port>  -Dhttps.proxyHost=<proxy_host without http[s]:// prefix> -Dhttps.proxyPort=<proxy_port>" \
      jenkins_chatops
    ```
    example:

    ```bash
    docker run -p 80:9090 -p 50000:50000 -v /data/jenkins:/var/jenkins_home -u :105 \
    -e "ADMIN_PW=123" \
    -e "GH_TOKEN=dlkfajhslkfjh" \
    -e "GHE_TOKEN=jdglmnmnbdajhdg"
    -e "GIT_KEY=$(cat ~/.ssh/id_rsa)" \
    -e "SLACK_CONF=myteam|xox-fkjhjhk|#general" \
    -e "http_proxy=$http_proxy" -e "https_proxy=$http_proxy" \
    -e "no_proxy=$no_proxy"  \
    -e "ORGANIZATION=myOrg^github.myorg.org" \
    -e 'REGEX=hubot-.*' -e "HOST=http://myserver.acme.com" \
    -e JAVA_OPTS="-Dhttp.proxyHost=<proxy_host without http[s]:// prefix>  -Dhttp.proxyPort=<proxy_port>  -Dhttps.proxyHost=<proxy_host without http[s]:// prefix> -Dhttps.proxyPort=<proxy_port>" \
    jenkins_chatops
    ```

  3. (if using local folder) log in to jenkins server with `admin` username and provided password and stop the docker image
  4. (if using local folder) run image without secret variables:

    ```bash
    docker run -p 80:9090 -p 50000:50000 \
    -v <LOCAL_DIR>:/var/jenkins_home \
    -e "http_proxy=$http_proxy" -e "https_proxy=$http_proxy" \
    -e JAVA_OPTS="-Dhttp.proxyHost=<proxy_host without http[s]:// prefix>  -Dhttp.proxyPort=<proxy_port>  -Dhttps.proxyHost=<proxy_host without http[s]:// prefix> -Dhttps.proxyPort=<proxy_port>" \
    -u :<GID> jenkins_chatops
    ```

2. edit `init.d/jenkins-docker` file to change the following params (if needed)
  - `JENKINS_NAME=jenkins-master`
  - `DATA_DIR="<LOCAL_DIR>"``
  - `DOCKER_IMAGE="jenkins_chatops"``
  - `PORT=80`
  - `DOCKER_PORT=9090`
  - `SLAVE_PORT=50000`
  - `GROUP_ID=<JENKINS_GROUP_ID>` group id jenkins data folder belongs to (`id <username>`)
  - `RESTART_OPT="--restart=always"``
3. `sudo cp init.d/jenkins-docker /etc/init.d/`
4. `sudo chmod +x /etc/init.d/jenkins-docker`
5. manage service using `sudo service jenkins-docker start/stop/status`

## Dockerized:
1. plugins
2. skip setup wizard
3. 5 executors
4. credentials for pipeline (github-token, github-enterprise-token, github-ssh)
  - created if passed `GH_TOKEN`, `GHE_TOKEN`, `GIT_KEY`, default is `PLACEHOLDER`
5. Create admin user, default password 'admin' unless `ADMIN_PW` env var passed
6. Github enterprise endpoint creation if `^enterprise.url` passed in `ORGANIZATION` env var
7. Slack notifications configuration if `SLACK_CONF` passed
8. proxy auto setup (if `http_proxy` env var is set)
9. set jenkins root url if `HOST` env var is set
10. **create github organization folder** if `ORGANIZATION` env var passed

## Testing [linux]:
1. Download and install bats testing framework: https://github.com/sstephenson/bats
2. run `bats tests/tests.bat`

## DISCLAIMER
Currently jenkins-chatops support slack platform, other platforms might be added later on.

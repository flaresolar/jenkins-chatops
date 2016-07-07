#!/usr/bin/env bats

SUT_IMAGE=jenkins_chatops
SUT_CONTAINER=bats-jenkins

#TEST PARAMS
ADMIN_PW=testpw
GH_TOKEN=123
GHE_TOKEN=321
GIT_KEY=aaa
SLACK_CONF="TM|TK|#RM"
TESTORG="TESTORG"
TEST_GH="github.acme.com"
ORGANIZATION="${TESTORG}^${TEST_GH}"
REGEX="hubot-.*"
HOST="http://$(ifconfig | awk '/inet addr/{print substr($2,6)}' | head -2 | tail -1)"

load 'test_helper/bats-support/load'
load 'test_helper/bats-assert/load'
load test_helpers

@test "build image" {
  cd $BATS_TEST_DIRNAME/..
  run docker build -t $SUT_IMAGE --build-arg "http_proxy=$http_proxy" \
    --build-arg "https_proxy=$http_proxy" --build-arg "no_proxy=$no_proxy" .
  assert_success
}

@test "clean test containers" {
    cleanup $SUT_CONTAINER
}

@test "create test container" {
  run docker run -d -e "ADMIN_PW=${ADMIN_PW}" \
    -e "GH_TOKEN=${GH_TOKEN}" \
    -e "GHE_TOKEN=${GHE_TOKEN}" \
    -e "GIT_KEY=${GIT_KEY}" \
    -e "SLACK_CONF=${SLACK_CONF}" \
    -e "http_proxy=$http_proxy" -e "https_proxy=$http_proxy" \
    -e "no_proxy=$no_proxy"  \
    -e "ORGANIZATION=${ORGANIZATION}" \
    -e "REGEX=${REGEX}" -e "HOST=${HOST}" \
    --name $SUT_CONTAINER -P $SUT_IMAGE
  assert_success
}

@test "test container is running" {
  sleep 1  # give time to eventually fail to initialize
  retry 3 1 assert "true" docker inspect -f {{.State.Running}} $SUT_CONTAINER
}

@test "Jenkins is initialized" {
    retry 30 5 test_url ${ADMIN_PW} /api/json
}

@test "plugins are installed" {
  run bash -c "docker run -i --rm $SUT_IMAGE ls /var/jenkins_home/plugins | sed -e 's/  / /'"
  assert_success
  for line in $(cat config/plugins.txt)
  do
    plugin=$(echo $line | cut -d : -f1)
    assert_output --partial "${plugin}.jpi"
    assert_output --partial "${plugin}.jpi.pinned"
  done

}

@test 'job created' {
  get_url ${ADMIN_PW} "/job/${TESTORG}/config.xml"
  assert_output --partial "repoOwner>${TESTORG}<"
  assert_output --partial "apiUri>https://${TEST_GH}/api/v3/<"
  assert_output --partial "scanCredentialsId>github-enterprise-token<"
  assert_output --partial "pattern>${REGEX}<"
}

@test 'credentials exist' {
  get_url ${ADMIN_PW} "/credentials/store/system/domain/_/credential/github-ssh/api/json?pretty=true"
  assert_output --partial '"typeName" : "SSH Username with private key"'
  get_url ${ADMIN_PW} "/credentials/store/system/domain/_/credential/github-enterprise-token/api/json?pretty=true"
  assert_output --partial '"typeName" : "Username with password"'
  get_url ${ADMIN_PW} "/credentials/store/system/domain/_/credential/github-token/api/json?pretty=true"
  assert_output --partial '"typeName" : "Username with password"'
}

@test "clean test containers" {
    cleanup $SUT_CONTAINER
}

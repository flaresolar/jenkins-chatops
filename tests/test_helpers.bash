#!/bin/bash
#
# Copyright 2016 Hewlett-Packard Development Company, L.P.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# Software distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.
#
# Code modified from tests/test_helpers.bash obtained from  https://github.com/jenkinsci/docker

# check dependencies
(
    type docker &>/dev/null || ( echo "docker is not available"; exit 1 )
    type curl &>/dev/null || ( echo "curl is not available"; exit 1 )
)>&2

# Assert that $1 is the outputof a command $2
function assert {
    local expected_output=$1
    shift
    local actual_output
    actual_output=$("$@")
    actual_output="${actual_output//[$'\t\r\n']}" # remove newlines
    if ! [ "$actual_output" = "$expected_output" ]; then
        echo "expected: \"$expected_output\""
        echo "actual:   \"$actual_output\""
        false
    fi
}

# Retry a command $1 times until it succeeds. Wait $2 seconds between retries.
function retry {
    local attempts=$1
    shift
    local delay=$1
    shift
    local i

    for ((i=0; i < attempts; i++)); do
        run "$@"
        if [ "$status" -eq 0 ]; then
            return 0
        fi
        sleep $delay
    done

    echo "Command \"$*\" failed $attempts times. Status: $status. Output: $output" >&2
    false
}

function get_jenkins_url {
    # DOCKER_HOST crashing docker in dockerized jenkins, using PARENT_HOST
    if [ -z "${PARENT_HOST}" ]; then
        DOCKER_IP=localhost
    else
        DOCKER_IP=$(echo "$PARENT_HOST" | sed -e 's|tcp://\(.*\):[0-9]*|\1|')
    fi
    echo "http://$DOCKER_IP:$(docker port "$SUT_CONTAINER" 9090 | cut -d: -f2)"
}

function get_jenkins_password {
    docker logs "$SUT_CONTAINER" 2>&1 | grep -A 2 "Please use the following password to proceed to installation" | tail -n 1
}

function get_url {
  run curl --user "admin:${1}" --fail --connect-timeout 30 --max-time 60 "$(get_jenkins_url)$2"
}

function test_url {
    run curl --user "admin:${1}" --output /dev/null --silent --head --fail --connect-timeout 30 --max-time 60 "$(get_jenkins_url)$2"
    if [ "$status" -eq 0 ]; then
        true
    else
        echo "URL $(get_jenkins_url)$2 failed" >&2
        echo "output: $output" >&2
        false
    fi
}

function cleanup {
    docker kill "$1" &>/dev/null ||:
    docker rm -fv "$1" &>/dev/null ||:
}

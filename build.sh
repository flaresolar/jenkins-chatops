#!/bin/bash
docker build -t jenkins_chatops --build-arg "http_proxy=$http_proxy" \
  --build-arg "https_proxy=$http_proxy" --build-arg "no_proxy=$no_proxy" .

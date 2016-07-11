/**
 Copyright 2016 Hewlett-Packard Development Company, L.P.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 Software distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License.
*/

def wraps(body) {
    wrap([$class: 'AnsiColorBuildWrapper', 'colorMapName': 'XTerm',
      'defaultFg': 1, 'defaultBg': 2]) {
        wrap([$class: 'TimestamperBuildWrapper']) {
            body()
        }
    }
}

node {
  wraps {
    stage 'Clean workspace'
    deleteDir()

    stage 'Checkout'
    checkout scm
	sh 'git submodule update --init --recursive'

    stage 'Build'
	// retry because of bad internet here
	retry(3) {
      sh "./build.sh"
    }

    stage 'Test'
    checkout([$class: 'GitSCM', branches: [[name: '*/master']],
	  doGenerateSubmoduleConfigurations: false,
	  extensions: [[$class: 'RelativeTargetDirectory',
	  relativeTargetDir: 'bats']], submoduleCfg: [],
	  userRemoteConfigs: [[url: 'https://github.com/sstephenson/bats.git']]])
    sh "bats/bin/bats tests/tests.bats"
  }
}

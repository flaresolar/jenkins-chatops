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
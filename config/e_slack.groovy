/*
 * Copyright 2016 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * Software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

import jenkins.model.*
import hudson.security.*
import hudson.model.User
def env = System.getenv()

def slack = Jenkins.instance.getExtensionList(
  jenkins.plugins.slack.SlackNotifier.DescriptorImpl.class)[0]
//valid tokens for testing in the jenkins-slack-plugin-test instance of slack.com

def slack_params = env['SLACK_CONF'] ? env['SLACK_CONF'].tokenize('|') : []

if ((slack_params.size() ==3) && !slack.getTeamDomain())
{
  def params = [
    slackTeamDomain: slack_params[0],
    slackToken: slack_params[1],
    slackRoom: slack_params[2],
    slackBuildServerUrl: '',
    slackSendAs: ''
  ]
  def req = [
  getParameter: { name -> params[name] }
  ] as org.kohsuke.stapler.StaplerRequest
  slack.configure(req, null)
  println('configured')
}

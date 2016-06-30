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

def env = System.getenv();

def project = ''
def gitUrl = ''
def searchTerm = env['REGEX'] ? env['REGEX'] : '.*'

if (env['ORGANIZATION'])
{
  def GH = env['ORGANIZATION'].tokenize('^')
  project = GH[0]
  gitUrl = GH.size() > 1 ? GH[1] : 'github.com'
}

def gitToken = gitUrl == 'github.com' ? 'github-token' :
  'github-enterprise-token'
def gitSsh = 'github-ssh'
def apiUrl = gitUrl == 'github.com' ? '' :
  '<apiUri>https://'+gitUrl+'/api/v3/</apiUri>'

if (!hudson.model.Hudson.instance.getItem(project))
{
  def configXml = new File('/var/tmp/templates/githubOrg.xml').getText('UTF-8')
    .replaceAll( '__ORG__', project )
    .replaceAll('__GIT_URL__', 'https://'+gitUrl)
    .replace('__REGEX__', searchTerm)
    .replaceAll('__GIT_TOKEN__', gitToken).replaceAll('__GIT_SSH__', gitSsh)
    .replaceAll('__API_URL__', apiUrl).replaceAll('__AVATAR__', '')
  def xmlStream = new ByteArrayInputStream(configXml.getBytes())
  Jenkins.instance.createProjectFromXML(project, xmlStream)
}

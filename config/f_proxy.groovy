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

def instance = Jenkins.getInstance()

def env = System.getenv()

if (env['http_proxy'])
{
  def proxy_uri = new URI(env['http_proxy'])
  println(env['no_proxy'])
  def name = proxy_uri.host
  def userName = ''
  def password = ''
  if (proxy_uri.userInfo)
  {
    def uinfo = proxy_uri.userInfo.tokenize(':')
    userName = uinfo[0]
    userName = uinfo.size() > 1 ? uinfo[1] : ''
  }
  def port = proxy_uri.port ? proxy_uri.port : ''
  def noProxyHost = env['no_proxy'] ? env['no_proxy'] : ''
  def pc = new hudson.ProxyConfiguration(name, port, userName, password, noProxyHost)
  instance.proxy = pc
  instance.save()
  println "Proxy settings updated!"
}

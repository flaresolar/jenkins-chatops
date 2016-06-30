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
def instance = Jenkins.getInstance()

// create admin user if not exists
if (User.get('admin', false) == null)
{
  def hudsonRealm = new HudsonPrivateSecurityRealm(false)
  hudsonRealm.createAccount("admin",
    env['ADMIN_PW'] ? env['ADMIN_PW'] : 'admin')
  instance.setSecurityRealm(hudsonRealm)

  def strategy = new GlobalMatrixAuthorizationStrategy()
  strategy.add(Jenkins.ADMINISTER, "admin")
  instance.setAuthorizationStrategy(strategy)
  instance.save()
}

//def strategy = new hudson.security.FullControlOnceLoggedInAuthorizationStrategy()
//strategy.setAllowAnonymousRead(false)
//instance.setAuthorizationStrategy(strategy)

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

import jenkins.model.*;
import org.jenkinsci.plugins.github_branch_source.*;
import java.util.*;
import java.util.logging.Logger;

def env = System.getenv()

def find_endpoint(list, url){
  return list.any {it.getApiUri() == url}
}

Logger logger = Logger.getLogger("github-enterprise-api-endpoint")
logger.info("about to add GHE API endpoint")

def GH = env['ORGANIZATION'] ? env['ORGANIZATION'].tokenize('^') : []

GitHubConfiguration gitHubConfig = GlobalConfiguration.all().get(GitHubConfiguration.class)
endpointList = gitHubConfig.getEndpoints().collect()

new_url = GH.size() > 1 ? GH[1] : null

if (new_url && !find_endpoint(gitHubConfig.getEndpoints(), new_url)){
  Endpoint gheApiEndpoint = new Endpoint("https://"+new_url+"/api/v3/", new_url)
  endpointList.add(gheApiEndpoint)
  gitHubConfig.setEndpoints(endpointList)
  logger.info("added GHE API endpoint")
}

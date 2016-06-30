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

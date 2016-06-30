import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret
import hudson.plugins.sshslaves.*
import org.apache.commons.fileupload.*
import org.apache.commons.fileupload.disk.*
import java.nio.file.Files

def env = System.getenv()

def check_cred(cred_id){
  def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials.class,
    jenkins.model.Jenkins.instance)
    return (creds.findResult { it.id == cred_id ? it : null } == null)
}


domain = Domain.global()
store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

if (check_cred('github-token')){
  def token = env['GH_TOKEN'] ? env['GH_TOKEN'] : 'PLACEHOLDER'
  githubToken = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
    "github-token", "API Token for github.com", "", token)
  store.addCredentials(domain, githubToken)
}

if (check_cred('github-enterprise-token')){
  def token = env['GHE_TOKEN'] ? env['GHE_TOKEN'] : 'PLACEHOLDER'
  gheToken = new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL, "github-enterprise-token",
    "API Token for github enterprise", "", token)
  store.addCredentials(domain, gheToken)
}

if (check_cred('github-ssh')){
  def private_key = env['GIT_KEY'] ? env['GIT_KEY'] : 'PLACEHOLDER'
  gitSSH = new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL, "github-ssh",
    "github-ssh",
    new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(private_key), "", "")
  store.addCredentials(domain, gitSSH)
}

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

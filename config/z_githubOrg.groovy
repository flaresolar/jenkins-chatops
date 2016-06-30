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

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

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

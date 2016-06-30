import jenkins.model.*
env = System.getenv()
Jenkins.instance.setNumExecutors(5)

jlc = JenkinsLocationConfiguration.get()
if (env['HOST'] && !jlc.getUrl()){
  jlc.setUrl(env['HOST'])
  jlc.save()
}

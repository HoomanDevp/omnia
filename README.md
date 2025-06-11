### Java Spring template project

This project is based on a GitLab [Project Template](https://docs.gitlab.com/ee/gitlab-basics/create-project.html).

Improvements can be proposed in the [original project](https://gitlab.com/gitlab-org/project-templates/spring).

### CI/CD with Auto DevOps

This template is compatible with [Auto DevOps](https://docs.gitlab.com/ee/topics/autodevops/).

If Auto DevOps is not already enabled for this project, you can [turn it on](https://docs.gitlab.com/ee/topics/autodevops/#enabling-auto-devops) in the project settings.


### Add Splunk java logging jar manually
- Download it from:

  https://splunk.jfrog.io/ui/native/ext-releases-local/com/splunk/logging/splunk-library-javalogging/1.11.8/
- Add it to maven with command:

  `mvn install:install-file -Dfile=path/to/splunk-library-javalogging-1.11.8.jar -DgroupId=com.splunk.logging -DartifactId=splunk-library-javalogging -Dversion=1.11.8 -Dpackaging=jar`
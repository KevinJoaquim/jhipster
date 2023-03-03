node{
  def app

    stage('Clone') {
        checkout scm
    }

    stage('Build image') {
        app = docker.build("kev/nginx")
    }

    stage('Test image') {
        docker.image('kev/nginx').withRun('-p 90:80') { c ->
        sh 'docker ps'
        sh 'curl localhost'
	     }
    }
}

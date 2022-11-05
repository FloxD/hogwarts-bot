pipeline {
  agent any
  environment {
    KUBECONFIG = "/etc/kubernetes/admin.conf"
    name = "hogwarts-bot"
    image = "hogwarts-bot:0.0.1-${env.BUILD_ID}"
    dockerPort = "8080"
    hostPort = "30170"
  }
  stages {
    stage('Build') {
      steps {
        sh "docker build -f Dockerfile -t ${image} ."
      }
    }

    stage('Deploy') {
      steps {
        sh "sed -i 's/{name}/${name}/g;' deployment.yml"
        sh "sed -i 's/{image}/${image}/g;' deployment.yml"
        sh "sed -i 's/{dockerPort}/${dockerPort}/g;' deployment.yml"
        sh "sed -i 's/{hostPort}/${hostPort}/g;' deployment.yml"
        sh "kubectl apply -f deployment.yml"
      }
    }
  }
}

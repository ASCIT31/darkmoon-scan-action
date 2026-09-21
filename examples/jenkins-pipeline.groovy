// Autonomous pentest in CI/CD, Jenkins pipeline stage (open source, no license key).
//
// This declarative Jenkinsfile runs the open source Darkmoon CLI as a security
// stage against an AUTHORIZED staging URL. It finds, exploits and proves each
// issue, runs fully self hosted, and its Privacy Gateway tokenizes sensitive
// target values locally before any cloud model sees them. Scheduled runs are
// driven by the pipeline trigger below; the web dashboard and finding-to-fix
// remediation pull requests are Darkmoon Pro (paid) features. This stage uses the
// open source CLI only.
//
// Prerequisites:
//   - An agent with Docker and Docker Compose v2, and enough resources for the
//     Darkmoon stack (it is resource heavy).
//   - Jenkins credentials (Secret text):
//       openrouter-api-key  -> your LLM provider API key
//       opencode-model      -> model id (an Opus class model is recommended)
//   - A STAGING_URL parameter or environment value pointing at the authorized
//     staging URL you are allowed to test.
//
// Only test targets you are explicitly authorized to test.

pipeline {
  agent { label 'docker' }   // an agent with Docker + Compose v2 and enough resources

  parameters {
    string(name: 'STAGING_URL', defaultValue: 'https://staging.example.test',
           description: 'Authorized staging URL to test')
  }

  // Scheduled run: nightly at 03:00. Remove if you only run on deploy.
  triggers { cron('H 3 * * *') }

  environment {
    OPENROUTER_PROVIDER = 'openrouter'
    OPENROUTER_API_KEY  = credentials('openrouter-api-key')
    OPENCODE_MODEL      = credentials('opencode-model')
  }

  stages {
    // Replace or precede this with your normal deploy stage; run the pentest
    // after the target is deployed and reachable.
    stage('Darkmoon autonomous pentest') {
      steps {
        sh '''
          set -e
          git clone --depth 1 https://github.com/ASCIT31/Dark-Moon.git darkmoon
          cd darkmoon
          chmod +x install.sh
          ./install.sh
          ./darkmoon.sh run --agent pentest "TARGET: ${STAGING_URL} PROGRAM=\\"Staging (authorized)\\""
          mkdir -p "${WORKSPACE}/reports"
          for f in $(docker exec opencode bash -c "ls /opt/darkmoon/mcp/server/data/reports/*.md 2>/dev/null"); do
            docker exec opencode cat "$f" > "${WORKSPACE}/reports/$(basename "$f")"
          done
          ls -la "${WORKSPACE}/reports" || true
        '''
      }
    }
  }

  post {
    always {
      // Publish the Markdown findings reports as build artifacts.
      archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
    }
  }
}

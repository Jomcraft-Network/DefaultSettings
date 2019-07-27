pipeline 
{
	agent any
	options
	{
		gitLabConnection('Jenkins')
		gitlabBuilds(builds: ['Checkout', 'Build', 'Deploy'])
	}
	stages 
	{
		stage('Checkout')
		{
			steps
			{
				gitlabCommitStatus("Checkout")
				{
				    checkout scm
				}
			}
		}
		stage('Build') 
		{
			steps 
			{
				gitlabCommitStatus('Build')
				{
				    sh 'chmod +x gradlew'
                    sh './gradlew build publish'

				}
			}
			
		}
		stage('Deploy')
		{
			steps
			{
				gitlabCommitStatus('Deploy')
				{
                    cleanWs()
				}
			}
		}
	}
}
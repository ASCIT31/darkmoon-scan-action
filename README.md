# Darkmoon Autonomous Pentest (GitHub Action)

Run [Darkmoon](https://github.com/ASCIT31/Dark-Moon), the open source (GPLv3) autonomous penetration testing platform, against an authorized target inside your CI, and upload the findings report as a build artifact.

Darkmoon runs 50 specialist security agents over MCP orchestration, driving 50+ offensive tools across web, API, Active Directory, Kubernetes, cloud, CMS and network targets. Every finding ships with reproducible proof of exploitation. It is fully self hosted, and its Privacy Gateway tokenizes sensitive target values before any cloud model sees them, so real IPs, hostnames and credentials stay on your machine.

## No license required

This action uses the open source distribution. It does not require any Darkmoon license key. You only provide your own LLM provider API key as a secret.

## Usage

```yaml
name: darkmoon-scan
on:
  workflow_dispatch:
    inputs:
      target:
        description: 'Authorized target'
        required: true
        default: 'TARGET: https://juice-shop.local'

jobs:
  pentest:
    runs-on: self-hosted   # a runner with Docker and enough resources for the stack
    steps:
      - uses: ASCIT31/darkmoon-scan-action@v1
        with:
          target: ${{ inputs.target }}
          provider: openrouter
          model: ${{ secrets.OPENCODE_MODEL }}
          api_key: ${{ secrets.OPENROUTER_API_KEY }}
```

## Inputs

| Input | Required | Description |
| --- | --- | --- |
| `target` | yes | Authorized target, for example `TARGET: https://example.test` or `TARGET: 10.0.4.12`. |
| `model` | yes | Model id for the reasoning engine. An Opus class model is recommended for full autonomous campaigns. |
| `api_key` | yes | LLM provider API key, provided as a repository secret. |
| `provider` | no | LLM provider (`openrouter`, `anthropic`, `openai`, or a local endpoint). Default `openrouter`. |
| `ref` | no | Darkmoon git ref to check out. Default `main`. |

## Requirements and limitations

* Docker and Docker Compose v2 on the runner.
* An LLM provider API key. A capable model is needed for a full campaign. Small local models will not finish an autonomous campaign.
* The stack is resource heavy. A self hosted runner or a large runner is recommended rather than a standard hosted runner.
* Only run this against targets you are explicitly authorized to test. You are responsible for authorization and scope.

## How it works

1. Clones the open source Darkmoon repository.
2. Runs `install.sh` non interactively using the provider settings from your secrets, which pulls the public `ascit/darkmoon:latest` image and starts the Compose stack.
3. Runs `darkmoon.sh run --agent pentest "<target>"`.
4. Collects the Markdown findings reports from the container and uploads them as an artifact.

## License

GPLv3, same as Darkmoon.

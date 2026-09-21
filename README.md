<div align="center">

<a href="https://github.com/ASCIT31/Dark-Moon"><img src=".github/assets/darkmoon-banner.png" alt="Darkmoon, autonomous AI penetration testing" width="100%"></a>

# Darkmoon Autonomous Pentest (GitHub Action)

### Part of [Darkmoon, the open source autonomous AI penetration testing platform](https://github.com/ASCIT31/Dark-Moon)

[![Star Dark-Moon](https://img.shields.io/github/stars/ASCIT31/Dark-Moon?style=social)](https://github.com/ASCIT31/Dark-Moon)

[![License GPLv3](https://img.shields.io/badge/license-GPLv3-0A2472)](https://github.com/ASCIT31/Dark-Moon) [![No license key required](https://img.shields.io/badge/open%20source-no%20license%20key-2667FF)](#no-license-required) [![Self-hosted runner](https://img.shields.io/badge/runs%20on-self--hosted%20CI-87BFFF)](#requirements-and-limitations) [![Website](https://img.shields.io/badge/site-dark--moon.org-0A2472)](https://dark-moon.org)

</div>

If Darkmoon is useful, a star on the [main repo](https://github.com/ASCIT31/Dark-Moon) helps others find it.

Run [Darkmoon](https://github.com/ASCIT31/Dark-Moon), the open source (GPLv3) autonomous penetration testing platform, against an authorized target inside your CI, and upload the findings report as a build artifact.

<div align="center">

<img src=".github/assets/cli_log.png" alt="Darkmoon open source CLI, live terminal log of a pentest run streaming its commands" width="90%">

<sub>The open source Darkmoon CLI, <code>darkmoon.sh --log</code> streaming a run's live output, the same terminal engine this Action drives inside your CI.</sub>

<br>

<img src=".github/assets/dashboard.png" alt="Pro web dashboard, Darkmoon campaigns and severity breakdown" width="90%">

<sub><b>Pro:</b> the paid Darkmoon Pro web dashboard, campaigns, severity breakdown and findings. This Action uses the open source CLI and needs no license key.</sub>

<sub><b>Web dashboard and remediation are Darkmoon Pro (paid) features; the open source edition is the CLI shown above.</b></sub>

</div>

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

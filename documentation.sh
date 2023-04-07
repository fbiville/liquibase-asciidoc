#!/usr/bin/env bash

set -euxo pipefail

docker build \
  --file documentation.Dockerfile \
  --tag fbiville/liquibase-asciidoc-docs:latest \
  .

# note to Mac OS users: you may need to add 'IgnoreUnknown UseKeychain' at the top of your ~/.ssh/config file
docker run \
  --pull never \
  --publish 8000:8000 \
  --volume "$(pwd)":/usr/src/app \
  --volume "$HOME"/.ssh/:/root/.ssh/ \
   fbiville/liquibase-asciidoc-docs:latest \
   ${1:-}

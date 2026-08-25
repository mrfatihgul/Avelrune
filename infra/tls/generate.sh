#!/bin/sh
set -e
cd "$(dirname "$0")"
openssl req -x509 -newkey rsa:2048 -sha256 -days 825 -nodes \
  -keyout localhost.key \
  -out localhost.crt \
  -config openssl.cnf
echo "Wrote infra/tls/localhost.crt and localhost.key"

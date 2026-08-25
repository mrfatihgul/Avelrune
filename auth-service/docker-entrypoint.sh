#!/bin/sh
set -e
mkdir -p /app/keys
chown -R app:app /app/keys
exec gosu app java -jar /app/app.jar

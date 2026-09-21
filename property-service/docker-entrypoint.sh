#!/bin/sh
set -e
MEDIA_DIR="${APP_MEDIA_DIR:-/app/media}"
mkdir -p "$MEDIA_DIR"
chown -R app:app "$MEDIA_DIR"
exec gosu app java -jar /app/app.jar

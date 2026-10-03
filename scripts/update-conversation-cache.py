#!/usr/bin/env python3
"""Copy user-facing Codex message events without rewriting their content."""

import datetime as dt
import json
import re
from pathlib import Path
from zoneinfo import ZoneInfo


SESSIONS = Path.home() / ".codex" / "sessions"
OUTPUT = Path(__file__).resolve().parents[1] / ".conversation-cache" / "all-codex-messages.jsonl"
THREAD_ID = re.compile(r"[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}")


def collect_messages():
    messages = []
    for session in SESSIONS.rglob("*.jsonl"):
        match = THREAD_ID.search(session.name)
        if not match:
            continue
        with session.open(encoding="utf-8") as stream:
            first = json.loads(stream.readline())
        if isinstance(first.get("payload", {}).get("source"), dict):
            continue  # Internal guardian/subagent transcripts are not user chats.
        for line in session.open(encoding="utf-8"):
            try:
                event = json.loads(line)
            except json.JSONDecodeError:
                continue
            item = event.get("payload", {})
            if event.get("type") != "response_item" or item.get("type") != "message":
                continue
            if item.get("role") not in ("user", "assistant"):
                continue
            turn_id = item.get("internal_chat_message_metadata_passthrough", {}).get("turn_id")
            if not turn_id or not item.get("id"):
                continue
            timestamp = dt.datetime.fromisoformat(event["timestamp"].replace("Z", "+00:00"))
            messages.append({
                "timestampUTC": event["timestamp"],
                "timestampIstanbul": timestamp.astimezone(ZoneInfo("Europe/Istanbul")).isoformat(timespec="milliseconds"),
                "sessionFile": session.name,
                "ordinal": event.get("ordinal"),
                "payload": item,
            })
    return sorted(messages, key=lambda row: (row["timestampUTC"], row["sessionFile"], row["ordinal"] or 0))


def main():
    messages = collect_messages()
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    temporary = OUTPUT.with_suffix(".tmp")
    with temporary.open("w", encoding="utf-8") as stream:
        for message in messages:
            stream.write(json.dumps(message, ensure_ascii=False) + "\n")
    temporary.replace(OUTPUT)
    print(f"{len(messages)} messages saved to {OUTPUT}")


if __name__ == "__main__":
    main()

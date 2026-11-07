"use client";

import { useId, useRef, useState } from "react";
import { apiAuthFetch } from "@/lib/api";
import { ContactMessage, InquiryStatus } from "@/lib/types";

const statuses: { value: InquiryStatus; label: string }[] = [
  { value: "UNDERREVIEW", label: "İnceleniyor" },
  { value: "SOLVED", label: "Çözüldü" },
  { value: "UNSOLVED", label: "Çözülmedi" },
];

export function InquiryActions({ message, onUpdated }: {
  message: ContactMessage;
  onUpdated: (message: ContactMessage) => void;
}) {
  const selectId = useId();
  const inFlight = useRef(false);
  const [pending, setPending] = useState<"status" | "read" | null>(null);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");

  async function update(action: "status" | "read", status?: InquiryStatus) {
    if (inFlight.current) return;
    inFlight.current = true;
    setPending(action);
    setError("");
    setNotice("");
    try {
      const updated = await apiAuthFetch<ContactMessage>(
        `/api/inquiries/${message.id}/${action === "status" ? "update" : "read"}`,
        { method: "PATCH", ...(action === "status" ? { body: JSON.stringify({ status }) } : {}) },
      );
      onUpdated(updated);
      setNotice(action === "status" ? "Talep durumu kaydedildi." : "Okundu olarak işaretlendi.");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Talep güncellenemedi. Tekrar deneyin.");
    } finally {
      inFlight.current = false;
      setPending(null);
    }
  }

  return (
    <div className="inquiry-actions" aria-busy={pending !== null}>
      <div className="field">
        <label htmlFor={selectId}>Talep durumu</label>
        <select
          id={selectId}
          value={message.status}
          disabled={pending !== null}
          onChange={(event) => {
            const status = event.target.value as InquiryStatus;
            if (status !== message.status) void update("status", status);
          }}
        >
          {statuses.map((status) => (
            <option key={status.value} value={status.value}>{status.label}</option>
          ))}
        </select>
      </div>
      {message.read ? <span className="inquiry-read">Okundu</span> : (
        <button type="button" className="btn btn-ghost" disabled={pending !== null} onClick={() => void update("read")}>
          {pending === "read" ? "İşaretleniyor…" : "Okundu işaretle"}
        </button>
      )}
      <p className="inquiry-notice" role="status">
        {pending === "status" ? "Kaydediliyor…" : notice}
      </p>
      {error ? <p className="form-error" role="alert">{error}</p> : null}
    </div>
  );
}

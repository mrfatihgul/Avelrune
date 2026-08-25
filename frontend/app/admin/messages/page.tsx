"use client";

import { useEffect, useState } from "react";
import { AdminShell } from "@/components/admin/AdminShell";
import { apiAuthFetch } from "@/lib/api";
import { formatDate } from "@/lib/format";
import { ContactMessage } from "@/lib/types";

export default function MessagesPage() {
  const [messages, setMessages] = useState<ContactMessage[] | null>(null);
  const [error, setError] = useState("");

  async function load() {
    try {
      setMessages(await apiAuthFetch<ContactMessage[]>("/api/inquiries"));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Talepler alınamadı.");
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function markRead(id: number) {
    await apiAuthFetch(`/api/inquiries/${id}/read`, { method: "PATCH" });
    load();
  }

  return (
    <AdminShell>
      <p className="eyebrow">Talepler</p>
      <h1 className="display" style={{ fontSize: "2.6rem", marginBottom: 24 }}>
        İletişim kutusu
      </h1>
      {error ? <p className="form-error">{error}</p> : null}
      {!messages ? (
        <div className="skeleton" />
      ) : messages.length === 0 ? (
        <p className="empty">Henüz talep yok.</p>
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Ad</th>
              <th>İletişim</th>
              <th>Mesaj</th>
              <th>Tarih</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {messages.map((item) => (
              <tr key={item.id} style={{ opacity: item.read ? 0.6 : 1 }}>
                <td>{item.fullName}</td>
                <td>
                  {item.phone}
                  <br />
                  {item.email}
                </td>
                <td>
                  {item.propertySlug ? <strong>{item.propertySlug}</strong> : null}
                  <p>{item.message}</p>
                </td>
                <td>{formatDate(item.createdAt)}</td>
                <td>
                  {!item.read ? (
                    <button type="button" onClick={() => markRead(item.id)}>
                      Okundu
                    </button>
                  ) : (
                    "Okundu"
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </AdminShell>
  );
}

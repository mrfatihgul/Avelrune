"use client";

import { useEffect, useState } from "react";
import { AdminShell } from "@/components/admin/AdminShell";
import { InquiryActions } from "@/components/InquiryActions";
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

  return (
    <AdminShell>
      <p className="eyebrow">Talepler</p>
      <h1 className="display admin-page-title">
        İletişim kutusu
      </h1>
      {error ? <p className="form-error">{error}</p> : null}
      {!messages ? (
        <div className="skeleton" />
      ) : messages.length === 0 ? (
        <p className="empty">Henüz talep yok.</p>
      ) : (
        <table className="table admin-table admin-messages-table">
          <thead>
            <tr>
              <th>Ad</th>
              <th>İletişim</th>
              <th>Mesaj</th>
              <th>Tarih</th>
              <th>Talep işlemleri</th>
            </tr>
          </thead>
          <tbody>
            {messages.map((item) => (
              <tr key={item.id}>
                <td data-label="Ad">{item.fullName}</td>
                <td data-label="İletişim">
                  <span>
                    {item.phone}
                    <br />
                    {item.email}
                  </span>
                </td>
                <td data-label="Mesaj">
                  <div>
                    {item.propertySlug ? <strong>{item.propertySlug}</strong> : null}
                    <p>{item.message}</p>
                  </div>
                </td>
                <td data-label="Tarih">{formatDate(item.createdAt)}</td>
                <td className="admin-actions-cell" data-label="Talep işlemleri">
                  <InquiryActions message={item} onUpdated={(updated) => {
                    setMessages((current) => current?.map((message) => message.id === updated.id ? updated : message) ?? null);
                  }} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </AdminShell>
  );
}

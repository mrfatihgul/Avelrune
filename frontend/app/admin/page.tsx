"use client";

import { useEffect, useState } from "react";
import { AdminShell } from "@/components/admin/AdminShell";
import { apiAuthFetch } from "@/lib/api";
import { PropertyStats } from "@/lib/types";

export default function AdminDashboardPage() {
  const [stats, setStats] = useState<PropertyStats | null>(null);
  const [messages, setMessages] = useState(0);
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([
      apiAuthFetch<PropertyStats>("/api/admin/stats"),
      apiAuthFetch<{ total: number }>("/api/inquiries/stats"),
    ])
      .then(([propertyStats, contactStats]) => {
        setStats(propertyStats);
        setMessages(contactStats.total);
      })
      .catch((err) => setError(err instanceof Error ? err.message : "Özet alınamadı."));
  }, []);

  return (
    <AdminShell>
      <p className="eyebrow">Özet</p>
      <h1 className="display" style={{ fontSize: "3rem", margin: "8px 0 28px" }}>
        Portföy durumu
      </h1>
      {error ? <p className="form-error">{error}</p> : null}
      {!stats ? (
        <div className="skeleton" />
      ) : (
        <div className="admin-cards">
          <article className="admin-card">
            <span>Toplam ilan</span>
            <strong>{stats.total}</strong>
          </article>
          <article className="admin-card">
            <span>Aktif</span>
            <strong>{stats.active}</strong>
          </article>
          <article className="admin-card">
            <span>Pasif</span>
            <strong>{stats.inactive}</strong>
          </article>
          <article className="admin-card">
            <span>Satılık</span>
            <strong>{stats.sale}</strong>
          </article>
          <article className="admin-card">
            <span>Kiralık</span>
            <strong>{stats.rent}</strong>
          </article>
          <article className="admin-card">
            <span>Öne çıkan</span>
            <strong>{stats.featured}</strong>
          </article>
          <article className="admin-card">
            <span>İletişim talebi</span>
            <strong>{messages}</strong>
          </article>
        </div>
      )}
    </AdminShell>
  );
}

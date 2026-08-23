"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { apiFetch, authHeaders } from "@/lib/api";
import { getToken, getUser } from "@/lib/auth";
import { formatDate, formatPrice, listingLabel } from "@/lib/format";
import { ContactMessage, PropertyDetail } from "@/lib/types";

export default function HouseMessagesPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const [house, setHouse] = useState<PropertyDetail | null>(null);
  const [messages, setMessages] = useState<ContactMessage[] | null>(null);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function load(token: string) {
    const [detail, inbox] = await Promise.all([
      apiFetch<PropertyDetail>(`/api/properties/owned/${params.id}`, { headers: authHeaders(token) }),
      apiFetch<ContactMessage[]>(`/api/requests/property/${params.id}`, { headers: authHeaders(token) }),
    ]);
    setHouse(detail);
    setMessages(inbox);
  }

  useEffect(() => {
    const token = getToken();
    const user = getUser();
    if (!token || !user) {
      router.replace("/giris");
      return;
    }
    load(token).catch((err) => setError(err instanceof Error ? err.message : "Yüklenemedi."));
  }, [params.id, router]);

  async function markRead(id: number) {
    const token = getToken();
    if (!token) return;
    await apiFetch(`/api/requests/${id}/read`, { method: "PATCH", headers: authHeaders(token) });
    load(token);
  }

  async function removeListing() {
    const token = getToken();
    if (!token || !house) return;
    if (!window.confirm("Bu ilanı kaldırmak istiyor musunuz?")) return;
    setBusy(true);
    try {
      await apiFetch(`/api/properties/${house.id}`, { method: "DELETE", headers: authHeaders(token) });
      router.replace("/evlerim");
    } catch (err) {
      setError(err instanceof Error ? err.message : "İlan silinemedi.");
      setBusy(false);
    }
  }

  return (
    <SiteShell>
      <section className="page-hero wrap owner-hero">
        <div>
          <Link href="/evlerim" className="eyebrow">
            ← Evlerim
          </Link>
          <h1 className="display">{house ? house.title : "İlan"}</h1>
          {house ? (
            <p>
              {listingLabel(house.listingType)} · {house.district}, {house.city} · {formatPrice(house.price, house.listingType)}
            </p>
          ) : null}
        </div>
        {house ? (
          <div className="owner-actions">
            <Link className="btn btn-ghost" href={`/ilan/${house.slug}`}>
              İlanı gör
            </Link>
            <Link className="btn" href={`/evlerim/${house.id}/duzenle`}>
              Düzenle
            </Link>
            <button type="button" className="btn btn-ghost" onClick={removeListing} disabled={busy}>
              Kaldır
            </button>
          </div>
        ) : null}
      </section>
      <section className="wrap owner-section">
        {error ? <p className="form-error">{error}</p> : null}
        {messages === null && !error ? <div className="skeleton" /> : null}
        {messages && messages.length === 0 ? (
          <p className="empty">Bu eve henüz mesaj gelmedi.</p>
        ) : null}
        {messages && messages.length > 0 ? (
          <ol className="owner-message-list">
            {messages.map((item) => (
              <li key={item.id} className={item.read ? "is-read" : "is-new"}>
                <header>
                  <div>
                    <strong>{item.fullName}</strong>
                    <span>{item.member ? "Üye" : "Ziyaretçi"}</span>
                  </div>
                  <time>{formatDate(item.createdAt)}</time>
                </header>
                <p className="owner-message-contact">
                  {item.phone} · {item.email}
                </p>
                <p>{item.message}</p>
                {!item.read ? (
                  <button type="button" className="btn btn-ghost" onClick={() => markRead(item.id)}>
                    Okundu işaretle
                  </button>
                ) : null}
              </li>
            ))}
          </ol>
        ) : null}
      </section>
    </SiteShell>
  );
}

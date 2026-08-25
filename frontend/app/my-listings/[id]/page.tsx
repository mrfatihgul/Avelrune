"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { apiAuthFetch } from "@/lib/api";
import { useRequireUser } from "@/lib/useRequireUser";
import { formatDate, formatPrice, listingLabel } from "@/lib/format";
import { ContactMessage, PropertyDetail } from "@/lib/types";

export default function HouseMessagesPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const { ready } = useRequireUser();
  const [house, setHouse] = useState<PropertyDetail | null>(null);
  const [messages, setMessages] = useState<ContactMessage[] | null>(null);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function load() {
    const [detail, inbox] = await Promise.all([
      apiAuthFetch<PropertyDetail>(`/api/properties/owned/${params.id}`),
      apiAuthFetch<ContactMessage[]>(`/api/inquiries/property/${params.id}`),
    ]);
    setHouse(detail);
    setMessages(inbox);
  }

  useEffect(() => {
    if (!ready) return;
    load().catch((err) => setError(err instanceof Error ? err.message : "Yüklenemedi."));
  }, [params.id, ready]);

  async function markRead(id: number) {
    await apiAuthFetch(`/api/inquiries/${id}/read`, { method: "PATCH" });
    load();
  }

  async function removeListing() {
    if (!house) return;
    if (!window.confirm("Bu ilanı kaldırmak istiyor musunuz?")) return;
    setBusy(true);
    try {
      await apiAuthFetch(`/api/properties/${house.id}`, { method: "DELETE" });
      router.replace("/my-listings");
    } catch (err) {
      setError(err instanceof Error ? err.message : "İlan silinemedi.");
      setBusy(false);
    }
  }

  return (
    <SiteShell>
      <section className="page-hero wrap owner-hero">
        <div>
          <Link href="/my-listings" className="eyebrow">
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
            <Link className="btn btn-ghost" href={`/listing/${house.slug}`}>
              İlanı gör
            </Link>
            <Link className="btn" href={`/my-listings/${house.id}/edit`}>
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

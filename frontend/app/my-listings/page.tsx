"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { SiteShell } from "@/components/layout/SiteShell";
import { apiAuthFetch } from "@/lib/api";
import { useRequireUser } from "@/lib/useRequireUser";
import { formatPrice, listingLabel } from "@/lib/format";
import { PropertySummary } from "@/lib/types";

interface MessageSummary {
  propertyId: number;
  total: number;
  unread: number;
}

export default function EvlerimPage() {
  const { ready } = useRequireUser();
  const [properties, setProperties] = useState<PropertySummary[] | null>(null);
  const [summary, setSummary] = useState<Record<number, MessageSummary>>({});
  const [error, setError] = useState("");

  useEffect(() => {
    if (!ready) return;
    Promise.all([
      apiAuthFetch<PropertySummary[]>("/api/properties/mine"),
      apiAuthFetch<MessageSummary[]>("/api/inquiries/summary"),
    ])
      .then(([houses, counts]) => {
        setProperties(houses);
        setSummary(Object.fromEntries(counts.map((item) => [item.propertyId, item])));
      })
      .catch((err) => setError(err instanceof Error ? err.message : "Evler yüklenemedi."));
  }, [ready]);

  return (
    <SiteShell>
      <section className="page-hero wrap owner-hero">
        <div>
          <span className="eyebrow">Hesabım</span>
          <h1 className="display">Evlerim</h1>
          <p>Yayınladığınız ilanlar. Bir eve tıklayınca o eve gelen mesajları görürsünüz.</p>
        </div>
        <Link className="btn" href="/my-listings/new">
          Yeni ilan
        </Link>
      </section>
      <section className="wrap owner-section">
        {error ? <p className="form-error">{error}</p> : null}
        {properties === null && !error ? <div className="skeleton" /> : null}
        {properties && properties.length === 0 ? (
          <div className="empty owner-empty">
            <p>Henüz ilan yok.</p>
            <Link className="btn" href="/my-listings/new">
              İlk evinizi ekleyin
            </Link>
          </div>
        ) : null}
        {properties && properties.length > 0 ? (
          <div className="owner-house-list">
            {properties.map((house) => {
              const stats = summary[house.id];
              return (
                <Link key={house.id} href={`/my-listings/${house.id}`} className="owner-house-card">
                  <div
                    className="owner-house-cover"
                    style={
                      house.coverImage
                        ? { backgroundImage: `url(${house.coverImage})` }
                        : undefined
                    }
                  />
                  <div className="owner-house-body">
                    <span className="tag">{listingLabel(house.listingType)}</span>
                    <h2>{house.title}</h2>
                    <p>
                      {house.district}, {house.city} · {house.roomCount} · {formatPrice(house.price, house.listingType)}
                    </p>
                    <div className="owner-house-meta">
                      <span>{stats?.total ?? 0} mesaj</span>
                      {(stats?.unread ?? 0) > 0 ? <em>{stats?.unread} yeni</em> : <span>Okunmamış yok</span>}
                    </div>
                  </div>
                </Link>
              );
            })}
          </div>
        ) : null}
      </section>
    </SiteShell>
  );
}

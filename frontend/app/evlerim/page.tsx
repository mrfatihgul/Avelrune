"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { apiFetch, authHeaders } from "@/lib/api";
import { getToken, getUser } from "@/lib/auth";
import { formatPrice, listingLabel } from "@/lib/format";
import { PropertySummary } from "@/lib/types";

interface MessageSummary {
  propertyId: number;
  total: number;
  unread: number;
}

export default function EvlerimPage() {
  const router = useRouter();
  const [properties, setProperties] = useState<PropertySummary[] | null>(null);
  const [summary, setSummary] = useState<Record<number, MessageSummary>>({});
  const [error, setError] = useState("");

  useEffect(() => {
    const token = getToken();
    const user = getUser();
    if (!token || !user) {
      router.replace("/giris");
      return;
    }
    if (user.role === "ADMIN") {
      router.replace("/admin");
      return;
    }
    Promise.all([
      apiFetch<PropertySummary[]>("/api/properties/mine", { headers: authHeaders(token) }),
      apiFetch<MessageSummary[]>("/api/requests/summary", { headers: authHeaders(token) }),
    ])
      .then(([houses, counts]) => {
        setProperties(houses);
        setSummary(Object.fromEntries(counts.map((item) => [item.propertyId, item])));
      })
      .catch((err) => setError(err instanceof Error ? err.message : "Evler yüklenemedi."));
  }, [router]);

  return (
    <SiteShell>
      <section className="page-hero wrap owner-hero">
        <div>
          <span className="eyebrow">Hesabım</span>
          <h1 className="display">Evlerim</h1>
          <p>Yayınladığınız ilanlar. Bir eve tıklayınca o eve gelen mesajları görürsünüz.</p>
        </div>
        <Link className="btn" href="/evlerim/yeni">
          Yeni ilan
        </Link>
      </section>
      <section className="wrap owner-section">
        {error ? <p className="form-error">{error}</p> : null}
        {properties === null && !error ? <div className="skeleton" /> : null}
        {properties && properties.length === 0 ? (
          <div className="empty owner-empty">
            <p>Henüz ilan yok.</p>
            <Link className="btn" href="/evlerim/yeni">
              İlk evinizi ekleyin
            </Link>
          </div>
        ) : null}
        {properties && properties.length > 0 ? (
          <div className="owner-house-list">
            {properties.map((house) => {
              const stats = summary[house.id];
              return (
                <Link key={house.id} href={`/evlerim/${house.id}`} className="owner-house-card">
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

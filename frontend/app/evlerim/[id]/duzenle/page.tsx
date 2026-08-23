"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { OwnerListingForm } from "@/components/owner/OwnerListingForm";
import { apiFetch, authHeaders } from "@/lib/api";
import { getToken, getUser } from "@/lib/auth";
import { PropertyDetail } from "@/lib/types";

export default function EditListingPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const [property, setProperty] = useState<PropertyDetail | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = getToken();
    const user = getUser();
    if (!token || !user) {
      router.replace("/giris");
      return;
    }
    apiFetch<PropertyDetail>(`/api/properties/owned/${params.id}`, { headers: authHeaders(token) })
      .then(setProperty)
      .catch((err) => setError(err instanceof Error ? err.message : "İlan alınamadı."));
  }, [params.id, router]);

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Evlerim</span>
        <h1 className="display">İlanı düzenle</h1>
      </section>
      <section className="wrap owner-section" style={{ maxWidth: 920 }}>
        {error ? <p className="form-error">{error}</p> : null}
        {property ? <OwnerListingForm property={property} /> : !error ? <div className="skeleton" /> : null}
      </section>
    </SiteShell>
  );
}

"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { OwnerListingForm } from "@/components/owner/OwnerListingForm";
import { apiAuthFetch } from "@/lib/api";
import { useRequireUser } from "@/lib/useRequireUser";
import { PropertyDetail } from "@/lib/types";

export default function EditListingPage() {
  const params = useParams<{ id: string }>();
  const { ready } = useRequireUser();
  const [property, setProperty] = useState<PropertyDetail | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!ready) return;
    apiAuthFetch<PropertyDetail>(`/api/properties/owned/${params.id}`)
      .then(setProperty)
      .catch((err) => setError(err instanceof Error ? err.message : "İlan alınamadı."));
  }, [params.id, ready]);

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

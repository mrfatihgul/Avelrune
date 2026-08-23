"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { AdminShell } from "@/components/admin/AdminShell";
import { PropertyForm } from "@/components/admin/PropertyForm";
import { apiFetch, authHeaders } from "@/lib/api";
import { getToken } from "@/lib/auth";
import { PropertyDetail } from "@/lib/types";

export default function EditPropertyPage() {
  const params = useParams<{ id: string }>();
  const [property, setProperty] = useState<PropertyDetail | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = getToken();
    if (!token) return;
    apiFetch<PropertyDetail>(`/api/admin/properties/${params.id}`, {
      headers: authHeaders(token),
    })
      .then(setProperty)
      .catch((err) => setError(err instanceof Error ? err.message : "İlan alınamadı."));
  }, [params.id]);

  return (
    <AdminShell>
      <p className="eyebrow">Düzenle</p>
      <h1 className="display" style={{ fontSize: "2.6rem", marginBottom: 24 }}>
        {property?.title ?? "İlan"}
      </h1>
      {error ? <p className="form-error">{error}</p> : null}
      {property ? <PropertyForm property={property} /> : <div className="skeleton" />}
    </AdminShell>
  );
}

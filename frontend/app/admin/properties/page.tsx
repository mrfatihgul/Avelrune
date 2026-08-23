"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { AdminShell } from "@/components/admin/AdminShell";
import { apiFetch, authHeaders } from "@/lib/api";
import { getToken } from "@/lib/auth";
import { formatPrice, listingLabel } from "@/lib/format";
import { PageResponse, PropertySummary } from "@/lib/types";

export default function AdminPropertiesPage() {
  const [data, setData] = useState<PageResponse<PropertySummary> | null>(null);
  const [error, setError] = useState("");

  async function load() {
    const token = getToken();
    if (!token) return;
    try {
      const response = await apiFetch<PageResponse<PropertySummary>>(
        "/api/admin/properties?size=40",
        { headers: authHeaders(token) }
      );
      setData(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Liste alınamadı.");
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function patch(path: string) {
    const token = getToken();
    if (!token) return;
    await apiFetch(path, { method: "PATCH", headers: authHeaders(token) });
    load();
  }

  async function remove(id: number) {
    if (!confirm("İlan silinsin mi?")) return;
    const token = getToken();
    if (!token) return;
    await apiFetch(`/api/admin/properties/${id}`, {
      method: "DELETE",
      headers: authHeaders(token),
    });
    load();
  }

  return (
    <AdminShell>
      <div className="results-head">
        <div>
          <p className="eyebrow">İlanlar</p>
          <h1 className="display" style={{ fontSize: "2.6rem" }}>
            Portföy yönetimi
          </h1>
        </div>
        <Link href="/admin/properties/new" className="btn">
          Yeni ilan
        </Link>
      </div>
      {error ? <p className="form-error">{error}</p> : null}
      {!data ? (
        <div className="skeleton" />
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Başlık</th>
              <th>İşlem</th>
              <th>Fiyat</th>
              <th>Durum</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {data.content.map((property) => (
              <tr key={property.id}>
                <td>{property.title}</td>
                <td>{listingLabel(property.listingType)}</td>
                <td>{formatPrice(property.price, property.listingType)}</td>
                <td>
                  {property.active ? "Aktif" : "Pasif"}
                  {property.featured ? " · Öne çıkan" : ""}
                </td>
                <td style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
                  <Link href={`/admin/properties/${property.id}`}>Düzenle</Link>
                  <button type="button" onClick={() => patch(`/api/admin/properties/${property.id}/active`)}>
                    {property.active ? "Pasifleştir" : "Aktifleştir"}
                  </button>
                  <button type="button" onClick={() => patch(`/api/admin/properties/${property.id}/featured`)}>
                    Öne çıkar
                  </button>
                  <button type="button" onClick={() => remove(property.id)}>
                    Sil
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </AdminShell>
  );
}

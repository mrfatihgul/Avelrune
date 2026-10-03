"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { AdminShell } from "@/components/admin/AdminShell";
import { apiAuthFetch } from "@/lib/api";
import { formatPrice, listingLabel } from "@/lib/format";
import { PageResponse, PropertySummary } from "@/lib/types";

export default function AdminPropertiesPage() {
  const [data, setData] = useState<PageResponse<PropertySummary> | null>(null);
  const [error, setError] = useState("");

  async function load() {
    try {
      const response = await apiAuthFetch<PageResponse<PropertySummary>>("/api/admin/properties?size=40");
      setData(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Liste alınamadı.");
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function patch(path: string) {
    try {
      await apiAuthFetch(path, { method: "PATCH" });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "İşlem tamamlanamadı.");
    }
  }

  async function remove(id: number) {
    if (!confirm("İlan silinsin mi?")) return;
    try {
      await apiAuthFetch(`/api/admin/properties/${id}`, { method: "DELETE" });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "İlan silinemedi.");
    }
  }

  return (
    <AdminShell>
      <div className="results-head">
        <div>
          <p className="eyebrow">İlanlar</p>
          <h1 className="display admin-page-title">
            İlan yönetimi
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
        <table className="table admin-table admin-properties-table">
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
                <td data-label="Başlık">{property.title}</td>
                <td data-label="İşlem">{listingLabel(property.listingType)}</td>
                <td data-label="Fiyat">{formatPrice(property.price, property.listingType)}</td>
                <td data-label="Durum">
                  {property.active ? "Aktif" : "Pasif"}
                  {property.featured ? " · Öne çıkan" : ""}
                </td>
                <td className="admin-actions-cell">
                  <div className="admin-row-actions">
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
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </AdminShell>
  );
}

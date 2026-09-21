"use client";

import { FormEvent, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { FieldLabel } from "@/components/form/FieldLabel";
import { apiAuthFetch, apiFetch } from "@/lib/api";
import { Agent, AuthUser, PropertyDetail } from "@/lib/types";

const empty = {
  title: "",
  description: "",
  price: "",
  listingType: "SALE",
  propertyType: "APARTMENT",
  city: "İstanbul",
  district: "",
  neighborhood: "",
  grossArea: "",
  netArea: "",
  roomCount: "3+1",
  buildingAge: "0",
  floor: "1",
  totalFloors: "5",
  heatingType: "COMBI",
  furnished: false,
  latitude: "41.04",
  longitude: "29.00",
  featured: false,
  active: true,
  features: "Asansör|Güvenlik",
  agentId: "",
  assignedUserId: "",
  imageUrls: "/images/properties/p01-1.jpg\n/images/properties/p01-2.jpg\n/images/properties/p01-3.jpg",
};

export function PropertyForm({ property }: { property?: PropertyDetail }) {
  const router = useRouter();
  const [agents, setAgents] = useState<Agent[]>([]);
  const [users, setUsers] = useState<AuthUser[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    apiFetch<Agent[]>("/api/agents").then(setAgents).catch(() => setAgents([]));
    apiAuthFetch<AuthUser[]>("/api/auth/users")
      .then(setUsers)
      .catch(() => setUsers([]));
  }, []);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const payload = {
      title: String(form.get("title")),
      description: String(form.get("description")),
      price: Number(form.get("price")),
      listingType: String(form.get("listingType")),
      propertyType: String(form.get("propertyType")),
      city: String(form.get("city")),
      district: String(form.get("district")),
      neighborhood: String(form.get("neighborhood")),
      grossArea: Number(form.get("grossArea")),
      netArea: Number(form.get("netArea")),
      roomCount: String(form.get("roomCount")),
      buildingAge: Number(form.get("buildingAge")),
      floor: Number(form.get("floor")),
      totalFloors: Number(form.get("totalFloors")),
      heatingType: String(form.get("heatingType")),
      furnished: form.get("furnished") === "on",
      latitude: Number(form.get("latitude")),
      longitude: Number(form.get("longitude")),
      featured: form.get("featured") === "on",
      active: form.get("active") === "on",
      features: String(form.get("features"))
        .split("|")
        .map((item) => item.trim())
        .filter(Boolean),
      agentId: Number(form.get("agentId")),
      assignedUserId: form.get("assignedUserId") ? Number(form.get("assignedUserId")) : null,
      imageUrls: String(form.get("imageUrls"))
        .split("\n")
        .map((item) => item.trim())
        .filter(Boolean),
    };
    setLoading(true);
    setError("");
    try {
      if (property) {
        await apiAuthFetch(`/api/admin/properties/${property.id}`, {
          method: "PUT",
          body: JSON.stringify(payload),
        });
      } else {
        await apiAuthFetch("/api/admin/properties", {
          method: "POST",
          body: JSON.stringify(payload),
        });
      }
      router.push("/admin/properties");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Kaydedilemedi.");
    } finally {
      setLoading(false);
    }
  }

  const initial = property
    ? {
        ...empty,
        title: property.title,
        description: property.description,
        price: String(property.price),
        listingType: property.listingType,
        propertyType: property.propertyType,
        city: property.city,
        district: property.district,
        neighborhood: property.neighborhood,
        grossArea: String(property.grossArea),
        netArea: String(property.netArea),
        roomCount: property.roomCount,
        buildingAge: String(property.buildingAge),
        floor: String(property.floor),
        totalFloors: String(property.totalFloors),
        heatingType: property.heatingType,
        furnished: property.furnished,
        latitude: String(property.latitude),
        longitude: String(property.longitude),
        featured: property.featured,
        active: property.active,
        features: property.features.join("|"),
        agentId: String(property.agent?.id ?? ""),
        assignedUserId: String(property.assignedUserId ?? ""),
        imageUrls: property.images.map((image) => image.url).join("\n"),
      }
    : empty;

  return (
    <form className="form" onSubmit={onSubmit}>
      <label className="field">
        <FieldLabel>Başlık</FieldLabel>
        <input name="title" defaultValue={initial.title} required aria-required="true" />
      </label>
      <label className="field">
        <FieldLabel>Açıklama</FieldLabel>
        <textarea name="description" defaultValue={initial.description} required aria-required="true" />
      </label>
      <div className="listing-grid">
        <label className="field">
          <FieldLabel>Fiyat</FieldLabel>
          <input name="price" defaultValue={initial.price} required aria-required="true" />
        </label>
        <label className="field">
          <FieldLabel>İşlem</FieldLabel>
          <select name="listingType" defaultValue={initial.listingType} required aria-required="true">
            <option value="SALE">Satılık</option>
            <option value="RENT">Kiralık</option>
          </select>
        </label>
        <label className="field">
          <FieldLabel>Tip</FieldLabel>
          <select name="propertyType" defaultValue={initial.propertyType} required aria-required="true">
            <option value="APARTMENT">Daire</option>
            <option value="RESIDENCE">Rezidans</option>
            <option value="VILLA">Villa</option>
            <option value="HOUSE">Müstakil Ev</option>
            <option value="YALI">Yalı</option>
            <option value="STONE_HOUSE">Taş Ev</option>
          </select>
        </label>
        <label className="field">
          <FieldLabel>Oda</FieldLabel>
          <input name="roomCount" defaultValue={initial.roomCount} required aria-required="true" />
        </label>
        <label className="field">
          <FieldLabel>Şehir</FieldLabel>
          <input name="city" defaultValue={initial.city} required aria-required="true" />
        </label>
        <label className="field">
          <FieldLabel>İlçe</FieldLabel>
          <input name="district" defaultValue={initial.district} required aria-required="true" />
        </label>
        <label className="field">
          <FieldLabel>Mahalle</FieldLabel>
          <input name="neighborhood" defaultValue={initial.neighborhood} required aria-required="true" />
        </label>
        <label className="field">
          <FieldLabel>Brüt m²</FieldLabel>
          <input name="grossArea" defaultValue={initial.grossArea} required aria-required="true" />
        </label>
        <label className="field">
          <FieldLabel>Net m²</FieldLabel>
          <input name="netArea" defaultValue={initial.netArea} required aria-required="true" />
        </label>
        <label className="field">
          <span>Bina yaşı</span>
          <input name="buildingAge" defaultValue={initial.buildingAge} />
        </label>
        <label className="field">
          <span>Kat</span>
          <input name="floor" defaultValue={initial.floor} />
        </label>
        <label className="field">
          <span>Toplam kat</span>
          <input name="totalFloors" defaultValue={initial.totalFloors} />
        </label>
        <label className="field">
          <FieldLabel>Isıtma</FieldLabel>
          <select name="heatingType" defaultValue={initial.heatingType} required aria-required="true">
            <option value="COMBI">Kombi</option>
            <option value="CENTRAL">Merkezi</option>
            <option value="FLOOR_HEATING">Yerden ısıtma</option>
            <option value="AIR_CONDITIONING">Klima</option>
            <option value="NONE">Yok</option>
          </select>
        </label>
        <label className="field">
          <FieldLabel>Danışman</FieldLabel>
          <select name="agentId" defaultValue={initial.agentId} required aria-required="true">
            <option value="">Seçin</option>
            {agents.map((agent) => (
              <option key={agent.id} value={agent.id}>
                {agent.fullName}
              </option>
            ))}
          </select>
        </label>
        <label className="field">
          <span>Atanan kullanıcı</span>
          <select name="assignedUserId" defaultValue={initial.assignedUserId}>
            <option value="">Atanmamış</option>
            {users
              .filter((user) => user.role === "USER")
              .map((user) => (
                <option key={user.id} value={user.id}>
                  {user.fullName} — {user.email}
                </option>
              ))}
          </select>
        </label>
      </div>
      <label className="field">
        <span>Özellikler ( | ile)</span>
        <input name="features" defaultValue={initial.features} />
      </label>
      <label className="field">
        <FieldLabel>Görseller (satır satır)</FieldLabel>
        <textarea name="imageUrls" defaultValue={initial.imageUrls} required aria-required="true" />
      </label>
      <input type="hidden" name="latitude" defaultValue={initial.latitude} />
      <input type="hidden" name="longitude" defaultValue={initial.longitude} />
      <label>
        <input type="checkbox" name="furnished" defaultChecked={initial.furnished} /> Eşyalı
      </label>
      <label>
        <input type="checkbox" name="featured" defaultChecked={initial.featured} /> Öne çıkan
      </label>
      <label>
        <input type="checkbox" name="active" defaultChecked={initial.active} /> Aktif
      </label>
      {error ? <p className="form-error">{error}</p> : null}
      <button className="btn" type="submit" disabled={loading}>
        {loading ? "Kaydediliyor…" : "Kaydet"}
      </button>
    </form>
  );
}

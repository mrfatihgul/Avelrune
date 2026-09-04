"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { apiAuthFetch } from "@/lib/api";
import { PropertyDetail } from "@/lib/types";

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
  features: "Asansör|Güvenlik",
  imageUrls:
    "/images/properties/p01-1.jpg\n/images/properties/p01-2.jpg\n/images/properties/p01-3.jpg",
};

export function OwnerListingForm({ property }: { property?: PropertyDetail }) {
  const router = useRouter();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

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
      features: String(form.get("features"))
        .split("|")
        .map((item) => item.trim())
        .filter(Boolean),
      imageUrls: String(form.get("imageUrls"))
        .split("\n")
        .map((item) => item.trim())
        .filter(Boolean),
    };
    setLoading(true);
    setError("");
    try {
      if (property) {
        await apiAuthFetch(`/api/properties/${property.id}`, {
          method: "PUT",
          body: JSON.stringify(payload),
        });
      } else {
        await apiAuthFetch("/api/properties", {
          method: "POST",
          body: JSON.stringify(payload),
        });
      }
      router.push("/my-listings");
    } catch (err) {
      setError(err instanceof Error ? err.message : "İlan kaydedilemedi.");
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
        features: property.features.join("|"),
        imageUrls: property.images.map((image) => image.url).join("\n"),
      }
    : empty;

  return (
    <form className="form owner-form" onSubmit={onSubmit}>
      <input type="hidden" name="latitude" defaultValue={property?.latitude ?? 41.04} />
      <input type="hidden" name="longitude" defaultValue={property?.longitude ?? 29.0} />
      <div className="listing-type-switch">
        <label className="type-chip">
          <input type="radio" name="listingType" value="SALE" defaultChecked={initial.listingType === "SALE"} />
          Satılık
        </label>
        <label className="type-chip">
          <input type="radio" name="listingType" value="RENT" defaultChecked={initial.listingType === "RENT"} />
          Kiralık
        </label>
      </div>
      <label className="field">
        <span>Başlık</span>
        <input name="title" defaultValue={initial.title} required />
      </label>
      <label className="field">
        <span>Açıklama</span>
        <textarea name="description" defaultValue={initial.description} required />
      </label>
      <div className="owner-form-grid">
        <label className="field">
          <span>Fiyat</span>
          <input name="price" defaultValue={initial.price} required />
        </label>
        <label className="field">
          <span>Tip</span>
          <select name="propertyType" defaultValue={initial.propertyType}>
            <option value="APARTMENT">Daire</option>
            <option value="RESIDENCE">Rezidans</option>
            <option value="VILLA">Villa</option>
            <option value="HOUSE">Müstakil Ev</option>
            <option value="YALI">Yalı</option>
            <option value="STONE_HOUSE">Taş Ev</option>
          </select>
        </label>
        <label className="field">
          <span>Oda</span>
          <input name="roomCount" defaultValue={initial.roomCount} />
        </label>
        <label className="field">
          <span>Şehir</span>
          <input name="city" defaultValue={initial.city} />
        </label>
        <label className="field">
          <span>İlçe</span>
          <input name="district" defaultValue={initial.district} />
        </label>
        <label className="field">
          <span>Mahalle</span>
          <input name="neighborhood" defaultValue={initial.neighborhood} />
        </label>
        <label className="field">
          <span>Brüt m²</span>
          <input name="grossArea" defaultValue={initial.grossArea} />
        </label>
        <label className="field">
          <span>Net m²</span>
          <input name="netArea" defaultValue={initial.netArea} />
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
          <span>Isıtma</span>
          <select name="heatingType" defaultValue={initial.heatingType}>
            <option value="COMBI">Kombi</option>
            <option value="CENTRAL">Merkezi</option>
            <option value="FLOOR_HEATING">Yerden ısıtma</option>
            <option value="AIR_CONDITIONING">Klima</option>
            <option value="NONE">Yok</option>
          </select>
        </label>
      </div>
      <label className="field">
        <span>Özellikler ( | ile)</span>
        <input name="features" defaultValue={initial.features} />
      </label>
      <label className="field">
        <span>Görseller (satır satır URL)</span>
        <textarea name="imageUrls" defaultValue={initial.imageUrls} />
      </label>
      <label className="check-line">
        <input type="checkbox" name="furnished" defaultChecked={initial.furnished} /> Eşyalı
      </label>
      {error ? <p className="form-error">{error}</p> : null}
      <button className="btn" type="submit" disabled={loading}>
        {loading ? "Kaydediliyor…" : property ? "İlanı güncelle" : "İlanı yayınla"}
      </button>
    </form>
  );
}

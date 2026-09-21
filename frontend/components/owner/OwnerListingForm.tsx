"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { FieldLabel } from "@/components/form/FieldLabel";
import { ImageUploader } from "@/components/form/ImageUploader";
import { LocationPickerField } from "@/components/map/LocationMap";
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
  imageUrls: "",
};

export function OwnerListingForm({ property }: { property?: PropertyDetail }) {
  const router = useRouter();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [imagesBusy, setImagesBusy] = useState(false);
  const [coords, setCoords] = useState({
    lat: property?.latitude ?? 41.04,
    lng: property?.longitude ?? 29.0,
  });

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
      imageUrls: String(form.get("imageUrls") ?? "")
        .split("\n")
        .map((item) => item.trim())
        .filter(Boolean),
    };
    if (payload.imageUrls.length === 0) {
      setError("En az bir görsel ekleyin.");
      return;
    }
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
      <p className="field-heading">
        <FieldLabel>İşlem</FieldLabel>
      </p>
      <div className="listing-type-switch">
        <label className="type-chip">
          <input type="radio" name="listingType" value="SALE" required defaultChecked={initial.listingType === "SALE"} />
          Satılık
        </label>
        <label className="type-chip">
          <input type="radio" name="listingType" value="RENT" required defaultChecked={initial.listingType === "RENT"} />
          Kiralık
        </label>
      </div>
      <label className="field">
        <FieldLabel>Başlık</FieldLabel>
        <input name="title" defaultValue={initial.title} required aria-required="true" />
      </label>
      <label className="field">
        <FieldLabel>Açıklama</FieldLabel>
        <textarea name="description" defaultValue={initial.description} required aria-required="true" />
      </label>
      <div className="owner-form-grid">
        <label className="field">
          <FieldLabel>Fiyat</FieldLabel>
          <input name="price" defaultValue={initial.price} required aria-required="true" />
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
      </div>
      <LocationPickerField
        latitude={coords.lat}
        longitude={coords.lng}
        onChange={(lat, lng) => setCoords({ lat, lng })}
      />
      <label className="field">
        <span>Özellikler ( | ile)</span>
        <input name="features" defaultValue={initial.features} />
      </label>
      <div className="field">
        <FieldLabel>Görseller</FieldLabel>
        <ImageUploader
          name="imageUrls"
          initialUrls={initial.imageUrls ? initial.imageUrls.split("\n").filter(Boolean) : []}
          onBusyChange={setImagesBusy}
        />
      </div>
      <label className="check-line">
        <input type="checkbox" name="furnished" defaultChecked={initial.furnished} /> Eşyalı
      </label>
      {error ? <p className="form-error">{error}</p> : null}
      <button className="btn" type="submit" disabled={loading || imagesBusy}>
        {loading ? "Kaydediliyor…" : property ? "İlanı güncelle" : "İlanı yayınla"}
      </button>
    </form>
  );
}

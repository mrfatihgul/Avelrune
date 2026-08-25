"use client";

import { useRouter } from "next/navigation";
import { FormEvent, useState } from "react";
import { FilterMeta } from "@/lib/types";

export function HeroSearch({ meta }: { meta: FilterMeta }) {
  const router = useRouter();
  const [listingType, setListingType] = useState("SALE");
  const [city, setCity] = useState("");
  const [district, setDistrict] = useState("");
  const [propertyType, setPropertyType] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  const districts = meta.cities.find((item) => item.name === city)?.districts ?? [];

  function onSubmit(event: FormEvent) {
    event.preventDefault();
    const params = new URLSearchParams();
    if (listingType) params.set("listingType", listingType);
    if (city) params.set("city", city);
    if (district) params.set("district", district);
    if (propertyType) params.set("propertyType", propertyType);
    if (minPrice) params.set("minPrice", minPrice);
    if (maxPrice) params.set("maxPrice", maxPrice);
    router.push(`/listings?${params.toString()}`);
  }

  return (
    <form className="search-bar" onSubmit={onSubmit}>
      <label>
        <span>İşlem</span>
        <select value={listingType} onChange={(e) => setListingType(e.target.value)}>
          <option value="SALE">Satılık</option>
          <option value="RENT">Kiralık</option>
        </select>
      </label>
      <label>
        <span>Şehir</span>
        <select
          value={city}
          onChange={(e) => {
            setCity(e.target.value);
            setDistrict("");
          }}
        >
          <option value="">Tümü</option>
          {meta.cities.map((item) => (
            <option key={item.name} value={item.name}>
              {item.name}
            </option>
          ))}
        </select>
      </label>
      <label>
        <span>İlçe</span>
        <select value={district} onChange={(e) => setDistrict(e.target.value)} disabled={!city}>
          <option value="">Tümü</option>
          {districts.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
      </label>
      <label>
        <span>Tip</span>
        <select value={propertyType} onChange={(e) => setPropertyType(e.target.value)}>
          <option value="">Tümü</option>
          {meta.propertyTypes.map((item) => (
            <option key={item.value} value={item.value}>
              {item.label}
            </option>
          ))}
        </select>
      </label>
      <label>
        <span>Min. fiyat</span>
        <input
          inputMode="numeric"
          placeholder="0"
          value={minPrice}
          onChange={(e) => setMinPrice(e.target.value.replace(/\D/g, ""))}
        />
      </label>
      <label>
        <span>Max. fiyat</span>
        <input
          inputMode="numeric"
          placeholder="—"
          value={maxPrice}
          onChange={(e) => setMaxPrice(e.target.value.replace(/\D/g, ""))}
        />
      </label>
      <button className="btn search-submit" type="submit">
        Ara
      </button>
    </form>
  );
}

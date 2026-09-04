"use client";

import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { FormEvent, useMemo, useState } from "react";
import { FilterMeta, SearchParams } from "@/lib/types";

export function Filters({
  meta,
  current,
}: {
  meta: FilterMeta;
  current: SearchParams;
}) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const [open, setOpen] = useState(false);

  const districts = useMemo(() => {
    return meta.cities.find((item) => item.name === current.city)?.districts ?? [];
  }, [meta.cities, current.city]);

  function apply(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const params = new URLSearchParams();
    form.forEach((value, key) => {
      if (typeof value === "string" && value) params.set(key, value);
    });
    const sort = searchParams.get("sort");
    if (sort) {
      params.set("sort", sort);
    }
    router.push(`${pathname}?${params.toString()}`);
    setOpen(false);
  }

  function clear() {
    router.push(pathname);
    setOpen(false);
  }

  const form = (
    <form className="filters" onSubmit={apply}>
      <p className="eyebrow">Filtreler</p>
      <label className="field">
        <span>İşlem</span>
        <select name="listingType" defaultValue={current.listingType ?? ""}>
          <option value="">Tümü</option>
          <option value="SALE">Satılık</option>
          <option value="RENT">Kiralık</option>
        </select>
      </label>
      <label className="field">
        <span>Şehir</span>
        <select name="city" defaultValue={current.city ?? ""}>
          <option value="">Tümü</option>
          {meta.cities.map((city) => (
            <option key={city.name} value={city.name}>
              {city.name}
            </option>
          ))}
        </select>
      </label>
      <label className="field">
        <span>İlçe</span>
        <select name="district" defaultValue={current.district ?? ""}>
          <option value="">Tümü</option>
          {districts.map((district) => (
            <option key={district} value={district}>
              {district}
            </option>
          ))}
        </select>
      </label>
      <label className="field">
        <span>Tip</span>
        <select name="propertyType" defaultValue={current.propertyType ?? ""}>
          <option value="">Tümü</option>
          {meta.propertyTypes.map((type) => (
            <option key={type.value} value={type.value}>
              {type.label}
            </option>
          ))}
        </select>
      </label>
      <label className="field">
        <span>Oda</span>
        <select name="rooms" defaultValue={current.rooms ?? ""}>
          <option value="">Tümü</option>
          {["1+1", "2+1", "3+1", "4+1", "5+1", "5+2", "6+1"].map((room) => (
            <option key={room} value={room}>
              {room}
            </option>
          ))}
        </select>
      </label>
      <label className="field">
        <span>Min. fiyat</span>
        <input name="minPrice" defaultValue={current.minPrice ?? ""} inputMode="numeric" />
      </label>
      <label className="field">
        <span>Max. fiyat</span>
        <input name="maxPrice" defaultValue={current.maxPrice ?? ""} inputMode="numeric" />
      </label>
      <label className="field">
        <span>Min. m²</span>
        <input name="minArea" defaultValue={current.minArea ?? ""} inputMode="numeric" />
      </label>
      <label className="field">
        <span>Max. m²</span>
        <input name="maxArea" defaultValue={current.maxArea ?? ""} inputMode="numeric" />
      </label>
      <label className="field">
        <span>Bina yaşı en fazla</span>
        <input name="buildingAge" defaultValue={current.buildingAge ?? ""} inputMode="numeric" />
      </label>
      <button className="btn" type="submit">
        Uygula
      </button>
      {searchParams.toString() ? (
        <button className="btn btn-ghost" type="button" onClick={clear}>
          Temizle
        </button>
      ) : null}
    </form>
  );

  return (
    <>
      <button className="filter-toggle btn btn-ghost" type="button" onClick={() => setOpen(true)}>
        Filtrele
      </button>
      {form}
      {open ? (
        <>
          <button className="drawer-backdrop" aria-label="Kapat" onClick={() => setOpen(false)} />
          <div className="filter-drawer">{form}</div>
        </>
      ) : null}
    </>
  );
}

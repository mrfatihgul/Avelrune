"use client";

import { useRouter, useSearchParams } from "next/navigation";

export function SortSelect({ current }: { current?: string }) {
  const router = useRouter();
  const params = useSearchParams();

  function onChange(value: string) {
    const next = new URLSearchParams(params.toString());
    if (value && value !== "newest") next.set("sort", value);
    else next.delete("sort");
    next.delete("page");
    router.push(`/ilanlar?${next.toString()}`);
  }

  return (
    <label className="field" style={{ minWidth: 180 }}>
      <span>Sıralama</span>
      <select value={current ?? "newest"} onChange={(e) => onChange(e.target.value)}>
        <option value="newest">En yeni</option>
        <option value="price_asc">Fiyat artan</option>
        <option value="price_desc">Fiyat azalan</option>
        <option value="area">Metrekare</option>
      </select>
    </label>
  );
}

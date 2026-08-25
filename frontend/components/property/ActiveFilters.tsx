import Link from "next/link";
import { SearchParams } from "@/lib/types";
import { listingLabel, propertyTypeLabel } from "@/lib/format";

const labels: Record<string, string> = {
  listingType: "İşlem",
  city: "Şehir",
  district: "İlçe",
  propertyType: "Tip",
  rooms: "Oda",
  minPrice: "Min. fiyat",
  maxPrice: "Max. fiyat",
  minArea: "Min. m²",
  maxArea: "Max. m²",
  buildingAge: "Bina yaşı",
};

export function ActiveFilters({ params }: { params: SearchParams }) {
  const chips = Object.entries(params).filter(([key, value]) => value && key !== "sort" && key !== "page");
  if (chips.length === 0) return null;

  return (
    <div className="chips">
      {chips.map(([key, value]) => {
        const display =
          key === "listingType"
            ? listingLabel(value!)
            : key === "propertyType"
              ? propertyTypeLabel(value!)
              : value;
        return (
          <span key={key} className="chip">
            {labels[key] ?? key}: {display}
          </span>
        );
      })}
      <Link href="/listings" className="chip">
        Tümünü temizle
      </Link>
    </div>
  );
}

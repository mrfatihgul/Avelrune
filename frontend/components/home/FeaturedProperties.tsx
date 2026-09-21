import Link from "next/link";
import { PropertySummary } from "@/lib/types";
import { PropertyCard } from "../property/PropertyCard";

export function FeaturedProperties({ properties }: { properties: PropertySummary[] }) {
  const [first, second, third, ...rest] = properties;

  return (
    <section className="section">
      <div className="wrap">
        <div className="section-head">
          <div>
            <span className="eyebrow">Seçkin portföy</span>
            <h2 className="display">Şu anda üzerinde durduğumuz adresler.</h2>
          </div>
          <Link href="/listings" className="btn btn-ghost">
            Tüm portföy
          </Link>
        </div>
        {properties.length === 0 ? (
          <p className="empty">Şu anda gösterilecek ilan bulunmuyor.</p>
        ) : (
          <>
            <div className="featured-grid">
              {first ? <PropertyCard property={first} variant="large" /> : null}
              <div className="featured-side">
                {second ? <PropertyCard property={second} variant="wide" /> : null}
                {third ? <PropertyCard property={third} variant="wide" /> : null}
              </div>
            </div>
            {rest.length > 0 ? (
              <div className="listing-grid" style={{ marginTop: 28 }}>
                {rest.slice(0, 4).map((property) => (
                  <PropertyCard key={property.id} property={property} />
                ))}
              </div>
            ) : null}
          </>
        )}
      </div>
    </section>
  );
}

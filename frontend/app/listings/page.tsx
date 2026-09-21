import { Suspense } from "react";
import type { Metadata } from "next";
import { SiteShell } from "@/components/layout/SiteShell";
import { ActiveFilters } from "@/components/property/ActiveFilters";
import { Filters } from "@/components/property/Filters";
import { PropertyCard } from "@/components/property/PropertyCard";
import { SortSelect } from "@/components/property/SortSelect";
import { ListingsPager } from "@/components/property/ListingsPager";
import { getFilterMeta, getProperties } from "@/lib/format";
import { SearchParams } from "@/lib/types";

export const dynamic = "force-dynamic";

export const metadata: Metadata = {
  title: "İlanlar",
  description: "Avelrune Gayrimenkul satılık ve kiralık yayınlanan konut ilanları.",
  alternates: { canonical: "/listings" },
};

export default async function ListingsPage({
  searchParams,
}: {
  searchParams: Promise<SearchParams>;
}) {
  const params = await searchParams;
  let properties;
  let meta;
  try {
    [properties, meta] = await Promise.all([getProperties(params), getFilterMeta()]);
  } catch {
    return (
      <SiteShell>
        <section className="page-hero wrap">
          <h1 className="display">İlanlar</h1>
          <p className="error-box">İlanlar şu anda yüklenemedi. Lütfen kısa süre sonra yeniden deneyin.</p>
        </section>
      </SiteShell>
    );
  }

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">İlanlar</span>
        <h1 className="display">Seçilmiş adresler.</h1>
      </section>
      <section className="wrap listing-layout">
        <Suspense>
          <Filters meta={meta} current={params} />
        </Suspense>
        <div>
          <Suspense>
            <ActiveFilters params={params} />
            <div className="results-head">
              <p>{properties.totalElements} ilan</p>
              <SortSelect current={params.sort} />
            </div>
          </Suspense>
          {properties.content.length === 0 ? (
            <div className="empty">
              Bu ölçütlere uyan ilan yok. Filtreleri azaltmayı deneyin.
            </div>
          ) : (
            <>
              <div className="listing-grid">
                {properties.content.map((property) => (
                  <PropertyCard key={property.id} property={property} />
                ))}
              </div>
              <Suspense>
                <ListingsPager page={properties.page} totalPages={properties.totalPages} />
              </Suspense>
            </>
          )}
        </div>
      </section>
    </SiteShell>
  );
}

import type { Metadata } from "next";
import Image from "next/image";
import { notFound } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { OwnerMessageForm } from "@/components/owner/OwnerMessageForm";
import { PropertyCard } from "@/components/property/PropertyCard";
import { PropertyGallery } from "@/components/property/PropertyGallery";
import { formatPrice, getProperty, getSimilar, heatingLabel, listingLabel, propertyTypeLabel } from "@/lib/format";

type Props = { params: Promise<{ slug: string }> };

export const dynamic = "force-dynamic";

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  try {
    const property = await getProperty(slug);
    return {
      title: property.title,
      description: `${property.city} ${property.district} — ${listingLabel(property.listingType)} ${propertyTypeLabel(property.propertyType)}.`,
      alternates: { canonical: `/ilan/${property.slug}` },
      openGraph: {
        title: property.title,
        images: property.images[0] ? [property.images[0].url] : undefined,
      },
    };
  } catch {
    return { title: "İlan bulunamadı" };
  }
}

export default async function PropertyDetailPage({ params }: Props) {
  const { slug } = await params;
  try {
    const [property, similar] = await Promise.all([getProperty(slug), getSimilar(slug)]);
    return (
      <SiteShell>
        <section className="page-hero wrap">
          <span className="eyebrow">
            {listingLabel(property.listingType)} · {property.city} / {property.district}
          </span>
          <h1 className="display">{property.title}</h1>
        </section>
        <PropertyGallery images={property.images} title={property.title} />
        <section className="wrap detail-layout">
          <div>
            <div className="prose">
              {property.description.split(". ").slice(0, 2).join(". ") + "."}
            </div>
            <dl className="spec-list">
              <div>
                <dt>Oda</dt>
                <dd>{property.roomCount}</dd>
              </div>
              <div>
                <dt>Brüt</dt>
                <dd>{property.grossArea} m²</dd>
              </div>
              <div>
                <dt>Net</dt>
                <dd>{property.netArea} m²</dd>
              </div>
              <div>
                <dt>Bina yaşı</dt>
                <dd>{property.buildingAge}</dd>
              </div>
              <div>
                <dt>Kat</dt>
                <dd>
                  {property.floor} / {property.totalFloors}
                </dd>
              </div>
              <div>
                <dt>Isıtma</dt>
                <dd>{heatingLabel(property.heatingType)}</dd>
              </div>
              <div>
                <dt>Eşya</dt>
                <dd>{property.furnished ? "Eşyalı" : "Eşyasız"}</dd>
              </div>
              <div>
                <dt>Tip</dt>
                <dd>{propertyTypeLabel(property.propertyType)}</dd>
              </div>
            </dl>
            <div className="prose">
              <p>{property.description}</p>
            </div>
            <ul className="feature-list">
              {property.features.map((feature) => (
                <li key={feature}>{feature}</li>
              ))}
            </ul>
            <div className="map-block">
              <div>
                <div className="map-pin" />
                <p className="eyebrow">Konum</p>
                <p style={{ marginTop: 8 }}>
                  {property.neighborhood}, {property.district}
                  <br />
                  {property.city}
                </p>
              </div>
            </div>
          </div>
          <aside className="side-panel">
            <p className="price-block">{formatPrice(property.price, property.listingType)}</p>
            <p style={{ color: "var(--muted)", marginBottom: 24 }}>
              {property.neighborhood}, {property.district}
            </p>
            {property.agent ? (
              <div className="agent-card">
                <Image
                  src={property.agent.photoUrl}
                  alt={property.agent.fullName}
                  width={84}
                  height={108}
                />
                <div>
                  <p className="eyebrow">Danışman</p>
                  <h3 style={{ fontFamily: "var(--font-serif)", fontSize: "1.4rem", fontWeight: 400 }}>
                    {property.agent.fullName}
                  </h3>
                  <p>{property.agent.expertise}</p>
                  <a className="btn btn-ghost" href={`tel:${property.agent.phone.replace(/\s/g, "")}`}>
                    Ara
                  </a>
                </div>
              </div>
            ) : null}
            <OwnerMessageForm
              slug={property.slug}
              propertyId={property.id}
              ownerUserId={property.ownerUserId ?? property.assignedUserId}
            />
          </aside>
        </section>
        {similar.length > 0 ? (
          <section className="section" style={{ paddingTop: 0 }}>
            <div className="wrap">
              <div className="section-head">
                <h2 className="display">Yakın dosyalar</h2>
              </div>
              <div className="listing-grid">
                {similar.map((item) => (
                  <PropertyCard key={item.id} property={item} />
                ))}
              </div>
            </div>
          </section>
        ) : null}
      </SiteShell>
    );
  } catch (error) {
    const status = (error as Error & { status?: number }).status;
    if (status === 404) notFound();
    return (
      <SiteShell>
        <section className="page-hero wrap">
          <h1 className="display">İlan yüklenemedi</h1>
          <p className="error-box">Bağlantı sırasında bir sorun oluştu.</p>
        </section>
      </SiteShell>
    );
  }
}

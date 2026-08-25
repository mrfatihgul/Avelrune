import Image from "next/image";
import Link from "next/link";
import { formatPrice, listingLabel, propertyTypeLabel } from "@/lib/format";
import { PropertySummary } from "@/lib/types";

export function PropertyCard({
  property,
  variant = "regular",
}: {
  property: PropertySummary;
  variant?: "large" | "wide" | "regular";
}) {
  return (
    <article className={`property-card is-${variant}`}>
      <Link href={`/listing/${property.slug}`}>
        <div className="media">
          <span className="tag">{listingLabel(property.listingType)}</span>
          {property.coverImage ? (
            <Image
              src={property.coverImage}
              alt={property.title}
              fill
              sizes={variant === "large" ? "(max-width: 768px) 100vw, 55vw" : "(max-width: 768px) 100vw, 33vw"}
            />
          ) : null}
        </div>
        <div className="property-meta">
          <span>
            {property.city} / {property.district}
          </span>
          <span>{propertyTypeLabel(property.propertyType)}</span>
        </div>
        <h3>{property.title}</h3>
        <div className="property-meta">
          <span className="price">{formatPrice(property.price, property.listingType)}</span>
          <span>
            {property.roomCount} · {property.grossArea} m²
          </span>
        </div>
      </Link>
    </article>
  );
}

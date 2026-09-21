import Image from "next/image";
import Link from "next/link";

const regions = [
  { name: "İstanbul", image: "/images/regions/istanbul.jpg", city: "İstanbul" },
  { name: "Bodrum", image: "/images/regions/bodrum.jpg", city: "Muğla", district: "Bodrum" },
  { name: "Çeşme", image: "/images/regions/cesme.jpg", city: "İzmir", district: "Çeşme" },
  { name: "Urla", image: "/images/regions/urla.jpg", city: "İzmir", district: "Urla" },
  { name: "Antalya", image: "/images/regions/antalya.jpg", city: "Antalya" },
];

export function Regions() {
  return (
    <section className="section" style={{ paddingTop: 0 }}>
      <div className="wrap">
        <div className="section-head">
          <div>
            <span className="eyebrow">Bölgeler</span>
            <h2 className="display">Çalıştığımız kıyılar ve kentler.</h2>
          </div>
        </div>
        <div className="regions">
          {regions.map((region) => {
            const params = new URLSearchParams({ city: region.city });
            if (region.district) params.set("district", region.district);
            return (
              <Link key={region.name} href={`/listings?${params.toString()}`} className="region">
                <Image src={region.image} alt={region.name} fill sizes="40vw" />
                <div className="region-copy">
                  <span className="eyebrow">İlanlar</span>
                  <strong>{region.name}</strong>
                </div>
              </Link>
            );
          })}
        </div>
      </div>
    </section>
  );
}

import Image from "next/image";
import { FilterMeta } from "@/lib/types";
import { HeroSearch } from "./HeroSearch";

export function Hero({ meta }: { meta: FilterMeta }) {
  return (
    <section className="hero">
      <div className="hero-media">
        <Image
          src="/images/hero.jpg"
          alt="Avelrune Gayrimenkul seçilmiş konut"
          fill
          priority
          sizes="100vw"
        />
      </div>
      <div className="hero-content">
        <span className="eyebrow">Avelrune Gayrimenkul</span>
        <h1 className="display">Yaşam alanından fazlasını keşfedin.</h1>
        <p>
          İstanbul’dan Ege ve Akdeniz’e, az sayıda ve yerinde görülmüş konutları
          aynı sakinlikle sunuyoruz.
        </p>
        <HeroSearch meta={meta} />
      </div>
    </section>
  );
}

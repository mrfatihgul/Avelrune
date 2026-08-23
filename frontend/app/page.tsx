import type { Metadata } from "next";
import { Advisors } from "@/components/home/Advisors";
import { Approach } from "@/components/home/Approach";
import { FeaturedProperties } from "@/components/home/FeaturedProperties";
import { Hero } from "@/components/home/Hero";
import { HomeCta } from "@/components/home/HomeCta";
import { Regions } from "@/components/home/Regions";
import { SiteShell } from "@/components/layout/SiteShell";
import { getAgents, getFeaturedProperties, getFilterMeta } from "@/lib/format";
import { Agent, FilterMeta, PropertySummary } from "@/lib/types";

export const dynamic = "force-dynamic";

export const metadata: Metadata = {
  title: "Avelrune Gayrimenkul",
  description:
    "İstanbul, Bodrum, Çeşme, Urla ve Antalya’da seçilmiş satılık ve kiralık konutlar.",
  alternates: { canonical: "/" },
};

export default async function HomePage() {
  let featured: PropertySummary[] = [];
  let agents: Agent[] = [];
  let meta: FilterMeta = { cities: [], propertyTypes: [], listingTypes: [] };

  try {
    [featured, agents, meta] = await Promise.all([
      getFeaturedProperties(),
      getAgents(),
      getFilterMeta(),
    ]);
  } catch {
    featured = [];
    agents = [];
  }

  return (
    <SiteShell overHero>
      <Hero meta={meta} />
      <FeaturedProperties properties={featured} />
      <Regions />
      <Approach />
      <Advisors agents={agents} />
      <HomeCta />
    </SiteShell>
  );
}

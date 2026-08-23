import type { MetadataRoute } from "next";

const site = process.env.NEXT_PUBLIC_SITE_URL || "http://localhost";

export default function sitemap(): MetadataRoute.Sitemap {
  return ["", "/ilanlar", "/hakkimizda", "/iletisim"].map((path) => ({
    url: `${site}${path}`,
    lastModified: new Date(),
  }));
}

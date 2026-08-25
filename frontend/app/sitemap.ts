import type { MetadataRoute } from "next";

const site = process.env.NEXT_PUBLIC_SITE_URL || "https://localhost";

export default function sitemap(): MetadataRoute.Sitemap {
  return ["", "/listings", "/about", "/contact"].map((path) => ({
    url: `${site}${path}`,
    lastModified: new Date(),
  }));
}

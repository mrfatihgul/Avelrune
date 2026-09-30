import type { NextConfig } from "next";

const apiInternalUrl = process.env.API_INTERNAL_URL || "http://localhost:8080";

const nextConfig: NextConfig = {
  output: "standalone",
  poweredByHeader: false,
  images: {
    formats: ["image/avif", "image/webp"],
    deviceSizes: [390, 640, 768, 1024, 1280, 1440, 1920],
    imageSizes: [96, 160, 240, 384, 640],
  },
  async redirects() {
    return [
      { source: "/giris", destination: "/login", permanent: true },
      { source: "/uye-ol", destination: "/register", permanent: true },
      { source: "/ilanlar", destination: "/listings", permanent: true },
      { source: "/ilan/:slug", destination: "/listing/:slug", permanent: true },
      { source: "/hakkimizda", destination: "/about", permanent: true },
      { source: "/iletisim", destination: "/contact", permanent: true },
      { source: "/hesabim", destination: "/account", permanent: true },
      { source: "/evlerim", destination: "/my-listings", permanent: true },
      { source: "/evlerim/yeni", destination: "/my-listings/new", permanent: true },
      { source: "/evlerim/:id/duzenle", destination: "/my-listings/:id/edit", permanent: true },
      { source: "/evlerim/:id", destination: "/my-listings/:id", permanent: true },
    ];
  },
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${apiInternalUrl}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;

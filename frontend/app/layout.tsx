import type { Metadata } from "next";
import { Cormorant_Garamond, Outfit } from "next/font/google";
import "./globals.css";

const serif = Cormorant_Garamond({
  subsets: ["latin", "latin-ext"],
  weight: ["400", "500", "600"],
  variable: "--font-serif",
  display: "swap",
});

const sans = Outfit({
  subsets: ["latin", "latin-ext"],
  weight: ["300", "400", "500"],
  variable: "--font-sans",
  display: "swap",
});

const siteUrl = process.env.NEXT_PUBLIC_SITE_URL || "https://localhost";

export const metadata: Metadata = {
  metadataBase: new URL(siteUrl),
  title: {
    default: "Avelrune Gayrimenkul",
    template: "%s — Avelrune Gayrimenkul",
  },
  description:
    "Avelrune Gayrimenkul, İstanbul’dan Ege ve Akdeniz’e seçilmiş konutları sakin bir dikkatle sunar.",
  openGraph: {
    title: "Avelrune Gayrimenkul",
    description: "Her adres, bir tercih.",
    type: "website",
    locale: "tr_TR",
    siteName: "Avelrune Gayrimenkul",
  },
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="tr">
      <body className={`${serif.variable} ${sans.variable}`}>{children}</body>
    </html>
  );
}

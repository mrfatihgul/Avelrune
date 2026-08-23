import type { Metadata } from "next";
import { Suspense } from "react";
import { ContactForm } from "@/components/home/ContactForm";
import { SiteShell } from "@/components/layout/SiteShell";

export const metadata: Metadata = {
  title: "İletişim",
  description: "Avelrune Gayrimenkul ile portföy ve görüşme için yazın.",
  alternates: { canonical: "/iletisim" },
};

export default function ContactPage() {
  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">İletişim</span>
        <h1 className="display">Kısa yazın. Yerinde devam ederiz.</h1>
      </section>
      <section className="wrap contact-layout">
        <div>
          <p className="eyebrow">Ofis</p>
          <p style={{ marginTop: 16, fontSize: "1.2rem" }}>
            Teşvikiye Caddesi No: 48
            <br />
            Nişantaşı, İstanbul
          </p>
          <p style={{ marginTop: 24 }}>
            Hafta içi 10.00–18.30
            <br />
            Cumartesi randevu ile
          </p>
          <p style={{ marginTop: 24 }}>
            <a href="tel:+902122551840">0212 255 18 40</a>
            <br />
            <a href="mailto:merhaba@avelrune.com">merhaba@avelrune.com</a>
          </p>
        </div>
        <Suspense>
          <ContactForm />
        </Suspense>
      </section>
    </SiteShell>
  );
}

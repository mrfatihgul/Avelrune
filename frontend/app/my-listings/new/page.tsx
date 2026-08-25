"use client";

import { SiteShell } from "@/components/layout/SiteShell";
import { OwnerListingForm } from "@/components/owner/OwnerListingForm";
import { useRequireUser } from "@/lib/useRequireUser";

export default function NewListingPage() {
  const { ready } = useRequireUser();

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Evlerim</span>
        <h1 className="display">Yeni ilan</h1>
        <p>Satılık veya kiralık olarak evinizi yayınlayın. Mesajlar bu ilanın sayfasında toplanır.</p>
      </section>
      <section className="wrap owner-section" style={{ maxWidth: 920 }}>
        {ready ? <OwnerListingForm /> : <div className="skeleton" />}
      </section>
    </SiteShell>
  );
}

"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { OwnerListingForm } from "@/components/owner/OwnerListingForm";
import { getToken, getUser } from "@/lib/auth";

export default function NewListingPage() {
  const router = useRouter();

  useEffect(() => {
    const token = getToken();
    const user = getUser();
    if (!token || !user) {
      router.replace("/giris");
      return;
    }
    if (user.role === "ADMIN") router.replace("/admin");
  }, [router]);

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Evlerim</span>
        <h1 className="display">Yeni ilan</h1>
        <p>Satılık veya kiralık olarak evinizi yayınlayın. Mesajlar bu ilanın sayfasında toplanır.</p>
      </section>
      <section className="wrap owner-section" style={{ maxWidth: 920 }}>
        <OwnerListingForm />
      </section>
    </SiteShell>
  );
}

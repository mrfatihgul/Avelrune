"use client";

import { AdminShell } from "@/components/admin/AdminShell";
import { PropertyForm } from "@/components/admin/PropertyForm";

export default function NewPropertyPage() {
  return (
    <AdminShell>
      <p className="eyebrow">Yeni ilan</p>
      <h1 className="display" style={{ fontSize: "2.6rem", marginBottom: 24 }}>
        Yeni ilan
      </h1>
      <PropertyForm />
    </AdminShell>
  );
}

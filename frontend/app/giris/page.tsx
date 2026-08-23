"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { SiteShell } from "@/components/layout/SiteShell";
import { apiFetch } from "@/lib/api";
import { saveSession } from "@/lib/auth";
import { AuthUser } from "@/lib/types";

export default function LoginPage() {
  const router = useRouter();
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function onSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    setLoading(true);
    setError("");
    try {
      const response = await apiFetch<{
        accessToken: string;
        user: AuthUser;
      }>("/api/auth/login", {
        method: "POST",
        body: JSON.stringify({
          email: form.get("email"),
          password: form.get("password"),
        }),
      });
      saveSession(response.accessToken, response.user);
      router.replace(response.user.role === "ADMIN" ? "/admin" : "/evlerim");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Giriş yapılamadı.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Hesap</span>
        <h1 className="display">Giriş</h1>
      </section>
      <section className="wrap" style={{ maxWidth: 520, paddingBottom: 80 }}>
        <form className="form" onSubmit={onSubmit}>
          <label className="field">
            <span>E-posta</span>
            <input name="email" type="email" autoComplete="username" required />
          </label>
          <label className="field">
            <span>Şifre</span>
            <input name="password" type="password" autoComplete="current-password" required />
          </label>
          {error ? <p className="form-error">{error}</p> : null}
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Kontrol ediliyor…" : "Giriş yap"}
          </button>
        </form>
        <p style={{ marginTop: 20, color: "var(--muted)" }}>
          Hesabınız yok mu? <Link href="/uye-ol">Üye olun</Link>
        </p>
      </section>
    </SiteShell>
  );
}

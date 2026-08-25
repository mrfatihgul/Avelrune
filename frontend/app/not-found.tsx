import Link from "next/link";
import { SiteShell } from "@/components/layout/SiteShell";

export default function NotFound() {
  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">404</span>
        <h1 className="display">Bu adres portföyde yok.</h1>
        <p style={{ marginTop: 18, maxWidth: 420 }}>
          Sayfa taşınmış veya hiç açılmamış olabilir.
        </p>
        <div style={{ marginTop: 28, display: "flex", gap: 12 }}>
          <Link href="/" className="btn">
            Ana sayfa
          </Link>
          <Link href="/listings" className="btn btn-ghost">
            Portföy
          </Link>
        </div>
      </section>
    </SiteShell>
  );
}

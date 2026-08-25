import Link from "next/link";
import { Logo } from "./Logo";

export function Footer() {
  return (
    <footer className="footer">
      <div className="wrap footer-grid">
        <div>
          <Logo />
          <p style={{ marginTop: 18, maxWidth: 360 }}>
            Seçilmiş konutları, acele etmeden ve yerinde görerek çalışıyoruz.
          </p>
        </div>
        <div>
          <p className="eyebrow">Ofis</p>
          <p style={{ marginTop: 12 }}>
            Teşvikiye Caddesi No: 48
            <br />
            Nişantaşı, İstanbul
          </p>
          <p style={{ marginTop: 12 }}>
            <a href="tel:+902122551840">0212 255 18 40</a>
            <br />
            <a href="mailto:merhaba@avelrune.com">merhaba@avelrune.com</a>
          </p>
        </div>
        <div>
          <p className="eyebrow">Sayfalar</p>
          <p style={{ marginTop: 12, display: "grid", gap: 8 }}>
            <Link href="/listings">Portföy</Link>
            <Link href="/about">Hakkımızda</Link>
            <Link href="/contact">İletişim</Link>
            <Link href="/login">Giriş</Link>
            <Link href="/register">Üye Ol</Link>
            <Link href="/admin/login">Yönetim</Link>
          </p>
        </div>
      </div>
      <div className="wrap footer-bottom">
        <span>© {new Date().getFullYear()} Avelrune Gayrimenkul</span>
        <span>Her adres, bir tercih.</span>
      </div>
    </footer>
  );
}

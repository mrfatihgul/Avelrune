"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { HeaderAuth } from "./HeaderAuth";
import { Logo } from "./Logo";

const links = [
  { href: "/", label: "Ana Sayfa" },
  { href: "/ilanlar?listingType=SALE", label: "Satılık" },
  { href: "/ilanlar?listingType=RENT", label: "Kiralık" },
  { href: "/ilanlar", label: "Portföy" },
  { href: "/hakkimizda", label: "Hakkımızda" },
  { href: "/iletisim", label: "İletişim" },
];

export function Header({ overHero = false }: { overHero?: boolean }) {
  const pathname = usePathname();
  const [scrolled, setScrolled] = useState(false);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    setOpen(false);
  }, [pathname]);

  const isActive = (href: string) => {
    if (href === "/") return pathname === "/";
    return pathname.startsWith(href.split("?")[0]) && href !== "/";
  };

  return (
    <>
      <header
        className={[
          "header",
          overHero ? "is-over-hero" : "is-solid",
          scrolled ? "is-scrolled" : "",
        ].join(" ")}
      >
        <div className="header-inner">
          <Logo />
          <nav className="nav-desktop" aria-label="Ana menü">
            {links.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                className={isActive(link.href) ? "is-active" : undefined}
              >
                {link.label}
              </Link>
            ))}
          </nav>
          <div className="header-tools">
            <nav className="nav-auth nav-auth-desktop" aria-label="Hesap">
              <HeaderAuth />
            </nav>
            <button
              className={`menu-toggle ${open ? "is-open" : ""}`}
              aria-label={open ? "Menüyü kapat" : "Menüyü aç"}
              onClick={() => setOpen((value) => !value)}
            >
              <span />
            </button>
          </div>
        </div>
      </header>
      <nav className={`mobile-nav ${open ? "is-open" : ""}`} aria-label="Mobil menü">
        {links.map((link) => (
          <Link key={link.href} href={link.href}>
            {link.label}
          </Link>
        ))}
        <div className="nav-auth">
          <HeaderAuth />
        </div>
      </nav>
    </>
  );
}

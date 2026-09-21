"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import { HeaderAuth } from "./HeaderAuth";
import { Logo } from "./Logo";

const links = [
  { href: "/", label: "Ana Sayfa" },
  { href: "/listings?listingType=SALE", label: "Satılık" },
  { href: "/listings?listingType=RENT", label: "Kiralık" },
  { href: "/listings", label: "İlanlar" },
  { href: "/about", label: "Hakkımızda" },
  { href: "/contact", label: "İletişim" },
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

  useEffect(() => {
    const desktop = window.matchMedia("(min-width: 1201px)");
    const closeOnDesktop = () => {
      if (desktop.matches) setOpen(false);
    };
    closeOnDesktop();
    desktop.addEventListener("change", closeOnDesktop);
    return () => desktop.removeEventListener("change", closeOnDesktop);
  }, []);

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
              aria-expanded={open}
              aria-controls="mobile-nav"
              onClick={() => setOpen((value) => !value)}
            >
              <span />
            </button>
          </div>
        </div>
      </header>
      <nav id="mobile-nav" className={`mobile-nav ${open ? "is-open" : ""}`} aria-label="Mobil menü">
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

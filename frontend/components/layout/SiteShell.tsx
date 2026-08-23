import { Footer } from "./Footer";
import { Header } from "./Header";

export function SiteShell({
  children,
  overHero = false,
}: {
  children: React.ReactNode;
  overHero?: boolean;
}) {
  return (
    <div className="site">
      <Header overHero={overHero} />
      <main className="site-main">{children}</main>
      <Footer />
    </div>
  );
}

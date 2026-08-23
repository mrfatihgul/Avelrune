import Link from "next/link";

export function HomeCta() {
  return (
    <section className="cta">
      <div className="wrap cta-inner">
        <div>
          <span className="eyebrow">Görüşme</span>
          <h2 className="display">Portföyünüzü sakin bir dosyayla konuşalım.</h2>
        </div>
        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <Link href="/ilanlar" className="btn btn-light">
            Portföyü incele
          </Link>
          <Link href="/iletisim" className="btn" style={{ background: "transparent", color: "#f1ece4", borderColor: "#f1ece4" }}>
            Yazın
          </Link>
        </div>
      </div>
    </section>
  );
}

import Link from "next/link";

export function HomeCta() {
  return (
    <section className="cta">
      <div className="wrap cta-inner">
        <div>
          <span className="eyebrow">Görüşme</span>
          <h2 className="display">Evinizi sakin bir dosyayla konuşalım.</h2>
        </div>
        <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
          <Link href="/listings" className="btn btn-light">
            İlanları incele
          </Link>
          <Link href="/contact" className="btn" style={{ background: "transparent", color: "#f1ece4", borderColor: "#f1ece4" }}>
            Yazın
          </Link>
        </div>
      </div>
    </section>
  );
}

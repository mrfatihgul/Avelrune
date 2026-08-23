import Image from "next/image";

export function Approach() {
  return (
    <section className="section">
      <div className="wrap approach">
        <div>
          <span className="eyebrow">Yaklaşım</span>
          <h2 className="display">Az ilan. Yerinde bakılmış dosyalar.</h2>
          <p>
            Avelrune, her hafta onlarca yeni ilanı yayına almak için kurulmadı.
            Önce yapıyı, sokağı ve gün ışığını görüyoruz. Ardından yalnızca
            durması gerekenleri portföye alıyoruz.
          </p>
          <p style={{ marginTop: 16 }}>
            Alıcıya acele ettirmiyoruz. Satıcıya da olduğundan fazlasını
            söylemiyoruz. Değer, ölçülerek konuşulur.
          </p>
          <div className="stats-inline">
            <div>
              <strong>500+</strong>
              <span>tamamlanan işlem</span>
            </div>
            <div>
              <strong>12</strong>
              <span>yıllık çalışma</span>
            </div>
            <div>
              <strong>250+</strong>
              <span>seçkin portföy</span>
            </div>
            <div>
              <strong>%98</strong>
              <span>tekrar başvuru</span>
            </div>
          </div>
        </div>
        <div className="approach-image">
          <Image
            src="/images/about/atelier.jpg"
            alt="Avelrune çalışma yaklaşımı"
            fill
            sizes="40vw"
          />
        </div>
      </div>
    </section>
  );
}

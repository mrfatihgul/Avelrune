import type { Metadata } from "next";
import Image from "next/image";
import { SiteShell } from "@/components/layout/SiteShell";
import { Advisors } from "@/components/home/Advisors";
import { getAgents } from "@/lib/format";

export const dynamic = "force-dynamic";

export const metadata: Metadata = {
  title: "Hakkımızda",
  description: "Avelrune Gayrimenkul’ün çalışma biçimi, değerleri ve ekibi.",
  alternates: { canonical: "/about" },
};

export default async function AboutPage() {
  const agents = await getAgents().catch(() => []);

  return (
    <SiteShell>
      <section className="page-hero wrap">
        <span className="eyebrow">Hakkımızda</span>
        <h1 className="display">Az dosya. Net konuşma.</h1>
      </section>
      <section className="wrap about-grid">
        <div className="prose">
          <p>
            Avelrune Gayrimenkul, 2014’ten bu yana İstanbul, Ege ve Akdeniz’de
            seçilmiş konutlar üzerinde çalışıyor. Ajans kalabalığından uzak,
            küçük bir masa düzeniyle ilerliyoruz.
          </p>
          <p>
            İlan sayısını şişirmiyoruz. Bir yapıyı ilanlara almadan önce sokağı,
            gün ışığını, kat planını ve komşuluk mesafesini yerinde görüyoruz.
            Bu yüzden listelerimiz kısa, dosyalarımız uzundur.
          </p>
          <p>
            Alıcıya “kaçırmayın” demeyiz. Satıcıya da evini olduğundan büyük
            göstermeyiz. Fiyat, karşılaştırmayla ve sakin bir tempo ile konuşulur.
          </p>
        </div>
        <div className="approach-image">
          <Image src="/images/about/detail.jpg" alt="Avelrune iç mekân çalışması" fill sizes="40vw" />
        </div>
      </section>
      <section className="wrap values">
        <div>
          <h3>Ölçü</h3>
          <p>Metrekare, cephe ve gürültü aynı dosyada durur. Tahminle yetinmeyiz.</p>
        </div>
        <div>
          <h3>Tempo</h3>
          <p>İlk görüşmede karar istemeyiz. Yerinde ikinci bakış her zaman mümkündür.</p>
        </div>
        <div>
          <h3>Sınır</h3>
          <p>Çalışmadığımız bölgeye ilan açmayız. Uzmanlık, her yere yetişmek değildir.</p>
        </div>
        <div>
          <h3>Kayıt</h3>
          <p>Sözlü vaat bırakmayız. Görüşmeler kısa notla tutulur, fiyat yazılı ilerler.</p>
        </div>
      </section>
      <section className="wrap" style={{ paddingBottom: 80 }}>
        <div className="stats-inline">
          <div>
            <strong>12 yıl</strong>
            <span>kesintisiz çalışma</span>
          </div>
          <div>
            <strong>4 şehir</strong>
            <span>odak bölge</span>
          </div>
          <div>
            <strong>18 kişi</strong>
            <span>ofis ve saha</span>
          </div>
          <div>
            <strong>1 masa</strong>
            <span>karar mercii</span>
          </div>
        </div>
      </section>
      <Advisors agents={agents} />
    </SiteShell>
  );
}

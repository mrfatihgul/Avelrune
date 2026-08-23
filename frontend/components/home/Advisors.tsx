import Image from "next/image";
import { Agent } from "@/lib/types";

export function Advisors({ agents }: { agents: Agent[] }) {
  return (
    <section className="section" style={{ paddingTop: 0 }}>
      <div className="wrap">
        <div className="section-head">
          <div>
            <span className="eyebrow">Danışmanlar</span>
            <h2 className="display">Bölgeyi bilen kişiler.</h2>
          </div>
        </div>
        <div className="advisors">
          {agents.map((agent) => (
            <article key={agent.id} className="advisor">
              <Image src={agent.photoUrl} alt={agent.fullName} width={480} height={640} />
              <h3>{agent.fullName}</h3>
              <p>{agent.expertise}</p>
              <p style={{ marginTop: 8 }}>{agent.bio}</p>
              <p style={{ marginTop: 12 }}>
                <a href={`tel:${agent.phone.replace(/\s/g, "")}`}>{agent.phone}</a>
                <br />
                <a href={`mailto:${agent.email}`}>{agent.email}</a>
              </p>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}

import Link from "next/link";

export function Logo() {
  return (
    <Link href="/" className="logo" aria-label="Avelrune Gayrimenkul">
      <span className="logo-word">AVELRUNE</span>
      <span className="logo-sub">GAYRİMENKUL</span>
    </Link>
  );
}

"use client";

import Link from "next/link";
import { usePathname, useSearchParams } from "next/navigation";

export function ListingsPager({
  page,
  totalPages,
}: {
  page: number;
  totalPages: number;
}) {
  const pathname = usePathname();
  const searchParams = useSearchParams();

  if (totalPages <= 1) {
    return null;
  }

  function hrefFor(nextPage: number) {
    const params = new URLSearchParams(searchParams.toString());
    if (nextPage <= 0) {
      params.delete("page");
    } else {
      params.set("page", String(nextPage));
    }
    const query = params.toString();
    return query ? `${pathname}?${query}` : pathname;
  }

  return (
    <nav className="listings-pager" aria-label="Sayfalar">
      {page > 0 ? (
        <Link className="btn btn-ghost" href={hrefFor(page - 1)}>
          Önceki
        </Link>
      ) : (
        <span />
      )}
      <p>
        {page + 1} / {totalPages}
      </p>
      {page + 1 < totalPages ? (
        <Link className="btn btn-ghost" href={hrefFor(page + 1)}>
          Sonraki
        </Link>
      ) : (
        <span />
      )}
    </nav>
  );
}

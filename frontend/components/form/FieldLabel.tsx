export function FieldLabel({ children }: { children: string }) {
  return (
    <span>
      {children}
      <span className="req" aria-hidden="true">
        *
      </span>
      <span className="sr-only"> (zorunlu)</span>
    </span>
  );
}

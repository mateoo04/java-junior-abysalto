export function LoadingSpinner() {
  return (
    <div className="flex justify-center py-12" aria-live="polite" aria-busy="true">
      <div className="h-8 w-8 animate-spin rounded-full border-2 border-slate-600 border-t-emerald-400" />
    </div>
  )
}

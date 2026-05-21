import type { BuyerResponse } from '../types/order'

type BuyerSearchSuggestionsProps = {
  suggestions: BuyerResponse[]
  loading: boolean
  hasSearched: boolean
  selectedBuyerId?: number | null
  onSelect: (buyer: BuyerResponse) => void
}

export function BuyerSearchSuggestions({
  suggestions,
  loading,
  hasSearched,
  selectedBuyerId,
  onSelect,
}: BuyerSearchSuggestionsProps) {
  if (loading) {
    return (
      <p className="text-sm text-slate-500" aria-live="polite">
        Searching customers…
      </p>
    )
  }

  if (!hasSearched) {
    return null
  }

  if (suggestions.length === 0) {
    return (
      <p className="text-sm text-slate-500" role="status">
        No matching customers
      </p>
    )
  }

  return (
    <ul
      role="listbox"
      aria-label="Customer suggestions"
      className="overflow-hidden rounded-lg border border-slate-700 bg-slate-900 shadow-lg"
    >
      {suggestions.map((buyer) => (
        <li key={buyer.buyerId} role="option">
          <button
            type="button"
            onClick={() => onSelect(buyer)}
            className="flex w-full items-center justify-between gap-4 px-4 py-2 text-left text-sm text-slate-200 hover:bg-slate-800 focus:bg-slate-800 focus:outline-none"
          >
            <span className="min-w-0">
              <span className="font-medium text-white">
                {buyer.firstName} {buyer.lastName}
              </span>
              {buyer.title && (
                <span className="ml-2 text-slate-500">({buyer.title})</span>
              )}
            </span>
            {buyer.buyerId === selectedBuyerId && (
              <span className="shrink-0 text-emerald-400" aria-label="Selected customer">
                ✓
              </span>
            )}
          </button>
        </li>
      ))}
    </ul>
  )
}

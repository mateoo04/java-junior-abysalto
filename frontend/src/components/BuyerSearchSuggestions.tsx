import type { BuyerResponse } from '../types/order'

type BuyerSearchSuggestionsProps = {
  suggestions: BuyerResponse[]
  loading: boolean
  hasSearched: boolean
  onSelect: (buyer: BuyerResponse) => void
}

export function BuyerSearchSuggestions({
  suggestions,
  loading,
  hasSearched,
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
            className="w-full px-4 py-2.5 text-left text-sm text-slate-200 hover:bg-slate-800 focus:bg-slate-800 focus:outline-none"
          >
            <span className="font-medium text-white">
              {buyer.firstName} {buyer.lastName}
            </span>
            {buyer.title && (
              <span className="ml-2 text-slate-500">({buyer.title})</span>
            )}
          </button>
        </li>
      ))}
    </ul>
  )
}

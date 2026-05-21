import { formatMoney } from '../lib/format'
import type { MenuItemResponse } from '../types/menu'

type MenuSearchSuggestionsProps = {
  suggestions: MenuItemResponse[]
  loading: boolean
  hasSearched: boolean
  currency?: string
  onSelect: (item: MenuItemResponse) => void
}

export function MenuSearchSuggestions({
  suggestions,
  loading,
  hasSearched,
  currency = 'EUR',
  onSelect,
}: MenuSearchSuggestionsProps) {
  if (loading) {
    return (
      <p className="text-sm text-slate-500" aria-live="polite">
        Searching meals…
      </p>
    )
  }

  if (!hasSearched) {
    return null
  }

  if (suggestions.length === 0) {
    return (
      <p className="text-sm text-slate-500" role="status">
        No meals found
      </p>
    )
  }

  return (
    <ul
      role="listbox"
      aria-label="Meal suggestions"
      className="overflow-hidden rounded-lg border border-slate-700 bg-slate-900 shadow-lg"
    >
      {suggestions.map((item) => (
        <li key={item.menuItemId} role="option">
          <button
            type="button"
            onClick={() => onSelect(item)}
            className="flex w-full items-center justify-between gap-4 px-4 py-2.5 text-left text-sm hover:bg-slate-800 focus:bg-slate-800 focus:outline-none"
          >
            <span className="font-medium text-white">{item.name}</span>
            <span className="text-slate-400">{formatMoney(item.price, currency)}</span>
          </button>
        </li>
      ))}
    </ul>
  )
}

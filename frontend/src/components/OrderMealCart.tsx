import { useState } from 'react'
import { useMenuSearch } from '../hooks/useMenuSearch'
import { formatMoney } from '../lib/format'
import type { CartLine, MenuItemResponse } from '../types/menu'
import { MenuSearchSuggestions } from './MenuSearchSuggestions'

type OrderMealCartProps = {
  cart: CartLine[]
  currency: string
  onChange: (cart: CartLine[]) => void
}

export function OrderMealCart({ cart, currency, onChange }: OrderMealCartProps) {
  const [search, setSearch] = useState('')
  const { suggestions, loading, error, hasSearched } = useMenuSearch(search)

  function addMeal(meal: MenuItemResponse) {
    const existing = cart.find((line) => line.menuItemId === meal.menuItemId)
    if (existing) {
      onChange(
        cart.map((line) =>
          line.menuItemId === meal.menuItemId
            ? { ...line, quantity: line.quantity + 1 }
            : line,
        ),
      )
    } else {
      onChange([
        ...cart,
        {
          menuItemId: meal.menuItemId,
          name: meal.name,
          price: meal.price,
          quantity: 1,
        },
      ])
    }
    setSearch('')
  }

  function updateQuantity(menuItemId: number, quantity: number) {
    if (quantity < 1) return
    onChange(
      cart.map((line) => (line.menuItemId === menuItemId ? { ...line, quantity } : line)),
    )
  }

  function removeLine(menuItemId: number) {
    onChange(cart.filter((line) => line.menuItemId !== menuItemId))
  }

  const cartTotal = cart.reduce((sum, line) => sum + line.price * line.quantity, 0)

  return (
    <section className="space-y-4">
      <h2 className="text-lg font-medium text-white">Meals</h2>

      <label className="block">
        <span className="text-xs text-slate-500">Search and add meals</span>
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Type meal name…"
          className="mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-100"
          autoComplete="off"
        />
      </label>

      {error && (
        <p className="text-sm text-red-400" role="alert">
          {error}
        </p>
      )}

      <MenuSearchSuggestions
        suggestions={suggestions}
        loading={loading}
        hasSearched={hasSearched}
        currency={currency}
        onSelect={addMeal}
      />

      {cart.length === 0 ? (
        <p className="text-sm text-slate-500">Add at least one meal to the order.</p>
      ) : (
        <div className="overflow-hidden rounded-lg border border-slate-800">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-900/80 text-xs text-slate-500">
              <tr>
                <th className="px-4 py-2 font-medium">Meal</th>
                <th className="px-4 py-2 font-medium">Qty</th>
                <th className="px-4 py-2 font-medium text-right">Line total</th>
                <th className="px-4 py-2" />
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {cart.map((line) => (
                <tr key={line.menuItemId} className="bg-slate-900/40">
                  <td className="px-4 py-3">
                    <span className="text-white">{line.name}</span>
                    <span className="mt-0.5 block text-xs text-slate-500">
                      {formatMoney(line.price, currency)} each
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <input
                      type="number"
                      min={1}
                      value={line.quantity}
                      onChange={(e) =>
                        updateQuantity(line.menuItemId, Number(e.target.value))
                      }
                      className="w-16 rounded-lg border border-slate-700 bg-slate-800 px-2 py-1 text-sm"
                    />
                  </td>
                  <td className="px-4 py-3 text-right text-slate-200">
                    {formatMoney(line.price * line.quantity, currency)}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button
                      type="button"
                      onClick={() => removeLine(line.menuItemId)}
                      className="text-sm text-red-400 hover:text-red-300"
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot className="border-t border-slate-800 bg-slate-900/60">
              <tr>
                <td colSpan={2} className="px-4 py-3 font-medium text-slate-300">
                  Cart total
                </td>
                <td className="px-4 py-3 text-right font-semibold text-emerald-400">
                  {formatMoney(cartTotal, currency)}
                </td>
                <td />
              </tr>
            </tfoot>
          </table>
        </div>
      )}
    </section>
  )
}

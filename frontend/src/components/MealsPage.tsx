import { useCallback, useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { listMenuItems } from '../api/menuItems'
import { formatMoney } from '../lib/format'
import type { MenuItemResponse } from '../types/menu'
import { CreateMealForm } from './CreateMealForm'
import { ErrorBanner } from './ErrorBanner'
import { LoadingSpinner } from './LoadingSpinner'

export function MealsPage() {
  const [meals, setMeals] = useState<MenuItemResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setMeals(await listMenuItems())
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load meals')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  return (
    <div className="space-y-8">
      <CreateMealForm onCreated={load} />

      {error && <ErrorBanner message={error} onDismiss={() => setError(null)} />}

      <section className="space-y-4">
        <h2 className="text-lg font-medium text-white">Menu</h2>
        {loading ? (
          <LoadingSpinner />
        ) : meals.length === 0 ? (
          <p className="text-slate-400">No meals yet. Add one above.</p>
        ) : (
          <ul className="divide-y divide-slate-800 rounded-xl border border-slate-800">
            {meals.map((meal) => (
              <li
                key={meal.menuItemId}
                className="flex items-center justify-between gap-4 px-4 py-3"
              >
                <span className="font-medium text-white">{meal.name}</span>
                <span className="text-emerald-400">{formatMoney(meal.price, 'EUR')}</span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}

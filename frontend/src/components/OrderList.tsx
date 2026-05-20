import { useCallback, useEffect, useState } from 'react'
import { ApiError } from '../api/client'
import { listOrders, type OrderSort } from '../api/orders'
import type { OrderResponse } from '../types/order'
import { ErrorBanner } from './ErrorBanner'
import { LoadingSpinner } from './LoadingSpinner'
import { OrderRow } from './OrderRow'
import { OrderSortControls } from './OrderSortControls'

export function OrderList() {
  const [orders, setOrders] = useState<OrderResponse[]>([])
  const [sort, setSort] = useState<OrderSort>('default')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listOrders(sort)
      setOrders(data)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to load orders')
    } finally {
      setLoading(false)
    }
  }, [sort])

  useEffect(() => {
    load()
  }, [load])

  function handleUpdated(updated: OrderResponse) {
    setOrders((prev) => prev.map((o) => (o.orderNr === updated.orderNr ? updated : o)))
  }

  return (
    <div className="space-y-6">
      <OrderSortControls sort={sort} onSortChange={setSort} />

      {error && <ErrorBanner message={error} onDismiss={() => setError(null)} />}

      {loading ? (
        <LoadingSpinner />
      ) : orders.length === 0 ? (
        <p className="py-12 text-center text-slate-400">No orders yet. Create one to get started.</p>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <OrderRow
              key={order.orderNr}
              order={order}
              onUpdated={handleUpdated}
              onError={setError}
            />
          ))}
        </div>
      )}
    </div>
  )
}

import { useState } from 'react'
import { ApiError } from '../api/client'
import { updateOrderStatus } from '../api/orders'
import { formatAddress, formatDateTime, formatMoney } from '../lib/format'
import { ORDER_STATUSES, orderStatusLabel, paymentOptionLabel } from '../lib/labels'
import type { OrderResponse, OrderStatus } from '../types/order'

type OrderRowProps = {
  order: OrderResponse
  onUpdated: (order: OrderResponse) => void
  onError: (message: string) => void
}

export function OrderRow({ order, onUpdated, onError }: OrderRowProps) {
  const [status, setStatus] = useState(order.orderStatus)
  const [saving, setSaving] = useState(false)

  async function handleStatusChange(next: OrderStatus) {
    const previous = status
    setStatus(next)
    setSaving(true)
    try {
      const updated = await updateOrderStatus(order.orderNr, next)
      onUpdated(updated)
    } catch (err) {
      setStatus(previous)
      onError(err instanceof ApiError ? err.message : 'Failed to update status')
    } finally {
      setSaving(false)
    }
  }

  const addr = order.deliveryAddress

  return (
    <article className="rounded-lg border border-slate-800 bg-slate-900/55 p-4 shadow-sm shadow-black/10">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate font-medium text-white">
            #{order.orderNr} · {order.buyerName}
          </p>
          <p className="mt-0.5 text-sm text-slate-400">{formatDateTime(order.orderTime)}</p>
        </div>
        <p className="shrink-0 text-lg font-semibold text-emerald-400">
          {formatMoney(order.totalPrice, order.currency)}
        </p>
      </div>

      <dl className="mt-4 grid gap-x-6 gap-y-3 text-sm sm:grid-cols-2 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)_minmax(0,1.4fr)]">
        <div>
          <dt className="text-slate-500">Payment</dt>
          <dd>{paymentOptionLabel(order.paymentOption)}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Contact</dt>
          <dd>{order.contactNumber}</dd>
        </div>
        <div>
          <dt className="text-slate-500">Delivery</dt>
          <dd>{formatAddress(addr.city, addr.street, addr.homeNumber)}</dd>
        </div>
        {order.note && (
          <div className="sm:col-span-2 lg:col-span-3">
            <dt className="text-slate-500">Note</dt>
            <dd>{order.note}</dd>
          </div>
        )}
      </dl>

      {order.items.length > 0 && (
        <ul className="mt-3 divide-y divide-slate-800 border-t border-slate-800 pt-2 text-sm text-slate-300">
          {order.items.map((item) => (
            <li
              key={item.orderItemId}
              className="flex items-center justify-between gap-4 py-1.5"
            >
              <span className="min-w-0 truncate">
                {item.name} × {item.quantity}
              </span>
              <span className="shrink-0 text-slate-400">
                {formatMoney(item.price * item.quantity, order.currency)}
              </span>
            </li>
          ))}
        </ul>
      )}

      <label className="mt-3 flex flex-wrap items-center gap-2 text-sm">
        <span className="text-slate-500">Status</span>
        <select
          value={status}
          disabled={saving}
          onChange={(e) => handleStatusChange(e.target.value as OrderStatus)}
          className="rounded-lg border border-slate-700 bg-slate-800 px-3 py-1.5 text-slate-100 disabled:opacity-50"
        >
          {ORDER_STATUSES.map((s) => (
            <option key={s} value={s}>
              {orderStatusLabel(s)}
            </option>
          ))}
        </select>
      </label>
    </article>
  )
}

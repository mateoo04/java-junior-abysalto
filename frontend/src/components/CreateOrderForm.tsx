import { useState, type FormEvent } from 'react'
import { ApiError } from '../api/client'
import { createOrder } from '../api/orders'
import { useBuyerSearch } from '../hooks/useBuyerSearch'
import { PAYMENT_OPTIONS, paymentOptionLabel } from '../lib/labels'
import type { CartLine } from '../types/menu'
import type { BuyerResponse, CreateOrderRequest, PaymentOption } from '../types/order'
import { BuyerSearchSuggestions } from './BuyerSearchSuggestions'
import { ErrorBanner } from './ErrorBanner'
import { OrderMealCart } from './OrderMealCart'

const inputClass =
  'mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-100 focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500'

const initialForm = (): CreateOrderRequest => ({
  buyer: { firstName: '', lastName: '', title: '' },
  paymentOption: 'CASH',
  deliveryAddress: { city: '', street: '', homeNumber: '' },
  contactNumber: '',
  note: '',
  currency: 'EUR',
  items: [],
})

type BuyerSnapshot = {
  firstName: string
  lastName: string
  title: string
}

function buyerSnapshot(buyer: CreateOrderRequest['buyer']): BuyerSnapshot {
  return {
    firstName: buyer.firstName,
    lastName: buyer.lastName,
    title: buyer.title ?? '',
  }
}

function snapshotsMatch(a: BuyerSnapshot, b: BuyerSnapshot): boolean {
  return a.firstName === b.firstName && a.lastName === b.lastName && a.title === b.title
}

type CreateOrderFormProps = {
  onCreated: () => void
}

export function CreateOrderForm({ onCreated }: CreateOrderFormProps) {
  const [form, setForm] = useState<CreateOrderRequest>(initialForm)
  const [cart, setCart] = useState<CartLine[]>([])
  const [selectedBuyerId, setSelectedBuyerId] = useState<number | null>(null)
  const [selectedSnapshot, setSelectedSnapshot] = useState<BuyerSnapshot | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const { suggestions, loading, error: searchError, hasSearched } = useBuyerSearch(
    form.buyer.firstName,
    form.buyer.lastName,
  )

  function updateBuyer(patch: Partial<CreateOrderRequest['buyer']>) {
    const nextBuyer = { ...form.buyer, ...patch }
    if (selectedBuyerId != null && selectedSnapshot != null) {
      const next = buyerSnapshot(nextBuyer)
      if (!snapshotsMatch(next, selectedSnapshot)) {
        setSelectedBuyerId(null)
        setSelectedSnapshot(null)
      }
    }
    setForm((f) => ({ ...f, buyer: nextBuyer }))
  }

  function handleSelectBuyer(buyer: BuyerResponse) {
    const snapshot: BuyerSnapshot = {
      firstName: buyer.firstName,
      lastName: buyer.lastName,
      title: buyer.title ?? '',
    }
    setSelectedBuyerId(buyer.buyerId)
    setSelectedSnapshot(snapshot)
    setForm((f) => ({
      ...f,
      buyer: {
        firstName: buyer.firstName,
        lastName: buyer.lastName,
        ...(buyer.title ? { title: buyer.title } : {}),
      },
    }))
  }

  function resetForm() {
    setForm(initialForm())
    setCart([])
    setSelectedBuyerId(null)
    setSelectedSnapshot(null)
  }

  function validate(): string | null {
    if (!form.buyer.firstName.trim() || !form.buyer.lastName.trim()) {
      return 'Buyer first and last name are required'
    }
    if (!form.deliveryAddress.city.trim() || !form.deliveryAddress.street.trim()) {
      return 'Delivery city and street are required'
    }
    if (!form.contactNumber.trim()) return 'Contact number is required'
    if (!form.currency.trim()) return 'Currency is required'
    if (cart.length === 0) return 'Add at least one meal to the order'
    for (const line of cart) {
      if (!line.quantity || line.quantity <= 0) return 'Each meal needs quantity greater than zero'
    }
    return null
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    const validationError = validate()
    if (validationError) {
      setError(validationError)
      return
    }

    setSubmitting(true)
    setError(null)

    const payload: CreateOrderRequest = {
      ...form,
      ...(selectedBuyerId != null ? { buyerId: selectedBuyerId } : {}),
      buyer: {
        firstName: form.buyer.firstName.trim(),
        lastName: form.buyer.lastName.trim(),
        ...(form.buyer.title?.trim() ? { title: form.buyer.title.trim() } : {}),
      },
      deliveryAddress: {
        city: form.deliveryAddress.city.trim(),
        street: form.deliveryAddress.street.trim(),
        ...(form.deliveryAddress.homeNumber?.trim()
          ? { homeNumber: form.deliveryAddress.homeNumber.trim() }
          : {}),
      },
      contactNumber: form.contactNumber.trim(),
      currency: form.currency.trim(),
      ...(form.note?.trim() ? { note: form.note.trim() } : {}),
      items: cart.map((line) => ({
        menuItemId: line.menuItemId,
        quantity: line.quantity,
      })),
    }

    try {
      await createOrder(payload)
      resetForm()
      onCreated()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to create order')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="flex flex-wrap items-end justify-between gap-3">
        <div>
          <h2 className="text-xl font-semibold text-white">New order</h2>
          <p className="mt-1 text-sm text-slate-500">Customer, delivery, payment, and meals in one flow.</p>
        </div>
        {selectedBuyerId != null && (
          <span className="rounded-full bg-emerald-500/10 px-3 py-1 text-sm text-emerald-300">
            Existing customer
          </span>
        )}
      </div>

      {error && <ErrorBanner message={error} onDismiss={() => setError(null)} />}

      <div className="grid gap-5 lg:grid-cols-[minmax(0,1fr)_22rem]">
        <div className="space-y-5">
          <section className="rounded-lg border border-slate-800 bg-slate-900/45 p-4">
            <h3 className="text-base font-medium text-white">Buyer</h3>
            <div className="mt-3 grid gap-3 sm:grid-cols-2">
              <label className="block">
                <span className="text-xs text-slate-500">First name</span>
                <input
                  type="text"
                  required
                  value={form.buyer.firstName}
                  onChange={(e) => updateBuyer({ firstName: e.target.value })}
                  className={inputClass}
                  autoComplete="off"
                />
              </label>
              <label className="block">
                <span className="text-xs text-slate-500">Last name</span>
                <input
                  type="text"
                  required
                  value={form.buyer.lastName}
                  onChange={(e) => updateBuyer({ lastName: e.target.value })}
                  className={inputClass}
                  autoComplete="off"
                />
              </label>
              <label className="block sm:col-span-2">
                <span className="text-xs text-slate-500">Title (optional)</span>
                <input
                  type="text"
                  value={form.buyer.title ?? ''}
                  onChange={(e) => updateBuyer({ title: e.target.value })}
                  className={inputClass}
                />
              </label>
            </div>
            {searchError && (
              <p className="mt-3 text-sm text-red-400" role="alert">
                {searchError}
              </p>
            )}
            <div className="mt-3">
              <BuyerSearchSuggestions
                suggestions={suggestions}
                loading={loading}
                hasSearched={hasSearched}
                selectedBuyerId={selectedBuyerId}
                onSelect={handleSelectBuyer}
              />
            </div>
          </section>

          <section className="rounded-lg border border-slate-800 bg-slate-900/45 p-4">
            <h3 className="text-base font-medium text-white">Delivery & payment</h3>
            <div className="mt-3 grid gap-3 sm:grid-cols-2">
              <label className="block">
                <span className="text-xs text-slate-500">City</span>
                <input
                  type="text"
                  required
                  value={form.deliveryAddress.city}
                  onChange={(e) =>
                    setForm((f) => ({
                      ...f,
                      deliveryAddress: { ...f.deliveryAddress, city: e.target.value },
                    }))
                  }
                  className={inputClass}
                />
              </label>
              <label className="block">
                <span className="text-xs text-slate-500">Street</span>
                <input
                  type="text"
                  required
                  value={form.deliveryAddress.street}
                  onChange={(e) =>
                    setForm((f) => ({
                      ...f,
                      deliveryAddress: { ...f.deliveryAddress, street: e.target.value },
                    }))
                  }
                  className={inputClass}
                />
              </label>
              <label className="block">
                <span className="text-xs text-slate-500">Home number (optional)</span>
                <input
                  type="text"
                  value={form.deliveryAddress.homeNumber ?? ''}
                  onChange={(e) =>
                    setForm((f) => ({
                      ...f,
                      deliveryAddress: { ...f.deliveryAddress, homeNumber: e.target.value },
                    }))
                  }
                  className={inputClass}
                />
              </label>
              <label className="block">
                <span className="text-xs text-slate-500">Contact number</span>
                <input
                  type="tel"
                  required
                  value={form.contactNumber}
                  onChange={(e) => setForm((f) => ({ ...f, contactNumber: e.target.value }))}
                  className={inputClass}
                />
              </label>
              <label className="block">
                <span className="text-xs text-slate-500">Payment</span>
                <select
                  value={form.paymentOption}
                  onChange={(e) =>
                    setForm((f) => ({
                      ...f,
                      paymentOption: e.target.value as PaymentOption,
                    }))
                  }
                  className={inputClass}
                >
                  {PAYMENT_OPTIONS.map((opt) => (
                    <option key={opt} value={opt}>
                      {paymentOptionLabel(opt)}
                    </option>
                  ))}
                </select>
              </label>
              <label className="block">
                <span className="text-xs text-slate-500">Currency</span>
                <input
                  type="text"
                  required
                  value={form.currency}
                  onChange={(e) => setForm((f) => ({ ...f, currency: e.target.value }))}
                  className={inputClass}
                />
              </label>
              <label className="block sm:col-span-2">
                <span className="text-xs text-slate-500">Note (optional)</span>
                <textarea
                  rows={3}
                  value={form.note ?? ''}
                  onChange={(e) => setForm((f) => ({ ...f, note: e.target.value }))}
                  className={inputClass}
                />
              </label>
            </div>
          </section>
        </div>

        <aside className="lg:sticky lg:top-6 lg:self-start">
          <OrderMealCart cart={cart} currency={form.currency} onChange={setCart} />

          <button
            type="submit"
            disabled={submitting}
            className="mt-4 w-full rounded-lg bg-emerald-600 px-6 py-3 text-sm font-medium text-white hover:bg-emerald-500 disabled:opacity-50"
          >
            {submitting ? 'Creating…' : 'Create order'}
          </button>
        </aside>
      </div>
    </form>
  )
}

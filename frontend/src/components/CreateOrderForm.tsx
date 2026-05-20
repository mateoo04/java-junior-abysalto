import { useState, type FormEvent } from 'react'
import { ApiError } from '../api/client'
import { createOrder } from '../api/orders'
import { PAYMENT_OPTIONS, paymentOptionLabel } from '../lib/labels'
import type { CreateOrderRequest, PaymentOption } from '../types/order'
import { ErrorBanner } from './ErrorBanner'
import { OrderItemFields } from './OrderItemFields'

const inputClass =
  'mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-100'

const initialForm = (): CreateOrderRequest => ({
  buyer: { firstName: '', lastName: '', title: '' },
  paymentOption: 'CASH',
  deliveryAddress: { city: '', street: '', homeNumber: '' },
  contactNumber: '',
  note: '',
  currency: 'EUR',
  items: [{ name: '', quantity: 1, price: 0 }],
})

type CreateOrderFormProps = {
  onCreated: () => void
}

export function CreateOrderForm({ onCreated }: CreateOrderFormProps) {
  const [form, setForm] = useState<CreateOrderRequest>(initialForm)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  function validate(): string | null {
    if (!form.buyer.firstName.trim() || !form.buyer.lastName.trim()) {
      return 'Buyer first and last name are required'
    }
    if (!form.deliveryAddress.city.trim() || !form.deliveryAddress.street.trim()) {
      return 'Delivery city and street are required'
    }
    if (!form.contactNumber.trim()) return 'Contact number is required'
    if (!form.currency.trim()) return 'Currency is required'
    if (form.items.length === 0) return 'At least one item is required'
    for (const item of form.items) {
      if (!item.name.trim()) return 'Each item needs a name'
      if (!item.quantity || item.quantity <= 0) return 'Each item needs quantity greater than zero'
      if (!item.price || item.price <= 0) return 'Each item needs price greater than zero'
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
      items: form.items.map((item) => ({
        name: item.name.trim(),
        quantity: item.quantity,
        price: item.price,
      })),
    }

    try {
      await createOrder(payload)
      setForm(initialForm())
      onCreated()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to create order')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-8">
      {error && <ErrorBanner message={error} onDismiss={() => setError(null)} />}

      <section className="space-y-4">
        <h2 className="text-lg font-medium text-white">Buyer</h2>
        <div className="grid gap-4 sm:grid-cols-3">
          <label className="block">
            <span className="text-xs text-slate-500">First name</span>
            <input
              type="text"
              required
              value={form.buyer.firstName}
              onChange={(e) =>
                setForm((f) => ({ ...f, buyer: { ...f.buyer, firstName: e.target.value } }))
              }
              className={inputClass}
            />
          </label>
          <label className="block">
            <span className="text-xs text-slate-500">Last name</span>
            <input
              type="text"
              required
              value={form.buyer.lastName}
              onChange={(e) =>
                setForm((f) => ({ ...f, buyer: { ...f.buyer, lastName: e.target.value } }))
              }
              className={inputClass}
            />
          </label>
          <label className="block">
            <span className="text-xs text-slate-500">Title (optional)</span>
            <input
              type="text"
              value={form.buyer.title ?? ''}
              onChange={(e) =>
                setForm((f) => ({ ...f, buyer: { ...f.buyer, title: e.target.value } }))
              }
              className={inputClass}
            />
          </label>
        </div>
      </section>

      <section className="space-y-4">
        <h2 className="text-lg font-medium text-white">Delivery & payment</h2>
        <div className="grid gap-4 sm:grid-cols-2">
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
          <label className="block sm:col-span-2">
            <span className="text-xs text-slate-500">Contact number</span>
            <input
              type="tel"
              required
              value={form.contactNumber}
              onChange={(e) => setForm((f) => ({ ...f, contactNumber: e.target.value }))}
              className={inputClass}
            />
          </label>
          <div className="flex flex-col gap-4 sm:col-span-2 sm:flex-row">
            <label className="block min-w-0 flex-1">
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
            <label className="block min-w-0 flex-1 sm:max-w-[10rem]">
              <span className="text-xs text-slate-500">Currency</span>
              <input
                type="text"
                required
                value={form.currency}
                onChange={(e) => setForm((f) => ({ ...f, currency: e.target.value }))}
                className={inputClass}
              />
            </label>
          </div>
          <label className="block sm:col-span-2">
            <span className="text-xs text-slate-500">Note (optional)</span>
            <textarea
              rows={2}
              value={form.note ?? ''}
              onChange={(e) => setForm((f) => ({ ...f, note: e.target.value }))}
              className={inputClass}
            />
          </label>
        </div>
      </section>

      <OrderItemFields items={form.items} onChange={(items) => setForm((f) => ({ ...f, items }))} />

      <button
        type="submit"
        disabled={submitting}
        className="rounded-lg bg-emerald-600 px-6 py-2.5 text-sm font-medium text-white hover:bg-emerald-500 disabled:opacity-50"
      >
        {submitting ? 'Creating…' : 'Create order'}
      </button>
    </form>
  )
}

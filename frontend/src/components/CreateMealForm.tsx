import { useState, type FormEvent } from 'react'
import { ApiError } from '../api/client'
import { createMenuItem } from '../api/menuItems'

const inputClass =
  'mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm text-slate-100 focus:border-sky-500 focus:outline-none focus:ring-1 focus:ring-sky-500'

type CreateMealFormProps = {
  onCreated: () => void
}

export function CreateMealForm({ onCreated }: CreateMealFormProps) {
  const [name, setName] = useState('')
  const [price, setPrice] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    const trimmedName = name.trim()
    const parsedPrice = Number(price)
    if (!trimmedName) {
      setError('Name is required')
      return
    }
    if (!parsedPrice || parsedPrice <= 0) {
      setError('Price must be greater than zero')
      return
    }

    setSubmitting(true)
    setError(null)
    try {
      await createMenuItem({ name: trimmedName, price: parsedPrice })
      setName('')
      setPrice('')
      onCreated()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Failed to create meal')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4 rounded-lg border border-slate-800 bg-slate-900/40 p-4">
      <h2 className="text-lg font-medium text-white">Add meal</h2>
      {error && <p className="text-sm text-red-400">{error}</p>}
      <div className="grid gap-4 sm:grid-cols-2">
        <label className="block">
          <span className="text-xs text-slate-500">Name</span>
          <input
            type="text"
            required
            value={name}
            onChange={(e) => setName(e.target.value)}
            className={inputClass}
          />
        </label>
        <label className="block">
          <span className="text-xs text-slate-500">Price</span>
          <input
            type="number"
            required
            min={0.01}
            step={0.01}
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            className={inputClass}
          />
        </label>
      </div>
      <button
        type="submit"
        disabled={submitting}
        className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-500 disabled:opacity-50"
      >
        {submitting ? 'Adding…' : 'Add meal'}
      </button>
    </form>
  )
}

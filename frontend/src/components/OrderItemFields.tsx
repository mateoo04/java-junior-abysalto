import type { CreateOrderItemRequest } from '../types/order'

type OrderItemFieldsProps = {
  items: CreateOrderItemRequest[]
  onChange: (items: CreateOrderItemRequest[]) => void
}

const emptyItem = (): CreateOrderItemRequest => ({
  name: '',
  quantity: 1,
  price: 0,
})

export function OrderItemFields({ items, onChange }: OrderItemFieldsProps) {
  function updateItem(index: number, patch: Partial<CreateOrderItemRequest>) {
    onChange(items.map((item, i) => (i === index ? { ...item, ...patch } : item)))
  }

  function addItem() {
    onChange([...items, emptyItem()])
  }

  function removeItem(index: number) {
    if (items.length <= 1) return
    onChange(items.filter((_, i) => i !== index))
  }

  return (
    <fieldset className="space-y-4">
      <legend className="text-sm font-medium text-slate-300">Items</legend>
      {items.map((item, index) => (
        <div
          key={index}
          className="grid gap-3 rounded-lg border border-slate-800 bg-slate-900/40 p-4 sm:grid-cols-2 lg:grid-cols-4"
        >
          <label className="block sm:col-span-2 lg:col-span-2">
            <span className="text-xs text-slate-500">Name</span>
            <input
              type="text"
              required
              value={item.name}
              onChange={(e) => updateItem(index, { name: e.target.value })}
              className="mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm"
            />
          </label>
          <label className="block">
            <span className="text-xs text-slate-500">Qty</span>
            <input
              type="number"
              required
              min={1}
              value={item.quantity || ''}
              onChange={(e) => updateItem(index, { quantity: Number(e.target.value) })}
              className="mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm"
            />
          </label>
          <label className="block">
            <span className="text-xs text-slate-500">Price</span>
            <input
              type="number"
              required
              min={0.01}
              step={0.01}
              value={item.price || ''}
              onChange={(e) => updateItem(index, { price: Number(e.target.value) })}
              className="mt-1 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-sm"
            />
          </label>
          {items.length > 1 && (
            <div className="flex items-end sm:col-span-2 lg:col-span-4">
              <button
                type="button"
                onClick={() => removeItem(index)}
                className="text-sm text-red-400 hover:text-red-300"
              >
                Remove item
              </button>
            </div>
          )}
        </div>
      ))}
      <button
        type="button"
        onClick={addItem}
        className="text-sm text-emerald-400 hover:text-emerald-300"
      >
        + Add item
      </button>
    </fieldset>
  )
}

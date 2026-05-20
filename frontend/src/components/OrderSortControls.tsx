import type { OrderSort } from '../api/orders'

type OrderSortControlsProps = {
  sort: OrderSort
  onSortChange: (sort: OrderSort) => void
}

const options: { value: OrderSort; label: string }[] = [
  { value: 'default', label: 'Newest first' },
  { value: 'totalPrice', label: 'Price: low → high' },
  { value: '-totalPrice', label: 'Price: high → low' },
]

export function OrderSortControls({ sort, onSortChange }: OrderSortControlsProps) {
  return (
    <div className="flex flex-wrap gap-2">
      {options.map((opt) => (
        <button
          key={opt.value}
          type="button"
          onClick={() => onSortChange(opt.value)}
          className={`rounded-lg px-3 py-1.5 text-sm transition-colors ${
            sort === opt.value
              ? 'bg-slate-700 text-white'
              : 'bg-slate-900 text-slate-400 hover:bg-slate-800 hover:text-slate-200'
          }`}
        >
          {opt.label}
        </button>
      ))}
    </div>
  )
}

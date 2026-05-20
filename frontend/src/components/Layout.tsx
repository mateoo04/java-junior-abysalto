import type { ReactNode } from 'react'

export type AppView = 'list' | 'create'

type LayoutProps = {
  view: AppView
  onViewChange: (view: AppView) => void
  children: ReactNode
}

export function Layout({ view, onViewChange, children }: LayoutProps) {
  return (
    <div className="min-h-svh bg-slate-950 text-slate-100">
      <header className="border-b border-slate-800">
        <div className="mx-auto flex max-w-5xl items-center justify-between gap-4 px-4 py-4">
          <h1 className="text-xl font-semibold tracking-tight">Restaurant orders</h1>
          <nav className="flex gap-2">
            <NavButton active={view === 'list'} onClick={() => onViewChange('list')}>
              Orders
            </NavButton>
            <NavButton active={view === 'create'} onClick={() => onViewChange('create')}>
              New order
            </NavButton>
          </nav>
        </div>
      </header>
      <main className="mx-auto max-w-5xl px-4 py-8">{children}</main>
    </div>
  )
}

function NavButton({
  active,
  onClick,
  children,
}: {
  active: boolean
  onClick: () => void
  children: ReactNode
}) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-lg px-3 py-1.5 text-sm font-medium transition-colors ${
        active
          ? 'bg-emerald-600 text-white'
          : 'bg-slate-800 text-slate-300 hover:bg-slate-700 hover:text-white'
      }`}
    >
      {children}
    </button>
  )
}

import type { ReactNode } from 'react'

export type AppView = 'list' | 'create' | 'meals'

type LayoutProps = {
  view: AppView
  onViewChange: (view: AppView) => void
  children: ReactNode
}

export function Layout({ view, onViewChange, children }: LayoutProps) {
  return (
    <div className="min-h-svh bg-slate-950 text-slate-100">
      <header className="border-b border-slate-800">
        <div className="mx-auto flex max-w-4xl flex-col items-center gap-3 px-4 py-4 sm:flex-row sm:justify-between sm:gap-4">
          <button
            type="button"
            onClick={() => onViewChange('list')}
            className="text-center text-xl font-semibold tracking-tight text-white transition-colors hover:text-emerald-300 sm:text-left"
          >
            Restaurant orders
          </button>
          <nav className="flex w-full justify-center gap-2 sm:w-auto sm:justify-start">
            <NavButton active={view === 'list'} onClick={() => onViewChange('list')}>
              Orders
            </NavButton>
            <NavButton active={view === 'meals'} onClick={() => onViewChange('meals')}>
              Menu
            </NavButton>
            <NavButton active={view === 'create'} primary onClick={() => onViewChange('create')}>
              + New order
            </NavButton>
          </nav>
        </div>
      </header>
      <main className="mx-auto max-w-4xl px-4 py-7">{children}</main>
    </div>
  )
}

function NavButton({
  active,
  primary = false,
  onClick,
  children,
}: {
  active: boolean
  primary?: boolean
  onClick: () => void
  children: ReactNode
}) {
  const primaryClass = active
    ? 'bg-emerald-600 text-white'
    : 'bg-emerald-700 text-white hover:bg-emerald-600'
  const secondaryClass = active
    ? 'bg-slate-700 text-white'
    : 'bg-slate-800 text-slate-300 hover:bg-slate-700 hover:text-white'

  return (
    <button
      type="button"
      onClick={onClick}
      className={`rounded-lg px-3 py-1.5 text-sm font-medium transition-colors ${
        primary ? primaryClass : secondaryClass
      }`}
    >
      {children}
    </button>
  )
}

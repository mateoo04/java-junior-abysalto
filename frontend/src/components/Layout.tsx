import type { ReactNode } from 'react'
import { Link, NavLink } from 'react-router-dom'

type LayoutProps = {
  children: ReactNode
}

export function Layout({ children }: LayoutProps) {
  return (
    <div className="min-h-svh bg-slate-950 text-slate-100">
      <header className="border-b border-slate-800">
        <div className="mx-auto flex max-w-4xl flex-col items-center gap-3 px-4 py-4 sm:flex-row sm:justify-between sm:gap-4">
          <Link
            to="/orders"
            className="text-center text-xl font-semibold tracking-tight text-white transition-colors hover:text-emerald-300 sm:text-left"
          >
            Restaurant orders
          </Link>
          <nav className="flex w-full justify-center gap-2 sm:w-auto sm:justify-start">
            <NavButton to="/orders">
              Orders
            </NavButton>
            <NavButton to="/menu">
              Menu
            </NavButton>
            <NavButton to="/orders/new" primary>
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
  to,
  primary = false,
  children,
}: {
  to: string
  primary?: boolean
  children: ReactNode
}) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => {
        const primaryClass = isActive
          ? 'bg-emerald-600 text-white'
          : 'bg-emerald-700 text-white hover:bg-emerald-600'
        const secondaryClass = isActive
          ? 'bg-slate-700 text-white'
          : 'bg-slate-800 text-slate-300 hover:bg-slate-700 hover:text-white'

        return `rounded-lg px-3 py-1.5 text-sm font-medium transition-colors ${
          primary ? primaryClass : secondaryClass
        }`
      }}
    >
      {children}
    </NavLink>
  )
}

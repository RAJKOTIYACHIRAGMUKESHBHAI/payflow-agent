import { NavLink, Outlet } from 'react-router-dom'

const navItems = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/clients', label: 'Clients' },
  { to: '/invoices', label: 'Invoices' },
  { to: '/agent', label: 'Agent' },
  { to: '/settings', label: 'Settings' },
]

function Layout() {
  return (
    <div className="app-shell">
      <aside className="sidebar" aria-label="Primary navigation">
        <div className="brand">PayFlow Agent</div>
        <nav>
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <main className="content">
        <header className="topbar">
          <h1>PayFlow Console</h1>
          <p className="sandbox-pill">PayPal Sandbox — test transactions</p>
        </header>
        <Outlet />
      </main>
    </div>
  )
}

export default Layout

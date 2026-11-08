import { useEffect, useMemo, useState } from 'react'
import { getDashboardSummary } from '../api.js'
import StatusBadge from '../components/StatusBadge.jsx'

const currencies = ['USD', 'INR', 'EUR']

function formatMoney(currency, value) {
  const amount = Number(value || 0)
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency,
    maximumFractionDigits: 2,
  }).format(amount)
}

function Dashboard() {
  const [currency, setCurrency] = useState('USD')
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let mounted = true
    setLoading(true)
    setError('')

    getDashboardSummary(currency)
      .then((data) => {
        if (!mounted) return
        setSummary(data)
      })
      .catch((err) => {
        if (!mounted) return
        setSummary(null)
        setError(err?.message || 'Failed to load dashboard summary.')
      })
      .finally(() => {
        if (mounted) setLoading(false)
      })

    return () => {
      mounted = false
    }
  }, [currency])

  const activeCurrency = useMemo(() => summary?.currency || currency, [currency, summary?.currency])

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h2>Dashboard</h2>
          <p className="muted">Real totals and invoice status counts from backend summary.</p>
        </div>

        <label htmlFor="currency" className="field">
          Currency
          <select
            id="currency"
            value={currency}
            onChange={(event) => setCurrency(event.target.value)}
          >
            {currencies.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>
        </label>
      </div>

      {loading && <p className="state-info">Loading dashboard summary...</p>}
      {!loading && error && <p className="state-error">{error}</p>}

      {!loading && !error && summary && (
        <>
          <div className="card-grid">
            <article className="card">
              <h3>Outstanding</h3>
              <p className="metric">{formatMoney(activeCurrency, summary.totalOutstandingAmount)}</p>
            </article>
            <article className="card">
              <h3>Paid</h3>
              <p className="metric">{formatMoney(activeCurrency, summary.totalPaidAmount)}</p>
            </article>
            <article className="card">
              <h3>Overdue</h3>
              <p className="metric">{formatMoney(activeCurrency, summary.totalOverdueAmount)}</p>
            </article>
          </div>

          <article className="panel">
            <div className="panel-head">
              <h3>Status counts</h3>
              <StatusBadge tone="neutral">Currency: {activeCurrency}</StatusBadge>
            </div>
            <div className="status-grid">
              <div><strong>Total:</strong> {summary.totalInvoices}</div>
              <div><strong>Draft:</strong> {summary.draftCount}</div>
              <div><strong>Sent:</strong> {summary.sentCount}</div>
              <div><strong>Paid:</strong> {summary.paidCount}</div>
              <div><strong>Overdue:</strong> {summary.overdueCount}</div>
            </div>
          </article>

          {Number(summary.totalInvoices || 0) === 0 && (
            <p className="state-info">No invoices found for this currency yet.</p>
          )}
        </>
      )}
    </section>
  )
}

export default Dashboard

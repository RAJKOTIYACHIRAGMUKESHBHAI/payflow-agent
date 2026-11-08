import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { createInvoice, getClients, getInvoices } from '../api.js'

const initialForm = {
  invoiceNumber: '',
  clientId: '',
  amount: '',
  currency: 'USD',
  issueDate: '',
  dueDate: '',
  status: '',
  description: '',
}

function Invoices() {
  const [clients, setClients] = useState([])
  const [invoices, setInvoices] = useState([])
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [statusFilter, setStatusFilter] = useState('')
  const [currencyFilter, setCurrencyFilter] = useState('')
  const [form, setForm] = useState(initialForm)

  const loadData = () => {
    setLoading(true)
    setError('')
    return Promise.all([getInvoices(), getClients()])
      .then(([invoiceData, clientData]) => {
        setInvoices(Array.isArray(invoiceData) ? invoiceData : [])
        setClients(Array.isArray(clientData) ? clientData : [])
      })
      .catch((err) => {
        setError(err?.message || 'Failed to load invoices.')
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadData()
  }, [])

  const statusOptions = useMemo(() => {
    const statuses = new Set()
    invoices.forEach((invoice) => {
      if (invoice?.status) statuses.add(invoice.status)
    })
    return Array.from(statuses).sort()
  }, [invoices])

  const currencyOptions = useMemo(() => {
    const currencies = new Set()
    invoices.forEach((invoice) => {
      if (invoice?.currency) currencies.add(invoice.currency)
    })
    if (form.currency) currencies.add(form.currency)
    return Array.from(currencies).sort()
  }, [invoices, form.currency])

  const filteredInvoices = useMemo(() => {
    return invoices.filter((invoice) => {
      const statusOk = !statusFilter || (invoice.status || '').toUpperCase() === statusFilter.toUpperCase()
      const currencyOk = !currencyFilter || (invoice.currency || '').toUpperCase() === currencyFilter.toUpperCase()
      return statusOk && currencyOk
    })
  }, [currencyFilter, invoices, statusFilter])

  const onSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    setNotice('')

    const payload = {
      invoiceNumber: form.invoiceNumber.trim(),
      clientId: Number(form.clientId),
      amount: form.amount,
      currency: form.currency.trim() || undefined,
      issueDate: form.issueDate || undefined,
      dueDate: form.dueDate,
      status: form.status.trim() || undefined,
      description: form.description.trim() || undefined,
    }

    try {
      await createInvoice(payload)
      setNotice('Invoice created successfully.')
      setForm(initialForm)
      await loadData()
    } catch (err) {
      setError(err?.message || 'Failed to create invoice.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h2>Invoices</h2>
          <p className="muted">Manual invoice creation and list with frontend filters.</p>
        </div>
      </div>

      <article className="panel">
        <h3>Create invoice</h3>
        <form className="form-grid" onSubmit={onSubmit}>
          <label className="field">
            Invoice Number
            <input
              type="text"
              required
              value={form.invoiceNumber}
              onChange={(e) => setForm((prev) => ({ ...prev, invoiceNumber: e.target.value }))}
            />
          </label>

          <label className="field">
            Client
            <select
              required
              value={form.clientId}
              onChange={(e) => setForm((prev) => ({ ...prev, clientId: e.target.value }))}
            >
              <option value="">Select client</option>
              {clients.map((client) => (
                <option key={client.id} value={client.id}>
                  {client.name} ({client.email})
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            Amount
            <input
              type="number"
              min="0"
              step="0.01"
              required
              value={form.amount}
              onChange={(e) => setForm((prev) => ({ ...prev, amount: e.target.value }))}
            />
          </label>

          <label className="field">
            Currency
            <input
              type="text"
              value={form.currency}
              onChange={(e) => setForm((prev) => ({ ...prev, currency: e.target.value.toUpperCase() }))}
            />
          </label>

          <label className="field">
            Issue Date
            <input
              type="date"
              value={form.issueDate}
              onChange={(e) => setForm((prev) => ({ ...prev, issueDate: e.target.value }))}
            />
          </label>

          <label className="field">
            Due Date
            <input
              type="date"
              required
              value={form.dueDate}
              onChange={(e) => setForm((prev) => ({ ...prev, dueDate: e.target.value }))}
            />
          </label>

          <label className="field">
            Status
            <input
              type="text"
              placeholder="DRAFT / SENT / ..."
              value={form.status}
              onChange={(e) => setForm((prev) => ({ ...prev, status: e.target.value.toUpperCase() }))}
            />
          </label>

          <label className="field form-grid-full">
            Description
            <textarea
              rows="3"
              value={form.description}
              onChange={(e) => setForm((prev) => ({ ...prev, description: e.target.value }))}
            />
          </label>

          <div className="form-actions form-grid-full">
            <button type="submit" className="btn primary" disabled={saving}>
              {saving ? 'Saving...' : 'Create invoice'}
            </button>
          </div>
        </form>
      </article>

      <article className="panel">
        <div className="panel-head">
          <h3>Invoice list</h3>
          <div className="filters">
            <label className="field">
              Status
              <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                <option value="">All</option>
                {statusOptions.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </label>

            <label className="field">
              Currency
              <select value={currencyFilter} onChange={(e) => setCurrencyFilter(e.target.value)}>
                <option value="">All</option>
                {currencyOptions.map((currency) => (
                  <option key={currency} value={currency}>
                    {currency}
                  </option>
                ))}
              </select>
            </label>
          </div>
        </div>

        {notice && <p className="state-info">{notice}</p>}
        {error && <p className="state-error">{error}</p>}
        {loading && <p className="state-info">Loading invoices...</p>}

        {!loading && !error && filteredInvoices.length === 0 && (
          <p className="state-info">No invoices found for selected filters.</p>
        )}

        {!loading && filteredInvoices.length > 0 && (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Invoice #</th>
                  <th>Client</th>
                  <th>Amount</th>
                  <th>Status</th>
                  <th>Due Date</th>
                  <th>PayPal Link</th>
                  <th>Details</th>
                </tr>
              </thead>
              <tbody>
                {filteredInvoices.map((invoice) => (
                  <tr key={invoice.id}>
                    <td>{invoice.invoiceNumber}</td>
                    <td>{invoice.clientName || invoice.clientEmail || '-'}</td>
                    <td>
                      {invoice.currency} {invoice.amount}
                    </td>
                    <td>{invoice.status || '-'}</td>
                    <td>{invoice.dueDate || '-'}</td>
                    <td>
                      {invoice.paypalPaymentLink ? (
                        <a href={invoice.paypalPaymentLink} target="_blank" rel="noreferrer">
                          Open
                        </a>
                      ) : (
                        '-'
                      )}
                    </td>
                    <td>
                      <Link className="link-btn" to={`/invoices/${invoice.id}`}>
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </article>
    </section>
  )
}

export default Invoices

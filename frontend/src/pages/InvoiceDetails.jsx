import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getInvoiceById } from '../api.js'

function InvoiceDetails() {
  const { id } = useParams()
  const [invoice, setInvoice] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!id) return
    setLoading(true)
    setError('')
    getInvoiceById(id)
      .then((data) => setInvoice(data))
      .catch((err) => {
        setInvoice(null)
        setError(err?.message || 'Failed to load invoice details.')
      })
      .finally(() => setLoading(false))
  }, [id])

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h2>Invoice Details</h2>
          <p className="muted">Read-only view from backend response.</p>
        </div>
        <Link className="link-btn" to="/invoices">
          Back to invoices
        </Link>
      </div>

      {loading && <p className="state-info">Loading invoice details...</p>}
      {!loading && error && <p className="state-error">{error}</p>}

      {!loading && !error && invoice && (
        <article className="panel">
          <div className="details-grid">
            <div><strong>ID:</strong> {invoice.id}</div>
            <div><strong>Invoice #:</strong> {invoice.invoiceNumber}</div>
            <div><strong>Client:</strong> {invoice.clientName || '-'}</div>
            <div><strong>Client Email:</strong> {invoice.clientEmail || '-'}</div>
            <div>
              <strong>Amount:</strong> {invoice.currency} {invoice.amount}
            </div>
            <div><strong>Status:</strong> {invoice.status || '-'}</div>
            <div><strong>Issue Date:</strong> {invoice.issueDate || '-'}</div>
            <div><strong>Due Date:</strong> {invoice.dueDate || '-'}</div>
            <div className="details-row-full">
              <strong>Description:</strong> {invoice.description || '-'}
            </div>
            <div className="details-row-full">
              <strong>PayPal Order ID:</strong> {invoice.paypalOrderId || '-'}
            </div>
            <div className="details-row-full">
              <strong>PayPal Payment URL:</strong>{' '}
              {invoice.paypalPaymentLink ? (
                <a href={invoice.paypalPaymentLink} target="_blank" rel="noreferrer">
                  {invoice.paypalPaymentLink}
                </a>
              ) : (
                'Not available from backend for this invoice.'
              )}
            </div>
          </div>

          <div className="form-actions">
            <button type="button" className="btn secondary" disabled>
              Reconcile/Refresh not supported by backend API
            </button>
          </div>
        </article>
      )}
    </section>
  )
}

export default InvoiceDetails

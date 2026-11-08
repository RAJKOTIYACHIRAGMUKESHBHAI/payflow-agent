import { useEffect, useMemo, useState } from 'react'
import {
  analyzePortfolio,
  approveDraft,
  draftInvoice,
  sendReminder,
} from '../api.js'
import ConfirmDialog from '../components/ConfirmDialog.jsx'

const initialReminderForm = {
  channel: 'EMAIL',
  recipientEmail: '',
  emailSubject: '',
  emailBody: '',
}

function Agent() {
  const [prompt, setPrompt] = useState('')
  const [draft, setDraft] = useState(null)
  const [draftError, setDraftError] = useState('')
  const [draftLoading, setDraftLoading] = useState(false)
  const [approving, setApproving] = useState(false)
  const [approveConfirmOpen, setApproveConfirmOpen] = useState(false)
  const [createdInvoice, setCreatedInvoice] = useState(null)

  const [analysis, setAnalysis] = useState([])
  const [analysisLoading, setAnalysisLoading] = useState(false)
  const [analysisError, setAnalysisError] = useState('')
  const [selectedInvoiceId, setSelectedInvoiceId] = useState(null)
  const [reminderForm, setReminderForm] = useState(initialReminderForm)
  const [reminderSending, setReminderSending] = useState(false)
  const [reminderResult, setReminderResult] = useState(null)
  const [reminderError, setReminderError] = useState('')
  const [sendConfirmOpen, setSendConfirmOpen] = useState(false)

  const selectedRecovery = useMemo(() => {
    return analysis.find((item) => item.invoiceId === selectedInvoiceId) || null
  }, [analysis, selectedInvoiceId])

  const loadAnalysis = () => {
    setAnalysisLoading(true)
    setAnalysisError('')
    return analyzePortfolio()
      .then((data) => {
        const items = Array.isArray(data) ? data : []
        setAnalysis(items)
        if (!selectedInvoiceId && items.length > 0) {
          setSelectedInvoiceId(items[0].invoiceId)
        }
      })
      .catch((err) => {
        setAnalysis([])
        setAnalysisError(err?.message || 'Failed to load overdue analysis.')
      })
      .finally(() => setAnalysisLoading(false))
  }

  useEffect(() => {
    loadAnalysis()
  }, [])

  useEffect(() => {
    if (!selectedRecovery) return
    setReminderForm({
      channel: 'EMAIL',
      recipientEmail: selectedRecovery.clientEmail || '',
      emailSubject: selectedRecovery.generatedEmailSubject || '',
      emailBody: selectedRecovery.generatedEmailBody || '',
    })
    setReminderResult(null)
    setReminderError('')
  }, [selectedRecovery])

  const onDraftSubmit = async (event) => {
    event.preventDefault()
    setDraftLoading(true)
    setDraftError('')
    setCreatedInvoice(null)
    setDraft(null)

    try {
      const result = await draftInvoice(prompt.trim())
      setDraft(result)
    } catch (err) {
      setDraftError(err?.message || 'Failed to generate draft.')
    } finally {
      setDraftLoading(false)
    }
  }

  const onApproveDraft = async () => {
    if (!draft) return
    setApproveConfirmOpen(false)
    setApproving(true)
    setDraftError('')
    setCreatedInvoice(null)

    try {
      const result = await approveDraft(draft)
      setCreatedInvoice(result)
    } catch (err) {
      setDraftError(err?.message || 'Failed to approve draft.')
    } finally {
      setApproving(false)
    }
  }

  const onSendReminder = async () => {
    if (!selectedRecovery) return
    setSendConfirmOpen(false)
    setReminderSending(true)
    setReminderError('')
    setReminderResult(null)

    try {
      const response = await sendReminder({
        invoiceId: selectedRecovery.invoiceId,
        channel: reminderForm.channel,
        recipientEmail: reminderForm.recipientEmail.trim() || undefined,
        emailSubject: reminderForm.emailSubject.trim() || undefined,
        emailBody: reminderForm.emailBody.trim() || undefined,
      })
      setReminderResult(response)

      if (response?.status && response.status !== 'SUCCESS') {
        setReminderError(`${response.status}: ${response.message || 'Reminder rejected.'}`)
      } else {
        await loadAnalysis()
      }
    } catch (err) {
      setReminderError(err?.message || 'Failed to send reminder.')
    } finally {
      setReminderSending(false)
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h2>AI Drafts and Reminders</h2>
          <p className="muted">
            Uses backend agent endpoints for draft generation, approval, overdue analysis, and reminders.
          </p>
        </div>
      </div>

      <article className="panel">
        <h3>AI Invoice Draft</h3>
        <form className="form-grid" onSubmit={onDraftSubmit}>
          <label className="field form-grid-full">
            Instruction
            <textarea
              rows="3"
              required
              value={prompt}
              onChange={(event) => setPrompt(event.target.value)}
              placeholder="Example: Draft invoice for Acme for $1250 due in 10 days for consulting work."
            />
          </label>
          <div className="form-actions form-grid-full">
            <button type="submit" className="btn primary" disabled={draftLoading}>
              {draftLoading ? 'Generating...' : 'Generate Draft'}
            </button>
          </div>
        </form>

        {draftError && <p className="state-error">{draftError}</p>}

        {draft && (
          <div className="panel-section">
            <h4>Structured draft</h4>
            <div className="details-grid">
              <div><strong>Client Name:</strong> {draft.clientName || '-'}</div>
              <div><strong>Client Email:</strong> {draft.clientEmail || '-'}</div>
              <div><strong>Amount:</strong> {draft.currency} {draft.amount}</div>
              <div><strong>Due Date:</strong> {draft.dueDate || '-'}</div>
              <div className="details-row-full"><strong>Description:</strong> {draft.description || '-'}</div>
              <div className="details-row-full">
                <strong>Ready for approval:</strong> {draft.readyForApproval ? 'Yes' : 'No'}
              </div>
              {Array.isArray(draft.missingOrAmbiguousFields) && draft.missingOrAmbiguousFields.length > 0 && (
                <div className="details-row-full">
                  <strong>Missing/Ambiguous:</strong>
                  <ul className="simple-list">
                    {draft.missingOrAmbiguousFields.map((item) => (
                      <li key={item}>{item}</li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="btn primary"
                disabled={!draft.readyForApproval || approving}
                onClick={() => setApproveConfirmOpen(true)}
              >
                {approving ? 'Approving...' : 'Approve Draft'}
              </button>
            </div>
          </div>
        )}

        {createdInvoice && (
          <div className="state-info">
            Invoice created: <strong>{createdInvoice.invoiceNumber}</strong>.{' '}
            {createdInvoice.paypalPaymentLink ? (
              <>
                Payment URL:{' '}
                <a href={createdInvoice.paypalPaymentLink} target="_blank" rel="noreferrer">
                  {createdInvoice.paypalPaymentLink}
                </a>
              </>
            ) : (
              'No backend payment URL available for this invoice.'
            )}
          </div>
        )}
      </article>

      <article className="panel">
        <div className="panel-head">
          <h3>Overdue Analysis and Reminder</h3>
          <button type="button" className="btn secondary" onClick={loadAnalysis} disabled={analysisLoading}>
            {analysisLoading ? 'Refreshing...' : 'Refresh Analysis'}
          </button>
        </div>

        {analysisError && <p className="state-error">{analysisError}</p>}
        {analysisLoading && <p className="state-info">Loading overdue analysis...</p>}
        {!analysisLoading && !analysisError && analysis.length === 0 && (
          <p className="state-info">No recovery items currently returned by backend.</p>
        )}

        {!analysisLoading && analysis.length > 0 && (
          <>
            <label className="field">
              Select Invoice
              <select
                value={selectedInvoiceId || ''}
                onChange={(event) => setSelectedInvoiceId(Number(event.target.value))}
              >
                {analysis.map((item) => (
                  <option key={item.invoiceId} value={item.invoiceId}>
                    {item.invoiceNumber} — {item.clientName} — {item.currency} {item.amount}
                  </option>
                ))}
              </select>
            </label>

            {selectedRecovery && (
              <div className="panel-section">
                <div className="details-grid">
                  <div><strong>Urgency:</strong> {selectedRecovery.urgencyLevel}</div>
                  <div><strong>Days Overdue:</strong> {selectedRecovery.daysOverdue}</div>
                  <div><strong>Client:</strong> {selectedRecovery.clientName}</div>
                  <div><strong>Email:</strong> {selectedRecovery.clientEmail || '-'}</div>
                  <div className="details-row-full">
                    <strong>Recommended Strategy:</strong> {selectedRecovery.recommendedStrategy}
                  </div>
                  <div className="details-row-full">
                    <strong>PayPal URL:</strong>{' '}
                    {selectedRecovery.paypalPaymentLink ? (
                      <a href={selectedRecovery.paypalPaymentLink} target="_blank" rel="noreferrer">
                        {selectedRecovery.paypalPaymentLink}
                      </a>
                    ) : (
                      'Not available'
                    )}
                  </div>
                </div>

                <form
                  className="form-grid"
                  onSubmit={(event) => {
                    event.preventDefault()
                    setSendConfirmOpen(true)
                  }}
                >
                  <label className="field">
                    Channel
                    <input
                      type="text"
                      required
                      value={reminderForm.channel}
                      onChange={(event) =>
                        setReminderForm((prev) => ({ ...prev, channel: event.target.value.toUpperCase() }))
                      }
                    />
                  </label>

                  <label className="field">
                    Recipient Email
                    <input
                      type="email"
                      value={reminderForm.recipientEmail}
                      onChange={(event) =>
                        setReminderForm((prev) => ({ ...prev, recipientEmail: event.target.value }))
                      }
                    />
                  </label>

                  <label className="field form-grid-full">
                    Reminder Subject
                    <input
                      type="text"
                      value={reminderForm.emailSubject}
                      onChange={(event) =>
                        setReminderForm((prev) => ({ ...prev, emailSubject: event.target.value }))
                      }
                    />
                  </label>

                  <label className="field form-grid-full">
                    Reminder Preview
                    <textarea
                      rows="6"
                      value={reminderForm.emailBody}
                      onChange={(event) =>
                        setReminderForm((prev) => ({ ...prev, emailBody: event.target.value }))
                      }
                    />
                  </label>

                  <div className="form-actions form-grid-full">
                    <button type="submit" className="btn primary" disabled={reminderSending}>
                      {reminderSending ? 'Sending...' : 'Send Reminder'}
                    </button>
                  </div>
                </form>
              </div>
            )}
          </>
        )}

        {reminderError && <p className="state-error">{reminderError}</p>}
        {reminderResult && (
          <p className="state-info">
            Reminder status: <strong>{reminderResult.status}</strong> — {reminderResult.message}. Count:{' '}
            {reminderResult.currentReminderCount}.{' '}
            {reminderResult.status === 'SUCCESS'
              ? 'Backend reports sent.'
              : 'Backend rejected this reminder request.'}
          </p>
        )}
        <p className="muted">
          Queue/delivery tracking is not exposed by current API; only immediate reminder response status is shown.
        </p>
      </article>

      <ConfirmDialog
        open={approveConfirmOpen}
        title="Approve draft invoice?"
        message="This will create a real invoice using backend approval."
        confirmText="Approve"
        onConfirm={onApproveDraft}
        onCancel={() => setApproveConfirmOpen(false)}
      />

      <ConfirmDialog
        open={sendConfirmOpen}
        title="Send reminder now?"
        message="This action will trigger backend reminder send for the selected invoice."
        confirmText="Send Reminder"
        onConfirm={onSendReminder}
        onCancel={() => setSendConfirmOpen(false)}
      />
    </section>
  )
}

export default Agent

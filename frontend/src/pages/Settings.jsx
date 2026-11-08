import { useEffect, useState } from 'react'
import { getAuditLogs, getMerchantSettings, updateMerchantSettings } from '../api.js'

const initialForm = {
  businessName: '',
  email: '',
  defaultCurrency: '',
  timezone: '',
  maxReminders: '',
  cooldownDays: '',
}

function Settings() {
  const [form, setForm] = useState(initialForm)
  const [loadingSettings, setLoadingSettings] = useState(false)
  const [savingSettings, setSavingSettings] = useState(false)
  const [settingsError, setSettingsError] = useState('')
  const [settingsNotice, setSettingsNotice] = useState('')

  const [auditLogs, setAuditLogs] = useState([])
  const [loadingAudit, setLoadingAudit] = useState(false)
  const [auditError, setAuditError] = useState('')

  const loadSettings = () => {
    setLoadingSettings(true)
    setSettingsError('')
    return getMerchantSettings()
      .then((data) => {
        setForm({
          businessName: data?.businessName || '',
          email: data?.email || '',
          defaultCurrency: data?.defaultCurrency || '',
          timezone: data?.timezone || '',
          maxReminders: data?.maxReminders ?? '',
          cooldownDays: data?.cooldownDays ?? '',
        })
      })
      .catch((err) => {
        setSettingsError(err?.message || 'Failed to load merchant settings.')
      })
      .finally(() => setLoadingSettings(false))
  }

  const loadAuditLogs = () => {
    setLoadingAudit(true)
    setAuditError('')
    return getAuditLogs()
      .then((data) => {
        setAuditLogs(Array.isArray(data) ? data : [])
      })
      .catch((err) => {
        setAuditError(err?.message || 'Failed to load audit logs.')
      })
      .finally(() => setLoadingAudit(false))
  }

  useEffect(() => {
    loadSettings()
    loadAuditLogs()
  }, [])

  const onSave = async (event) => {
    event.preventDefault()
    setSavingSettings(true)
    setSettingsError('')
    setSettingsNotice('')

    try {
      const payload = {
        businessName: form.businessName.trim(),
        email: form.email.trim(),
        defaultCurrency: form.defaultCurrency.trim().toUpperCase(),
        timezone: form.timezone.trim(),
        maxReminders: Number(form.maxReminders),
        cooldownDays: Number(form.cooldownDays),
      }
      const updated = await updateMerchantSettings(payload)
      setForm({
        businessName: updated?.businessName || '',
        email: updated?.email || '',
        defaultCurrency: updated?.defaultCurrency || '',
        timezone: updated?.timezone || '',
        maxReminders: updated?.maxReminders ?? '',
        cooldownDays: updated?.cooldownDays ?? '',
      })
      setSettingsNotice('Settings updated successfully.')
    } catch (err) {
      setSettingsError(err?.message || 'Failed to update settings.')
    } finally {
      setSavingSettings(false)
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h2>Settings and Audit</h2>
          <p className="muted">Merchant profile and reminder policy from backend.</p>
        </div>
      </div>

      <article className="panel">
        <div className="panel-head">
          <h3>Merchant Settings</h3>
          <button type="button" className="btn secondary" onClick={loadSettings} disabled={loadingSettings}>
            {loadingSettings ? 'Refreshing...' : 'Refresh'}
          </button>
        </div>

        {settingsNotice && <p className="state-info">{settingsNotice}</p>}
        {settingsError && <p className="state-error">{settingsError}</p>}

        <form className="form-grid" onSubmit={onSave}>
          <label className="field">
            Business Name
            <input
              type="text"
              required
              value={form.businessName}
              onChange={(e) => setForm((prev) => ({ ...prev, businessName: e.target.value }))}
            />
          </label>

          <label className="field">
            Merchant Email
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
            />
          </label>

          <label className="field">
            Default Currency
            <input
              type="text"
              required
              value={form.defaultCurrency}
              onChange={(e) => setForm((prev) => ({ ...prev, defaultCurrency: e.target.value.toUpperCase() }))}
            />
          </label>

          <label className="field">
            Timezone
            <input
              type="text"
              required
              value={form.timezone}
              onChange={(e) => setForm((prev) => ({ ...prev, timezone: e.target.value }))}
            />
          </label>

          <label className="field">
            Max Reminders
            <input
              type="number"
              min="0"
              required
              value={form.maxReminders}
              onChange={(e) => setForm((prev) => ({ ...prev, maxReminders: e.target.value }))}
            />
          </label>

          <label className="field">
            Cooldown Days
            <input
              type="number"
              min="0"
              required
              value={form.cooldownDays}
              onChange={(e) => setForm((prev) => ({ ...prev, cooldownDays: e.target.value }))}
            />
          </label>

          <div className="form-actions form-grid-full">
            <button type="submit" className="btn primary" disabled={savingSettings}>
              {savingSettings ? 'Saving...' : 'Save Settings'}
            </button>
          </div>
        </form>
      </article>

      <article className="panel">
        <div className="panel-head">
          <h3>Audit Logs (Read-only)</h3>
          <button type="button" className="btn secondary" onClick={loadAuditLogs} disabled={loadingAudit}>
            {loadingAudit ? 'Refreshing...' : 'Refresh'}
          </button>
        </div>

        {auditError && <p className="state-error">{auditError}</p>}
        {loadingAudit && <p className="state-info">Loading audit logs...</p>}
        {!loadingAudit && !auditError && auditLogs.length === 0 && (
          <p className="state-info">No audit logs returned by backend.</p>
        )}

        {!loadingAudit && auditLogs.length > 0 && (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Timestamp</th>
                  <th>Actor</th>
                  <th>Action</th>
                  <th>Target</th>
                  <th>Outcome</th>
                  <th>Details</th>
                </tr>
              </thead>
              <tbody>
                {auditLogs.map((log) => (
                  <tr key={log.id}>
                    <td>{log.timestamp || '-'}</td>
                    <td>{log.actor || '-'}</td>
                    <td>{log.action || '-'}</td>
                    <td>
                      {log.targetType || '-'}
                      {log.targetId !== null && log.targetId !== undefined ? ` #${log.targetId}` : ''}
                    </td>
                    <td>{log.outcome || '-'}</td>
                    <td>{log.details || '-'}</td>
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

export default Settings

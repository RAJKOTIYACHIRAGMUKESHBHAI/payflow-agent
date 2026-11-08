import { useEffect, useMemo, useState } from 'react'
import { createClient, getClients, updateClient } from '../api.js'

const initialForm = {
  name: '',
  email: '',
  phone: '',
  address: '',
}

function normalizeClientForm(values) {
  return {
    name: values.name.trim(),
    email: values.email.trim(),
    phone: values.phone.trim(),
    address: values.address.trim(),
  }
}

function Clients() {
  const [clients, setClients] = useState([])
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [search, setSearch] = useState('')
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(initialForm)

  const loadClients = () => {
    setLoading(true)
    setError('')
    return getClients()
      .then((data) => {
        setClients(Array.isArray(data) ? data : [])
      })
      .catch((err) => {
        setError(err?.message || 'Failed to load clients.')
      })
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadClients()
  }, [])

  const filteredClients = useMemo(() => {
    const term = search.trim().toLowerCase()
    if (!term) return clients

    return clients.filter((client) => {
      const haystack = `${client.name || ''} ${client.email || ''} ${client.phone || ''}`.toLowerCase()
      return haystack.includes(term)
    })
  }, [clients, search])

  const onEdit = (client) => {
    setEditingId(client.id)
    setNotice('')
    setError('')
    setForm({
      name: client.name || '',
      email: client.email || '',
      phone: client.phone || '',
      address: client.address || '',
    })
  }

  const onCancelEdit = () => {
    setEditingId(null)
    setForm(initialForm)
  }

  const onSubmit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setError('')
    setNotice('')

    const payload = normalizeClientForm(form)
    const existingByEmail = clients.find(
      (c) => c.email?.toLowerCase() === payload.email.toLowerCase(),
    )

    try {
      const result = editingId
        ? await updateClient(editingId, payload)
        : await createClient(payload)

      if (editingId) {
        setNotice('Client updated successfully.')
      } else if (existingByEmail && existingByEmail.id === result?.id) {
        setNotice('Duplicate email: backend returned the existing client record.')
      } else {
        setNotice('Client created successfully.')
      }

      setEditingId(null)
      setForm(initialForm)
      await loadClients()
    } catch (err) {
      setError(err?.message || 'Failed to save client.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h2>Clients</h2>
          <p className="muted">Create and edit clients using backend DTO fields.</p>
        </div>
      </div>

      <article className="panel">
        <h3>{editingId ? 'Edit client' : 'Create client'}</h3>
        <form className="form-grid" onSubmit={onSubmit}>
          <label className="field">
            Name
            <input
              type="text"
              required
              value={form.name}
              onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))}
            />
          </label>

          <label className="field">
            Email
            <input
              type="email"
              required
              value={form.email}
              onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))}
            />
          </label>

          <label className="field">
            Phone
            <input
              type="text"
              value={form.phone}
              onChange={(e) => setForm((prev) => ({ ...prev, phone: e.target.value }))}
            />
          </label>

          <label className="field form-grid-full">
            Address
            <textarea
              rows="3"
              value={form.address}
              onChange={(e) => setForm((prev) => ({ ...prev, address: e.target.value }))}
            />
          </label>

          <div className="form-actions form-grid-full">
            {editingId && (
              <button type="button" className="btn secondary" onClick={onCancelEdit}>
                Cancel edit
              </button>
            )}
            <button type="submit" className="btn primary" disabled={saving}>
              {saving ? 'Saving...' : editingId ? 'Update client' : 'Create client'}
            </button>
          </div>
        </form>
      </article>

      <article className="panel">
        <div className="panel-head">
          <h3>Client list</h3>
          <label className="field">
            Search
            <input
              type="search"
              placeholder="Name, email, phone"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </label>
        </div>

        {notice && <p className="state-info">{notice}</p>}
        {error && <p className="state-error">{error}</p>}
        {loading && <p className="state-info">Loading clients...</p>}

        {!loading && !error && filteredClients.length === 0 && (
          <p className="state-info">No matching clients found.</p>
        )}

        {!loading && filteredClients.length > 0 && (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Phone</th>
                  <th>Address</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredClients.map((client) => (
                  <tr key={client.id}>
                    <td>{client.name || '-'}</td>
                    <td>{client.email || '-'}</td>
                    <td>{client.phone || '-'}</td>
                    <td>{client.address || '-'}</td>
                    <td>
                      <button type="button" className="btn secondary" onClick={() => onEdit(client)}>
                        Edit
                      </button>
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

export default Clients

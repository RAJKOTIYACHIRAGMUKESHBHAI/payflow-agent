const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '')

async function toPayload(response) {
  if (response.status === 204) return null

  const contentType = response.headers.get('content-type') || ''
  if (contentType.includes('application/json')) {
    return response.json()
  }

  const text = await response.text()
  return text ? { message: text } : null
}

function toApiError(status, payload) {
  const fallback = `Request failed with status ${status}`

  if (!payload) {
    return { status, message: fallback, details: null }
  }

  if (typeof payload === 'string') {
    return { status, message: payload, details: null }
  }

  const message =
    payload.message ||
    payload.detail ||
    payload.error ||
    payload.title ||
    fallback

  return { status, message, details: payload }
}

export async function apiFetch(path, options = {}) {
  const { method = 'GET', body, headers = {}, ...rest } = options

  const requestHeaders = {
    Accept: 'application/json',
    ...headers,
  }

  let requestBody
  if (body !== undefined && body !== null) {
    requestHeaders['Content-Type'] = 'application/json'
    requestBody = JSON.stringify(body)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: requestHeaders,
    body: requestBody,
    ...rest,
  })

  const payload = await toPayload(response)

  if (!response.ok) {
    throw toApiError(response.status, payload)
  }

  return payload
}

export function getDashboardSummary(currency) {
  const params = new URLSearchParams()
  if (currency) params.set('currency', currency)
  const query = params.toString()
  return apiFetch(`/api/dashboard/summary${query ? `?${query}` : ''}`)
}

export function getClients() {
  return apiFetch('/api/clients')
}

export function createClient(payload) {
  return apiFetch('/api/clients', {
    method: 'POST',
    body: payload,
  })
}

export function updateClient(id, payload) {
  return apiFetch(`/api/clients/${id}`, {
    method: 'PUT',
    body: payload,
  })
}

export function getInvoices() {
  return apiFetch('/api/invoices')
}

export function getInvoiceById(id) {
  return apiFetch(`/api/invoices/${id}`)
}

export function createInvoice(payload) {
  return apiFetch('/api/invoices', {
    method: 'POST',
    body: payload,
  })
}

export function draftInvoice(prompt) {
  return apiFetch('/api/agent/draft-invoice', {
    method: 'POST',
    body: { prompt },
  })
}

export function approveDraft(draftPayload) {
  return apiFetch('/api/agent/approve-draft', {
    method: 'POST',
    body: draftPayload,
  })
}

export function analyzePortfolio() {
  return apiFetch('/api/agent/analyze')
}

export function sendReminder(payload) {
  return apiFetch('/api/agent/send-reminder', {
    method: 'POST',
    body: payload,
  })
}

export function getMerchantSettings() {
  return apiFetch('/api/merchant/settings')
}

export function updateMerchantSettings(payload) {
  return apiFetch('/api/merchant/settings', {
    method: 'PUT',
    body: payload,
  })
}

export function getAuditLogs() {
  return apiFetch('/api/audit-logs')
}

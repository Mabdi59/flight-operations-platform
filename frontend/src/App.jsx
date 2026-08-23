import { useEffect, useMemo, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api'

const emptyDashboard = {
  totalActiveFlights: 0,
  delayedFlights: 0,
  cancelledFlights: 0,
  availableAircraft: 0,
  currentFlights: [],
  aircraftAssignments: [],
  operationalAlerts: [],
}

const statCards = [
  { key: 'totalActiveFlights', label: 'Active flights' },
  { key: 'delayedFlights', label: 'Delayed flights' },
  { key: 'cancelledFlights', label: 'Cancelled flights' },
  { key: 'availableAircraft', label: 'Available aircraft' },
]

function App() {
  const [dashboard, setDashboard] = useState(emptyDashboard)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const controller = new AbortController()

    async function loadDashboard() {
      try {
        const response = await fetch(`${API_BASE_URL}/dashboard`, { signal: controller.signal })
        if (!response.ok) {
          throw new Error(`Dashboard request failed with status ${response.status}`)
        }

        const payload = await response.json()
        setDashboard(payload)
      } catch (requestError) {
        if (requestError.name !== 'AbortError') {
          setError(requestError.message)
        }
      } finally {
        setLoading(false)
      }
    }

    loadDashboard()
    return () => controller.abort()
  }, [])

  const flights = useMemo(() => dashboard.currentFlights ?? [], [dashboard.currentFlights])

  return (
    <main className="app-shell">
      <section className="hero-panel">
        <div>
          <p className="eyebrow">Flight Operations Management Platform</p>
          <h1>Airline operations control dashboard</h1>
          <p className="hero-copy">
            Monitor flight health, aircraft allocation, and operational alerts from a single control view.
          </p>
        </div>
        <div className="hero-meta">
          <span>Backend API</span>
          <strong>{API_BASE_URL}</strong>
        </div>
      </section>

      {error ? <p className="feedback error">{error}</p> : null}
      {loading ? <p className="feedback">Loading live operations snapshot…</p> : null}

      <section className="stats-grid">
        {statCards.map((card) => (
          <article key={card.key} className="stat-card">
            <span>{card.label}</span>
            <strong>{dashboard[card.key]}</strong>
          </article>
        ))}
      </section>

      <section className="panel-grid">
        <article className="panel panel-wide">
          <div className="panel-header">
            <h2>Current flight list</h2>
            <span>{flights.length} tracked routes</span>
          </div>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Flight</th>
                  <th>Route</th>
                  <th>Status</th>
                  <th>Scheduled departure</th>
                  <th>Delay</th>
                </tr>
              </thead>
              <tbody>
                {flights.map((flight) => (
                  <tr key={flight.id}>
                    <td>{flight.flightNumber}</td>
                    <td>
                      {flight.origin} → {flight.destination}
                    </td>
                    <td>
                      <span
                        className={`status-pill status-${flight.status?.toLowerCase() ?? ''}`}
                      >
                        {flight.status ?? 'UNKNOWN'}
                      </span>
                    </td>
                    <td>{formatDate(flight.scheduledDeparture)}</td>
                    <td>{flight.delayMinutes ? `${flight.delayMinutes} min` : 'On time'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Aircraft assignments</h2>
          </div>
          <ul className="stack-list">
            {dashboard.aircraftAssignments.map((assignment) => (
              <li key={assignment.assignmentId}>
                <strong>{assignment.flightNumber}</strong>
                <span>
                  {assignment.aircraftTailNumber} · {assignment.aircraftModel}
                </span>
              </li>
            ))}
          </ul>
        </article>

        <article className="panel">
          <div className="panel-header">
            <h2>Operational alerts</h2>
          </div>
          <ul className="stack-list alerts">
            {dashboard.operationalAlerts.map((alert) => (
              <li key={alert.id}>
                <strong>{alert.eventType}</strong>
                <span>{alert.message}</span>
                <time>{formatDate(alert.createdAt)}</time>
              </li>
            ))}
          </ul>
        </article>
      </section>
    </main>
  )
}

function formatDate(value) {
  if (!value) {
    return 'TBD'
  }

  const parsed = new Date(value)
  if (Number.isNaN(parsed.getTime())) {
    return 'TBD'
  }

  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(parsed)
}

export default App

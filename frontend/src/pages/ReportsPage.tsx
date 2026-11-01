import { useMemo, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { useAdherenceReport, useCoverageReport, useHoursByTrack } from "../api/hooks/useReports";

function isoDaysAgo(days: number): string {
  const d = new Date();
  d.setDate(d.getDate() - days);
  return d.toISOString().slice(0, 10);
}

export function ReportsPage() {
  const [rangeDays, setRangeDays] = useState(30);
  const from = useMemo(() => isoDaysAgo(rangeDays), [rangeDays]);
  const to = useMemo(() => isoDaysAgo(0), []);

  const { data: adherence } = useAdherenceReport(from, to);
  const { data: hoursByTrack } = useHoursByTrack(from, to);
  const { data: coverage } = useCoverageReport();

  return (
    <div>
      <h2>Reports</h2>

      <div className="row" style={{ marginBottom: 16 }}>
        <span className="muted">Range:</span>
        {[7, 30, 90].map((d) => (
          <button
            key={d}
            className={`btn ${rangeDays === d ? "" : "secondary"}`}
            onClick={() => setRangeDays(d)}
          >
            {d}d
          </button>
        ))}
      </div>

      <div className="grid">
        <div className="card">
          <h3>Adherence</h3>
          {adherence ? (
            <>
              <p>
                <strong>{adherence.adherencePct}%</strong> of logged days met the goal
              </p>
              <p className="muted">
                Current streak: {adherence.currentStreak} days &middot; Longest: {adherence.longestStreak} days
              </p>
              <p className="muted">
                {adherence.adherentDays} of {adherence.totalLoggedDays} logged days adherent
              </p>
            </>
          ) : (
            <p className="muted">Loading...</p>
          )}
        </div>

        <div className="card" style={{ gridColumn: "1 / -1" }}>
          <h3>Hours by track (planned vs actual)</h3>
          <div style={{ width: "100%", height: 280 }}>
            <ResponsiveContainer>
              <BarChart data={hoursByTrack ?? []}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                <XAxis dataKey="trackName" stroke="var(--color-text-muted)" />
                <YAxis stroke="var(--color-text-muted)" />
                <Tooltip />
                <Legend />
                <Bar dataKey="plannedHours" name="Planned" fill="var(--chart-2)" />
                <Bar dataKey="actualHours" name="Actual" fill="var(--chart-1)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="card" style={{ gridColumn: "1 / -1" }}>
          <h3>Topic coverage by track</h3>
          <div style={{ width: "100%", height: 280 }}>
            <ResponsiveContainer>
              <BarChart data={coverage ?? []}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                <XAxis dataKey="trackName" stroke="var(--color-text-muted)" />
                <YAxis stroke="var(--color-text-muted)" unit="%" />
                <Tooltip />
                <Bar dataKey="coveragePct" name="Coverage %" fill="var(--chart-4)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}

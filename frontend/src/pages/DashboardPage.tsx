import { useState } from "react";
import { useToday, usePatchDailyLog } from "../api/hooks/useDashboard";

export function DashboardPage() {
  const { data, isLoading } = useToday();
  const patchLog = usePatchDailyLog();
  const [drafts, setDrafts] = useState<Record<number, string>>({});

  if (isLoading) {
    return <p className="muted">Loading today's goals...</p>;
  }
  if (!data) {
    return <p className="muted">Could not load today's goals.</p>;
  }

  const submitHours = (trackId: number) => {
    const raw = drafts[trackId];
    if (raw === undefined) return;
    const actualHours = Number(raw);
    if (Number.isNaN(actualHours) || actualHours < 0) return;
    patchLog.mutate({ trackId, date: data.date, actualHours });
  };

  return (
    <div>
      <h2>Today - {data.date} ({data.dayType ?? "no tracks configured"})</h2>
      <p className="muted">
        {data.totalActualHours}h logged of {data.totalPlannedHours}h planned -{" "}
        {data.topicsCompletedToday} topic(s) completed, {data.topicsTouchedToday} touched today.
      </p>

      <div className="grid">
        {data.trackLogs.map((log) => (
          <div className="card" key={log.trackId}>
            <h3>{log.trackName}</h3>
            <p className="muted">
              Planned {log.plannedHours}h &middot; Status{" "}
              <span className={`badge ${log.status.toLowerCase()}`}>{log.status}</span>
            </p>
            <div className="row">
              <input
                type="number"
                step="0.25"
                min="0"
                style={{ width: 90 }}
                value={drafts[log.trackId] ?? log.actualHours}
                onChange={(e) => setDrafts((d) => ({ ...d, [log.trackId]: e.target.value }))}
              />
              <span className="muted">hrs actual</span>
              <button className="btn" onClick={() => submitHours(log.trackId)} disabled={patchLog.isPending}>
                Save
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

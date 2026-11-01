import { useState } from "react";
import { useTracks } from "../api/hooks/useTracks";
import {
  useDayHourConfig,
  useTrackAllocations,
  useUpdateDayHourConfig,
  useUpsertAllocation,
} from "../api/hooks/useConfig";

const DAY_TYPES = ["WEEKDAY", "WEEKEND", "HOLIDAY"];

export function HourConfigPage() {
  const { data: dayHours } = useDayHourConfig();
  const { data: allocations } = useTrackAllocations();
  const { data: tracks } = useTracks();
  const updateDayHours = useUpdateDayHourConfig();
  const upsertAllocation = useUpsertAllocation();
  const [edits, setEdits] = useState<Record<string, string>>({});

  const dayHourFor = (dayType: string) => dayHours?.find((d) => d.dayType === dayType)?.totalHours ?? 0;
  const allocationFor = (trackId: number, dayType: string) =>
    allocations?.allocations.find((a) => a.trackId === trackId && a.dayType === dayType)?.allocatedHours ?? 0;

  return (
    <div>
      <h2>Daily Hours & Track Allocation</h2>

      {allocations?.warnings.map((w) => (
        <div className="warning-banner" key={w.dayType}>
          {w.dayType}: allocations sum to {w.allocatedSum}h but the budget is {w.configuredTotal}h
          (off by {w.delta}h). This won't block saving - just adjust the splits below if you want them to match.
        </div>
      ))}

      <div className="card">
        <h3>Daily hour budget</h3>
        <table>
          <thead>
            <tr>
              <th>Day type</th>
              <th>Total hours</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {DAY_TYPES.map((dayType) => {
              const key = `budget-${dayType}`;
              return (
                <tr key={dayType}>
                  <td>{dayType}</td>
                  <td>
                    <input
                      type="number"
                      step="0.25"
                      min="0"
                      max="24"
                      style={{ width: 90 }}
                      value={edits[key] ?? dayHourFor(dayType)}
                      onChange={(e) => setEdits((s) => ({ ...s, [key]: e.target.value }))}
                    />
                  </td>
                  <td>
                    <button
                      className="btn secondary"
                      onClick={() =>
                        updateDayHours.mutate({ dayType, totalHours: Number(edits[key] ?? dayHourFor(dayType)) })
                      }
                    >
                      Save
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <div className="card">
        <h3>Per-track allocation</h3>
        <table>
          <thead>
            <tr>
              <th>Track</th>
              {DAY_TYPES.map((d) => (
                <th key={d}>{d}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {tracks?.map((track) => (
              <tr key={track.id}>
                <td>{track.name}</td>
                {DAY_TYPES.map((dayType) => {
                  const key = `alloc-${track.id}-${dayType}`;
                  return (
                    <td key={dayType}>
                      <input
                        type="number"
                        step="0.25"
                        min="0"
                        style={{ width: 80 }}
                        value={edits[key] ?? allocationFor(track.id, dayType)}
                        onChange={(e) => setEdits((s) => ({ ...s, [key]: e.target.value }))}
                        onBlur={() =>
                          upsertAllocation.mutate({
                            trackId: track.id,
                            dayType,
                            allocatedHours: Number(edits[key] ?? allocationFor(track.id, dayType)),
                          })
                        }
                      />
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
        <p className="muted">Changes save automatically when you leave a field.</p>
      </div>
    </div>
  );
}

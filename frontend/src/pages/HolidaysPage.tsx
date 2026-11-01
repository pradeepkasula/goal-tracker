import { useState, type FormEvent } from "react";
import { useAddHoliday, useDeleteHoliday, useHolidays } from "../api/hooks/useHolidays";

export function HolidaysPage() {
  const { data: holidays, isLoading } = useHolidays();
  const addHoliday = useAddHoliday();
  const deleteHoliday = useDeleteHoliday();
  const [date, setDate] = useState("");
  const [label, setLabel] = useState("");

  const handleAdd = async (e: FormEvent) => {
    e.preventDefault();
    if (!date) return;
    await addHoliday.mutateAsync({ date, label: label || undefined });
    setDate("");
    setLabel("");
  };

  return (
    <div>
      <h2>Holidays</h2>
      <p className="muted">
        Any date listed here counts as a HOLIDAY day type when computing that day's goal, instead
        of a weekday or weekend.
      </p>

      <form className="card row" onSubmit={handleAdd}>
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        <input placeholder="Label (optional)" value={label} onChange={(e) => setLabel(e.target.value)} />
        <button className="btn" type="submit" disabled={addHoliday.isPending}>
          Add holiday
        </button>
      </form>

      {isLoading && <p className="muted">Loading holidays...</p>}

      <div className="card">
        <table>
          <thead>
            <tr>
              <th>Date</th>
              <th>Label</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {holidays?.map((h) => (
              <tr key={h.id}>
                <td>{h.date}</td>
                <td>{h.label ?? "-"}</td>
                <td>
                  <button className="btn secondary" onClick={() => deleteHoliday.mutate(h.id)}>
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

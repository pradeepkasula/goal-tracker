import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { useCreateTrack, useDeleteTrack, useTracks } from "../api/hooks/useTracks";

export function TracksListPage() {
  const { data: tracks, isLoading } = useTracks(true);
  const createTrack = useCreateTrack();
  const deleteTrack = useDeleteTrack();
  const [newName, setNewName] = useState("");

  const handleCreate = async (e: FormEvent) => {
    e.preventDefault();
    if (!newName.trim()) return;
    await createTrack.mutateAsync({ name: newName.trim() });
    setNewName("");
  };

  return (
    <div>
      <h2>Tracks</h2>

      <form className="card row" onSubmit={handleCreate}>
        <input
          placeholder="New track name"
          value={newName}
          onChange={(e) => setNewName(e.target.value)}
        />
        <button className="btn" type="submit" disabled={createTrack.isPending}>
          Add track
        </button>
      </form>

      {isLoading && <p className="muted">Loading tracks...</p>}

      <div className="grid">
        {tracks?.map((track) => (
          <div className="card" key={track.id}>
            <h3 style={{ color: track.colorHex ?? undefined }}>{track.name}</h3>
            {track.description && <p className="muted">{track.description}</p>}
            {track.archived && <span className="badge pending">Archived</span>}
            <div className="row" style={{ marginTop: 12 }}>
              <Link className="btn secondary" to={`/tracks/${track.id}`}>
                Open
              </Link>
              <button
                className="btn secondary"
                onClick={() => {
                  if (confirm(`Delete track "${track.name}" and all its topics/logs?`)) {
                    deleteTrack.mutate(track.id);
                  }
                }}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

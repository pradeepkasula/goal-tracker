import { useState, type FormEvent } from "react";
import { useParams } from "react-router-dom";
import { useTrack } from "../api/hooks/useTracks";
import { useAddReference, useCreateTopic, useTopics } from "../api/hooks/useTopics";
import { useRecordTopicLog } from "../api/hooks/useTopicLogs";

const TOPIC_LOG_STATUSES = ["STARTED", "IN_PROGRESS", "PRACTICED", "COMPLETED"];

export function TrackDetailPage() {
  const { trackId } = useParams();
  const id = Number(trackId);
  const { data: track } = useTrack(id);
  const { data: topics, isLoading } = useTopics(id);
  const createTopic = useCreateTopic(id);
  const addReference = useAddReference(id);
  const recordLog = useRecordTopicLog(id);

  const [newTopicTitle, setNewTopicTitle] = useState("");
  const [referenceDraft, setReferenceDraft] = useState<Record<number, { title: string; url: string }>>({});

  const handleCreateTopic = async (e: FormEvent) => {
    e.preventDefault();
    if (!newTopicTitle.trim()) return;
    await createTopic.mutateAsync({ title: newTopicTitle.trim() });
    setNewTopicTitle("");
  };

  const today = new Date().toISOString().slice(0, 10);

  return (
    <div>
      <h2>{track?.name ?? "Track"}</h2>
      {track?.description && <p className="muted">{track.description}</p>}

      <form className="card row" onSubmit={handleCreateTopic}>
        <input
          placeholder="New topic title"
          value={newTopicTitle}
          onChange={(e) => setNewTopicTitle(e.target.value)}
        />
        <button className="btn" type="submit" disabled={createTopic.isPending}>
          Add topic
        </button>
      </form>

      {isLoading && <p className="muted">Loading topics...</p>}

      {topics?.map((topic) => {
        const draft = referenceDraft[topic.id] ?? { title: "", url: "" };
        return (
          <div className="card" key={topic.id}>
            <div className="row" style={{ justifyContent: "space-between" }}>
              <h3 style={{ margin: 0 }}>{topic.title}</h3>
              <span className={`badge ${topic.progress.status.toLowerCase()}`}>{topic.progress.status}</span>
            </div>
            {topic.description && <p className="muted">{topic.description}</p>}

            <ul>
              {topic.references.map((ref) => (
                <li key={ref.id}>
                  <a href={ref.url} target="_blank" rel="noreferrer">
                    {ref.title}
                  </a>{" "}
                  <span className="muted">({ref.referenceType})</span>
                </li>
              ))}
            </ul>

            <div className="row">
              {TOPIC_LOG_STATUSES.map((status) => (
                <button
                  key={status}
                  className="btn secondary"
                  onClick={() => recordLog.mutate({ topicId: topic.id, date: today, status })}
                >
                  Mark {status.replace("_", " ").toLowerCase()}
                </button>
              ))}
            </div>

            <form
              className="row"
              style={{ marginTop: 10 }}
              onSubmit={async (e) => {
                e.preventDefault();
                if (!draft.title.trim() || !draft.url.trim()) return;
                await addReference.mutateAsync({
                  topicId: topic.id,
                  input: { referenceType: "ARTICLE", title: draft.title.trim(), url: draft.url.trim() },
                });
                setReferenceDraft((d) => ({ ...d, [topic.id]: { title: "", url: "" } }));
              }}
            >
              <input
                placeholder="Reference title"
                value={draft.title}
                onChange={(e) =>
                  setReferenceDraft((d) => ({ ...d, [topic.id]: { ...draft, title: e.target.value } }))
                }
              />
              <input
                placeholder="https://..."
                value={draft.url}
                onChange={(e) =>
                  setReferenceDraft((d) => ({ ...d, [topic.id]: { ...draft, url: e.target.value } }))
                }
              />
              <button className="btn secondary" type="submit">
                Add reference
              </button>
            </form>
          </div>
        );
      })}
    </div>
  );
}

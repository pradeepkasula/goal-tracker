import { useTracks } from "../api/hooks/useTracks";
import {
  useOverallPrediction,
  useRefreshOverallPrediction,
  useRefreshTrackPrediction,
  useTrackPrediction,
} from "../api/hooks/usePredictions";
import type { Prediction } from "../api/types";

function PredictionCard({
  prediction,
  onRefresh,
  refreshing,
}: {
  prediction: Prediction | undefined;
  onRefresh: () => void;
  refreshing: boolean;
}) {
  if (!prediction) {
    return <p className="muted">Loading prediction...</p>;
  }
  return (
    <div className="card">
      <div className="row" style={{ justifyContent: "space-between" }}>
        <h3 style={{ margin: 0 }}>{prediction.trackName}</h3>
        <button className="btn secondary" onClick={onRefresh} disabled={refreshing}>
          {refreshing ? "Refreshing..." : "Refresh"}
        </button>
      </div>
      <p className="muted">
        Coverage {prediction.coveragePct}% &middot; Pace {prediction.paceTopicsPerWeek} topics/week &middot;
        Confidence {prediction.confidence}
      </p>
      <p>
        {prediction.predictedCompletionDate
          ? `Estimated completion: ${prediction.predictedCompletionDate}`
          : "Not enough data yet to estimate a completion date."}
      </p>
      {prediction.aiNarrative ? (
        <p>{prediction.aiNarrative}</p>
      ) : (
        <p className="muted">AI insight unavailable right now - showing stats only.</p>
      )}
    </div>
  );
}

export function PredictionsPage() {
  const { data: tracks } = useTracks();
  const { data: overall } = useOverallPrediction();
  const refreshOverall = useRefreshOverallPrediction();

  return (
    <div>
      <h2>Predictions</h2>
      <PredictionCard
        prediction={overall}
        onRefresh={() => refreshOverall.mutate()}
        refreshing={refreshOverall.isPending}
      />
      {tracks?.map((track) => (
        <TrackPredictionRow key={track.id} trackId={track.id} />
      ))}
    </div>
  );
}

function TrackPredictionRow({ trackId }: { trackId: number }) {
  const { data: prediction } = useTrackPrediction(trackId);
  const refresh = useRefreshTrackPrediction();
  return (
    <PredictionCard
      prediction={prediction}
      onRefresh={() => refresh.mutate(trackId)}
      refreshing={refresh.isPending}
    />
  );
}

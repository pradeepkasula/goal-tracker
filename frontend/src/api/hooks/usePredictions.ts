import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { Prediction } from "../types";

export function useTrackPrediction(trackId: number | undefined) {
  return useQuery({
    queryKey: ["predictions", "track", trackId],
    queryFn: () => api.get<Prediction>(`predictions/tracks/${trackId}`),
    enabled: trackId !== undefined,
  });
}

export function useOverallPrediction() {
  return useQuery({
    queryKey: ["predictions", "overall"],
    queryFn: () => api.get<Prediction>("predictions/overall"),
  });
}

export function useRefreshTrackPrediction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (trackId: number) => api.post<Prediction>(`predictions/tracks/${trackId}/refresh`),
    onSuccess: (_data, trackId) =>
      queryClient.invalidateQueries({ queryKey: ["predictions", "track", trackId] }),
  });
}

export function useRefreshOverallPrediction() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: () => api.post<Prediction>("predictions/overall/refresh"),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["predictions", "overall"] }),
  });
}

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { Track } from "../types";

export interface TrackInput {
  name: string;
  description?: string;
  colorHex?: string;
  sortOrder?: number;
}

export function useTracks(includeArchived = false) {
  return useQuery({
    queryKey: ["tracks", includeArchived],
    queryFn: () => api.get<Track[]>(`tracks?includeArchived=${includeArchived}`),
  });
}

export function useTrack(trackId: number | undefined) {
  return useQuery({
    queryKey: ["tracks", trackId],
    queryFn: () => api.get<Track>(`tracks/${trackId}`),
    enabled: trackId !== undefined,
  });
}

export function useCreateTrack() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: TrackInput) => api.post<Track>("tracks", input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["tracks"] }),
  });
}

export function useUpdateTrack() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ trackId, input }: { trackId: number; input: TrackInput }) =>
      api.put<Track>(`tracks/${trackId}`, input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["tracks"] }),
  });
}

export function useDeleteTrack() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (trackId: number) => api.delete(`tracks/${trackId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["tracks"] }),
  });
}

export function useArchiveTrack() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ trackId, archived }: { trackId: number; archived: boolean }) =>
      api.patch<Track>(`tracks/${trackId}/archive`, { archived }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["tracks"] }),
  });
}

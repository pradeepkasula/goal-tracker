import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { DayHourConfig, TrackAllocationsResponse } from "../types";

export function useDayHourConfig() {
  return useQuery({
    queryKey: ["config", "day-hours"],
    queryFn: () => api.get<DayHourConfig[]>("config/day-hours"),
  });
}

export function useUpdateDayHourConfig() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ dayType, totalHours }: { dayType: string; totalHours: number }) =>
      api.put<DayHourConfig>(`config/day-hours/${dayType}`, { totalHours }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["config"] });
    },
  });
}

export function useTrackAllocations() {
  return useQuery({
    queryKey: ["config", "track-allocations"],
    queryFn: () => api.get<TrackAllocationsResponse>("config/track-allocations"),
  });
}

export function useUpsertAllocation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ trackId, dayType, allocatedHours }: { trackId: number; dayType: string; allocatedHours: number }) =>
      api.put(`config/track-allocations/${trackId}/${dayType}`, { allocatedHours }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["config"] });
    },
  });
}

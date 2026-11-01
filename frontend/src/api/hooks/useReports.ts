import { useQuery } from "@tanstack/react-query";
import { api } from "../client";
import type { AdherenceReport, TrackCoveragePoint, TrackHoursPoint } from "../types";

export function useAdherenceReport(from: string, to: string) {
  return useQuery({
    queryKey: ["reports", "adherence", from, to],
    queryFn: () => api.get<AdherenceReport>(`reports/adherence?from=${from}&to=${to}`),
  });
}

export function useHoursByTrack(from: string, to: string) {
  return useQuery({
    queryKey: ["reports", "hours-by-track", from, to],
    queryFn: () => api.get<TrackHoursPoint[]>(`reports/hours-by-track?from=${from}&to=${to}`),
  });
}

export function useCoverageReport() {
  return useQuery({
    queryKey: ["reports", "coverage"],
    queryFn: () => api.get<TrackCoveragePoint[]>("reports/coverage"),
  });
}

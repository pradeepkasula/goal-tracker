import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { DailyLog, TodayView } from "../types";

export function useToday() {
  return useQuery({
    queryKey: ["dashboard", "today"],
    queryFn: () => api.get<TodayView>("dashboard/today"),
  });
}

export function usePatchDailyLog() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      trackId,
      date,
      actualHours,
      notes,
    }: {
      trackId: number;
      date: string;
      actualHours?: number;
      notes?: string;
    }) => api.patch<DailyLog>(`daily-logs/${trackId}/${date}`, { actualHours, notes }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["dashboard", "today"] });
      queryClient.invalidateQueries({ queryKey: ["reports"] });
      queryClient.invalidateQueries({ queryKey: ["daily-logs"] });
    },
  });
}

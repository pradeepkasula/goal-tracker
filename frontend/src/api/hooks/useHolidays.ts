import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { Holiday } from "../types";

export function useHolidays() {
  return useQuery({
    queryKey: ["holidays"],
    queryFn: () => api.get<Holiday[]>("holidays"),
  });
}

export function useAddHoliday() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: { date: string; label?: string }) => api.post<Holiday>("holidays", input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["holidays"] }),
  });
}

export function useDeleteHoliday() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (holidayId: number) => api.delete(`holidays/${holidayId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["holidays"] }),
  });
}

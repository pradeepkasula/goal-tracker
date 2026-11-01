import { useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { TopicLog } from "../types";

export interface TopicLogInput {
  topicId: number;
  date: string;
  status: string;
  minutesSpent?: number;
  notes?: string;
}

export function useRecordTopicLog(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: TopicLogInput) => api.post<TopicLog>("topic-logs", input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["topics", trackId] });
      queryClient.invalidateQueries({ queryKey: ["dashboard", "today"] });
      queryClient.invalidateQueries({ queryKey: ["reports"] });
    },
  });
}

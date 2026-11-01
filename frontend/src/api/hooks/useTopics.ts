import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../client";
import type { Topic, TopicReference } from "../types";

export interface TopicInput {
  title: string;
  description?: string;
  sortOrder?: number;
  estimatedMinutes?: number;
}

export interface TopicReferenceInput {
  referenceType: string;
  title: string;
  url: string;
  sortOrder?: number;
}

export function useTopics(trackId: number | undefined) {
  return useQuery({
    queryKey: ["topics", trackId],
    queryFn: () => api.get<Topic[]>(`tracks/${trackId}/topics`),
    enabled: trackId !== undefined,
  });
}

export function useCreateTopic(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (input: TopicInput) => api.post<Topic>(`tracks/${trackId}/topics`, input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["topics", trackId] }),
  });
}

export function useUpdateTopic(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ topicId, input }: { topicId: number; input: TopicInput }) =>
      api.put<Topic>(`topics/${topicId}`, input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["topics", trackId] }),
  });
}

export function useDeleteTopic(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (topicId: number) => api.delete(`topics/${topicId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["topics", trackId] }),
  });
}

export function useReorderTopics(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (orderedTopicIds: number[]) =>
      api.patch(`tracks/${trackId}/topics/reorder`, { orderedTopicIds }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["topics", trackId] }),
  });
}

export function useAddReference(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ topicId, input }: { topicId: number; input: TopicReferenceInput }) =>
      api.post<TopicReference>(`topics/${topicId}/references`, input),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["topics", trackId] }),
  });
}

export function useDeleteReference(trackId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (referenceId: number) => api.delete(`references/${referenceId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["topics", trackId] }),
  });
}

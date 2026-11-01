export interface LoginResponse {
  token: string;
  expiresInMinutes: number;
  userId: number;
  username: string;
  displayName: string | null;
  role: string;
}

export interface MeResponse {
  id: number;
  username: string;
  displayName: string | null;
  role: string;
}

export interface Track {
  id: number;
  name: string;
  description: string | null;
  colorHex: string | null;
  sortOrder: number;
  archived: boolean;
}

export interface TopicReference {
  id: number;
  referenceType: string;
  title: string;
  url: string;
  sortOrder: number;
}

export interface TopicProgress {
  status: string;
  timesPracticed: number;
  totalMinutesSpent: number;
  lastPracticedAt: string | null;
  completedAt: string | null;
}

export interface Topic {
  id: number;
  trackId: number;
  title: string;
  description: string | null;
  sortOrder: number;
  estimatedMinutes: number | null;
  progress: TopicProgress;
  references: TopicReference[];
}

export interface DayHourConfig {
  dayType: "WEEKDAY" | "WEEKEND" | "HOLIDAY";
  totalHours: number;
}

export interface TrackAllocation {
  trackId: number;
  trackName: string;
  dayType: string;
  allocatedHours: number;
}

export interface AllocationWarning {
  dayType: string;
  configuredTotal: number;
  allocatedSum: number;
  delta: number;
}

export interface TrackAllocationsResponse {
  allocations: TrackAllocation[];
  warnings: AllocationWarning[];
}

export interface Holiday {
  id: number;
  date: string;
  label: string | null;
}

export interface DailyLog {
  trackId: number;
  trackName: string | null;
  date: string;
  dayType: string;
  plannedHours: number;
  actualHours: number;
  status: string;
  notes: string | null;
}

export interface TopicLog {
  id: number;
  topicId: number;
  date: string;
  status: string;
  minutesSpent: number;
  notes: string | null;
}

export interface TodayView {
  date: string;
  dayType: string | null;
  trackLogs: DailyLog[];
  totalPlannedHours: number;
  totalActualHours: number;
  topicsCompletedToday: number;
  topicsTouchedToday: number;
}

export interface AdherenceReport {
  adherencePct: number;
  currentStreak: number;
  longestStreak: number;
  totalLoggedDays: number;
  adherentDays: number;
}

export interface TrackHoursPoint {
  trackId: number;
  trackName: string;
  plannedHours: number;
  actualHours: number;
}

export interface TrackCoveragePoint {
  trackId: number;
  trackName: string;
  totalTopics: number;
  completedTopics: number;
  coveragePct: number;
}

export interface Prediction {
  trackId: number | null;
  trackName: string;
  predictedCompletionDate: string | null;
  paceTopicsPerWeek: number;
  coveragePct: number;
  confidence: string;
  aiNarrative: string | null;
  computedAt: string;
}

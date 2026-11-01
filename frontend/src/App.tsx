import { Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage";
import { TracksListPage } from "./pages/TracksListPage";
import { TrackDetailPage } from "./pages/TrackDetailPage";
import { HourConfigPage } from "./pages/HourConfigPage";
import { HolidaysPage } from "./pages/HolidaysPage";
import { ReportsPage } from "./pages/ReportsPage";
import { PredictionsPage } from "./pages/PredictionsPage";

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Layout>
              <DashboardPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/tracks"
        element={
          <ProtectedRoute>
            <Layout>
              <TracksListPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/tracks/:trackId"
        element={
          <ProtectedRoute>
            <Layout>
              <TrackDetailPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/config/hours"
        element={
          <ProtectedRoute>
            <Layout>
              <HourConfigPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/config/holidays"
        element={
          <ProtectedRoute>
            <Layout>
              <HolidaysPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/reports"
        element={
          <ProtectedRoute>
            <Layout>
              <ReportsPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/predictions"
        element={
          <ProtectedRoute>
            <Layout>
              <PredictionsPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}

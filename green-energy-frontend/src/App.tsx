import LoginPage from "./pages/LoginPage";
import MapPage from "./pages/MapPage";
import AdminPage from "./pages/AdminPage";
import { Routes, Route, Navigate } from "react-router-dom";
import { Header } from "./components/Header";
import { ProtectedRoute } from "./components/ProtectedRoute";

export function App() {
  const token = localStorage.getItem("token");

  return (
    <>
      {token && <Header />}

      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route path="/" element={<Navigate to="/map" replace />} />

        <Route
          path="/map"
          element={
            <ProtectedRoute>
              <MapPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/admin"
          element={
            <ProtectedRoute role="ADMIN">
              <AdminPage />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<Navigate to="/map" replace />} />
      </Routes>
    </>
  );
}

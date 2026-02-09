import { Navigate } from "react-router-dom";
import type { JSX } from "react/jsx-runtime";

export function ProtectedRoute({
  children,
  role,
}: {
  children: JSX.Element;
  role?: "USER" | "ADMIN";
}) {
  const token = localStorage.getItem("token");
  const userRole = localStorage.getItem("role");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (role && userRole !== role) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

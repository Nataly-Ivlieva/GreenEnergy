import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import type { JSX } from "react/jsx-runtime";

type AppRole = "USER" | "ADMIN";

type ProtectedRouteProps = {
  children: JSX.Element;
  role?: AppRole;
};

export function ProtectedRoute({ children, role }: ProtectedRouteProps) {
  const { isAuthenticated, role: userRole } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (role && userRole !== role) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

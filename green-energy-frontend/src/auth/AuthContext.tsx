import { createContext, useContext, useMemo, useState } from "react";
import { jwtDecode } from "jwt-decode";

type AppRole = "USER" | "ADMIN";

type JwtPayload = {
  role?: AppRole;
};

type AuthContextType = {
  token: string | null;
  role: AppRole | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  setToken: (t: string | null) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextType>(null!);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setTokenState] = useState<string | null>(
    localStorage.getItem("token"),
  );

  const role = useMemo(() => {
    if (!token) return null;

    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded.role ?? null;
    } catch {
      return null;
    }
  }, [token]);

  const setToken = (t: string | null) => {
    if (t) {
      localStorage.setItem("token", t);
    } else {
      localStorage.removeItem("token");
    }
    setTokenState(t);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setTokenState(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        role,
        isAuthenticated: !!token,
        isAdmin: role === "ADMIN",
        setToken,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);

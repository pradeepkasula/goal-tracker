import { createContext, useContext, useState, type ReactNode } from "react";
import { api, getToken, setToken } from "../api/client";
import type { LoginResponse } from "../api/types";

interface AuthUser {
  userId: number;
  username: string;
  displayName: string | null;
  role: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const USER_KEY = "goal-tracker:user";

function readStoredUser(): AuthUser | null {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? (JSON.parse(raw) as AuthUser) : null;
  } catch {
    return null;
  }
}

function storeUser(user: AuthUser | null) {
  try {
    if (user) {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(USER_KEY);
    }
  } catch {
    // ignore - per-viewer convenience only
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => (getToken() ? readStoredUser() : null));

  const login = async (username: string, password: string) => {
    const result = await api.post<LoginResponse>("auth/login", { username, password });
    setToken(result.token);
    const authUser: AuthUser = {
      userId: result.userId,
      username: result.username,
      displayName: result.displayName,
      role: result.role,
    };
    storeUser(authUser);
    setUser(authUser);
  };

  const logout = () => {
    setToken(null);
    storeUser(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: user !== null, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
}

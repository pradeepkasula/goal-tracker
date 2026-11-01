import type { ReactNode } from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { ThemeSwitcher } from "../theme/ThemeSwitcher";
import { APP_VERSION } from "../version";

const NAV_ITEMS = [
  { to: "/dashboard", label: "Dashboard" },
  { to: "/tracks", label: "Tracks" },
  { to: "/config/hours", label: "Hours & Allocation" },
  { to: "/config/holidays", label: "Holidays" },
  { to: "/reports", label: "Reports" },
  { to: "/predictions", label: "Predictions" },
];

export function Layout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth();

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="brand">Goal Tracker</div>
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) => `nav-link${isActive ? " active" : ""}`}
          >
            {item.label}
          </NavLink>
        ))}
        <div style={{ marginTop: "auto", paddingTop: 16 }}>
          <div className="muted">{user?.displayName ?? user?.username}</div>
          <button className="btn secondary" style={{ marginTop: 8, width: "100%" }} onClick={logout}>
            Sign out
          </button>
          <div className="muted" style={{ marginTop: 8 }}>
            v{APP_VERSION}
          </div>
        </div>
      </aside>
      <main className="main">
        <div className="topbar">
          <ThemeSwitcher />
        </div>
        {children}
      </main>
    </div>
  );
}

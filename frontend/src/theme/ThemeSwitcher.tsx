import { useTheme, type ThemeName } from "./ThemeContext";

const THEME_OPTIONS: { value: ThemeName; label: string }[] = [
  { value: "default", label: "Default" },
  { value: "ocean", label: "Ocean" },
];

export function ThemeSwitcher() {
  const { theme, mode, setTheme, toggleMode } = useTheme();

  return (
    <div className="row">
      <select
        aria-label="Color theme"
        value={theme}
        onChange={(e) => setTheme(e.target.value as ThemeName)}
      >
        {THEME_OPTIONS.map((opt) => (
          <option key={opt.value} value={opt.value}>
            {opt.label}
          </option>
        ))}
      </select>
      <button className="btn secondary icon" onClick={toggleMode} title="Toggle light/dark mode">
        {mode === "light" ? "Dark mode" : "Light mode"}
      </button>
    </div>
  );
}

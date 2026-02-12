import { useState } from "react";
import { createUser } from "../api/adminApi";
import "./AuthPages.css";

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<"USER" | "ADMIN">("USER");

  const [message, setMessage] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);

  const submit = async () => {
    try {
      await createUser(username, password, role);

      setSuccess(true);
      setMessage("Benutzer wurde erfolgreich registriert.");

      setUsername("");
      setPassword("");
      setRole("USER");
    } catch (e) {
      setSuccess(false);
      setMessage("Benutzer existiert bereits.");
    }

    setTimeout(() => {
      setMessage(null);
    }, 3000);
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2 className="auth-title">Benutzer erstellen</h2>

        <input
          className="auth-input"
          placeholder="Benutzername"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          className="auth-input"
          type="password"
          placeholder="Passwort"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <select
          className="auth-select"
          value={role}
          onChange={(e) => setRole(e.target.value as "USER" | "ADMIN")}
        >
          <option value="USER">Benutzer</option>
          <option value="ADMIN">Administrator</option>
        </select>

        <button className="auth-button" onClick={submit}>
          Erstellen
        </button>
      </div>

      {message && (
        <div className={`popup ${success ? "popup-success" : "popup-error"}`}>
          {message}
        </div>
      )}
    </div>
  );
}

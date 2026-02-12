import { useState } from "react";
import { login } from "../api/authApi";
import { useAuth } from "../auth/AuthContext";
import { useNavigate } from "react-router-dom";
import "./AuthPages.css";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const { setToken } = useAuth();
  const navigate = useNavigate();

  const submit = async () => {
    try {
      const token = await login(username, password);
      setToken(token);
      navigate("/map");
    } catch (e) {
      setError("Ungültiger Benutzername oder Passwort");
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h2 className="auth-title">Anmelden</h2>

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

        <button className="auth-button" onClick={submit}>
          Anmelden
        </button>

        {error && <p className="auth-error">{error}</p>}
      </div>
    </div>
  );
}

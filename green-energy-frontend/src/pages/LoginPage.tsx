import { useState } from "react";
import { login } from "../api/authApi";
import { useAuth } from "../auth/AuthContext";
import { useNavigate } from "react-router-dom";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const { setToken } = useAuth();
  const navigate = useNavigate();

  const submit = async () => {
    try {
      console.log("SUBMIT CLICK");
      const token = await login(username, password);
      setToken(token);
      navigate("/map");
    } catch (e) {
      console.log("Error", e);
      setError("Ungültiger Benutzername oder Passwort");
    }
  };

  return (
    <div style={{ padding: 40 }}>
      <h2>Anmelden</h2>

      <input
        placeholder="Benutzername"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <br />

      <input
        type="password"
        placeholder="Passwort"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <br />

      <button onClick={submit}>Anmelden</button>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}

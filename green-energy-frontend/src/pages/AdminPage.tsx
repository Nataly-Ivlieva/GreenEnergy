import { useState } from "react";
import { createUser } from "../api/adminApi";

export default function AdminPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<"USER" | "ADMIN">("USER");

  const submit = async () => {
    await createUser(username, password, role);
    alert("Benutzer erstellt");
  };

  return (
    <div>
      <h2>Admin</h2>

      <input
        placeholder="Benutzername"
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        type="password"
        placeholder="Passwort"
        onChange={(e) => setPassword(e.target.value)}
      />

      <select onChange={(e) => setRole(e.target.value as any)}>
        <option value="USER">Benutzer</option>
        <option value="ADMIN">Administrator</option>
      </select>

      <button onClick={submit}>Erstellen</button>
    </div>
  );
}

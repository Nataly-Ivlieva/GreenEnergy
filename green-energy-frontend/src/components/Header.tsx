import { Link, useNavigate } from "react-router-dom";
import Logo from "../assets/logo.png";

export function Header() {
  const navigate = useNavigate();
  const role = localStorage.getItem("role");

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    navigate("/login");
  };

  return (
    <header style={styles.header}>
      <div style={styles.left}>
        <img src={Logo} alt="logo" style={styles.logo} />
        <h2>Green Energy Monitoring</h2>
      </div>

      <nav style={styles.nav}>
        <Link to="/map">Karte</Link>
        <Link to="/charts">Diagramme</Link>

        {role === "ADMIN" && <Link to="/admin">Benutzerregistrierung</Link>}

        <button onClick={logout}>Abmelden</button>
      </nav>
    </header>
  );
}

const styles = {
  header: {
    display: "flex",
    justifyContent: "space-between",
    padding: "12px 24px",
    background: "#0f172a",
    color: "white",
  },
  left: {
    display: "flex",
    gap: "12px",
    alignItems: "center",
  },
  logo: {
    height: 32,
  },
  nav: {
    display: "flex",
    gap: "16px",
    alignItems: "center",
  },
};

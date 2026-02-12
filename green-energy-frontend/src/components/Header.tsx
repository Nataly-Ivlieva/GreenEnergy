import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import Logo from "../assets/logo.png";
import "./Header.css";

export function Header() {
  const navigate = useNavigate();
  const { isAuthenticated, isAdmin, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="header">
      <div className="header-left">
        <img src={Logo} alt="Logo" className="header-logo" />
        <h2 className="header-title">Green Energy Monitoring</h2>
      </div>

      <nav className="header-nav">
        {isAuthenticated && (
          <Link to="/map" className="nav-link">
            Karte
          </Link>
        )}

        {isAdmin && (
          <Link to="/admin" className="nav-link">
            Benutzerregistrierung
          </Link>
        )}

        {isAuthenticated && (
          <button className="logout-button" onClick={handleLogout}>
            Abmelden
          </button>
        )}
      </nav>
    </header>
  );
}

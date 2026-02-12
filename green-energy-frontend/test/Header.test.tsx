import { render, screen, fireEvent } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { MemoryRouter } from "react-router-dom";
import { Header } from "../src/components/Header";

// --- mocks ---

const logoutMock = vi.fn();
const navigateMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<any>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

// ВАЖНО: мок как функция, которую можно менять
const useAuthMock = vi.fn();

vi.mock("../src/auth/AuthContext", () => ({
  useAuth: () => useAuthMock(),
}));

describe("Header", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders only logo and title when not authenticated", () => {
    useAuthMock.mockReturnValue({
      isAuthenticated: false,
      isAdmin: false,
      logout: logoutMock,
    });

    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>,
    );

    expect(screen.getByText("Green Energy Monitoring")).toBeInTheDocument();

    expect(screen.queryByText("Karte")).toBeNull();
    expect(screen.queryByText("Benutzerregistrierung")).toBeNull();
    expect(screen.queryByText("Abmelden")).toBeNull();
  });

  it("renders USER navigation", () => {
    useAuthMock.mockReturnValue({
      isAuthenticated: true,
      isAdmin: false,
      logout: logoutMock,
    });

    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>,
    );

    expect(screen.getByText("Karte")).toBeInTheDocument();
    expect(screen.queryByText("Benutzerregistrierung")).toBeNull();
    expect(screen.getByText("Abmelden")).toBeInTheDocument();
  });

  it("renders ADMIN navigation", () => {
    useAuthMock.mockReturnValue({
      isAuthenticated: true,
      isAdmin: true,
      logout: logoutMock,
    });

    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>,
    );

    expect(screen.getByText("Karte")).toBeInTheDocument();
    expect(screen.getByText("Benutzerregistrierung")).toBeInTheDocument();
    expect(screen.getByText("Abmelden")).toBeInTheDocument();
  });

  it("logs out correctly", () => {
    useAuthMock.mockReturnValue({
      isAuthenticated: true,
      isAdmin: false,
      logout: logoutMock,
    });

    render(
      <MemoryRouter>
        <Header />
      </MemoryRouter>,
    );

    fireEvent.click(screen.getByText("Abmelden"));

    expect(logoutMock).toHaveBeenCalled();
    expect(navigateMock).toHaveBeenCalledWith("/login");
  });
});

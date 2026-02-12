import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, it, expect, vi, beforeEach } from "vitest";
import LoginPage from "../src/pages/LoginPage";

// --- MOCKS ---

vi.mock("../src/api/authApi", () => ({
  login: vi.fn(),
}));

const setTokenMock = vi.fn();

vi.mock("../src/auth/AuthContext", () => ({
  useAuth: () => ({
    setToken: setTokenMock,
  }),
}));

const navigateMock = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual<any>("react-router-dom");
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

import { login } from "../src/api/authApi";

describe("LoginPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders login form", () => {
    render(<LoginPage />);

    expect(
      screen.getByRole("heading", { name: "Anmelden" }),
    ).toBeInTheDocument();

    expect(screen.getByPlaceholderText("Benutzername")).toBeInTheDocument();

    expect(screen.getByPlaceholderText("Passwort")).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: "Anmelden" }),
    ).toBeInTheDocument();
  });

  it("logs in successfully", async () => {
    (login as any).mockResolvedValue("fake-token");

    render(<LoginPage />);

    fireEvent.change(screen.getByPlaceholderText("Benutzername"), {
      target: { value: "admin" },
    });

    fireEvent.change(screen.getByPlaceholderText("Passwort"), {
      target: { value: "1234" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Anmelden" }));

    await waitFor(() => {
      expect(login).toHaveBeenCalledWith("admin", "1234");
      expect(setTokenMock).toHaveBeenCalledWith("fake-token");
      expect(navigateMock).toHaveBeenCalledWith("/map");
    });
  });

  it("shows error on failed login", async () => {
    (login as any).mockRejectedValue(new Error("Login failed"));

    render(<LoginPage />);

    fireEvent.change(screen.getByPlaceholderText("Benutzername"), {
      target: { value: "wrong" },
    });

    fireEvent.change(screen.getByPlaceholderText("Passwort"), {
      target: { value: "wrong" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Anmelden" }));

    await waitFor(() => {
      expect(
        screen.getByText("Ungültiger Benutzername oder Passwort"),
      ).toBeInTheDocument();
    });
  });
});

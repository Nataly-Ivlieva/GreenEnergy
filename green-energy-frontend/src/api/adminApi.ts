import { api } from "./api";

export function createUser(
  username: string,
  password: string,
  role: "USER" | "ADMIN"
) {
  return api.post<void>("/api/v1/admin/users", {
    username,
    password,
    role,
  });
}

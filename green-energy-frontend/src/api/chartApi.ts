import { api } from "./api";

export function fetchChart(params?: {
  id?: string;
}) {
  const cleaned = Object.fromEntries(
    Object.entries(params ?? {}).filter(
      ([, v]) => v !== undefined && v !== ""
    )
  );

  return api
    .get("/api/v1/charts", { params: cleaned })
    .then(res => res.data);
}
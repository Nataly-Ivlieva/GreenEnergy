import { api } from "./api";
import type { GeneratorStatus } from "../types/GeneratorStatus";

export async function fetchMap(): Promise<GeneratorStatus[]> {
  const res = await api.get("/api/v1/map");
  return res.data;
}


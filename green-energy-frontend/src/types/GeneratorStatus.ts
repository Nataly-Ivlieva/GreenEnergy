export interface GeneratorStatus {
  generatorId: number;
  type: string;
  name: string;
  latitude: number;
  longitude: number;
  expectedPowerKw: number;
  actualPowerKw: number | null;
  anomalous: boolean;
  anomalyType?: "WARNING" | "CRITICAL";
}

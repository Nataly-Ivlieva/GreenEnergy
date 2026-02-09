export interface EnergyChartPoint {
  timestamp: string;
  expectedPowerKw: number;
  actualPowerKw: number;
  anomalous: boolean;
}

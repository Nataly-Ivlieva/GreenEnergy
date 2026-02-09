import { useEffect, useState } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
} from "recharts";

import { useAuth } from "../auth/AuthContext";
import { fetchChart } from "../api/chartApi";
import type { EnergyChartPoint } from "../types/EnergyChartPoint";

export default function ChartsPage() {
  const { token } = useAuth();

  const [data, setData] = useState<EnergyChartPoint[]>([]);
  const [type, setType] = useState<string>("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) return;

    setLoading(true);

    fetchChart({ from: undefined, to: undefined, type })
      .then(setData)
      .finally(() => setLoading(false));
  }, [token, type]);

  if (loading) return <p>Lade Diagramme...</p>;

  return (
    <div style={{ padding: 24 }}>
      <h2>Energieerzeugung</h2>

      <label>
        Generator type:&nbsp;
        <select onChange={(e) => setType(e.target.value)}>
          <option value="">Alle</option>
          <option value="SOLAR">Solar</option>
          <option value="WIND">Wind</option>
          <option value="HYDRO">Hydro</option>
        </select>
      </label>

      <LineChart
        width={900}
        height={400}
        data={data}
        margin={{ top: 20, right: 30, left: 0, bottom: 0 }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="timestamp" />
        <YAxis />
        <Tooltip />
        <Legend />

        <Line
          type="monotone"
          dataKey="expectedPowerKw"
          name="Expected"
          stroke="#2ecc71"
          dot={false}
        />

        <Line
          type="monotone"
          dataKey="actualPowerKw"
          name="Actual"
          stroke="#3498db"
          dot={(p) =>
            p.payload.anomalous ? (
              <circle cx={p.cx} cy={p.cy} r={6} fill="red" />
            ) : null
          }
        />
      </LineChart>
    </div>
  );
}

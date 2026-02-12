import { useEffect, useState, useMemo } from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
} from "recharts";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";

import { useAuth } from "../auth/AuthContext";
import { fetchChart } from "../api/chartApi";
import { fetchMap } from "../api/mapApi";

import type { EnergyChartPoint } from "../types/EnergyChartPoint";
import type { GeneratorStatus } from "../types/GeneratorStatus";
import { iconByStatus } from "../map/icons";

import "./Dashboard.css";

export default function DashboardPage() {
  const { token } = useAuth();

  const [chartData, setChartData] = useState<EnergyChartPoint[]>([]);
  const [mapData, setMapData] = useState<GeneratorStatus[]>([]);
  const [selectedGeneratorId, setSelectedGeneratorId] = useState<number | null>(
    null,
  );

  const [loadingMap, setLoadingMap] = useState(true);
  const [loadingChart, setLoadingChart] = useState(false);

  useEffect(() => {
    fetchMap()
      .then(setMapData)
      .finally(() => setLoadingMap(false));
  }, []);

  useEffect(() => {
    if (!token || !selectedGeneratorId) return;

    setLoadingChart(true);

    fetchChart({ id: String(selectedGeneratorId) })
      .then(setChartData)
      .finally(() => setLoadingChart(false));
  }, [token, selectedGeneratorId]);

  const formattedData = useMemo(() => {
    return chartData.map((item, index) => ({
      ...item,
      formattedTime: formatTime(item.timestamp),
      showTick: index % 2 === 0,
    }));
  }, [chartData]);

  if (loadingMap) return <p className="loading">Daten werden geladen…</p>;

  return (
    <div className="dashboard">
      <div className="dashboard-container">
        {/* MAP */}
        <div className="card">
          <h3 className="card-title">Generatorenkarte</h3>

          <MapContainer center={[51.1657, 10.4515]} zoom={6} className="map">
            <TileLayer
              attribution="© OpenStreetMap"
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />

            {mapData.map((g) => (
              <Marker
                key={g.generatorId}
                position={[g.latitude, g.longitude]}
                icon={iconByStatus(g.anomalyType)}
                eventHandlers={{
                  click: () => setSelectedGeneratorId(g.generatorId),
                }}
              >
                <Popup>
                  <b>{g.name}</b>
                  <br />
                  Typ: {translateType(g.type)}
                  <br />
                  Leistung: {g.actualPowerKw} kW
                  <br />
                  Status: {translateStatus(g.anomalyType)}
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </div>

        {/* CHART */}
        <div className="card">
          <h3 className="card-title">Energieerzeugung</h3>

          {!selectedGeneratorId && (
            <p className="info-text">
              Bitte wählen Sie einen Generator auf der Karte aus.
            </p>
          )}

          {loadingChart && <p className="loading">Diagramm wird geladen…</p>}

          {selectedGeneratorId && !loadingChart && (
            <ResponsiveContainer width="100%" height={400}>
              <LineChart data={formattedData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis
                  dataKey="formattedTime"
                  tick={(props) => {
                    const { x, y, payload, index } = props;

                    if (typeof x !== "number" || typeof y !== "number") {
                      return null;
                    }

                    if (!formattedData[index]?.showTick) {
                      return null;
                    }

                    return (
                      <text
                        x={x}
                        y={y + 10}
                        textAnchor="middle"
                        fontSize={11}
                        fill="#166534"
                      >
                        {payload.value}
                      </text>
                    );
                  }}
                />

                <YAxis />
                <Tooltip />
                <Legend />

                <Line
                  type="monotone"
                  dataKey="expectedPowerKw"
                  name="Erwartet"
                  stroke="#16a34a"
                  dot={false}
                />

                <Line
                  type="monotone"
                  dataKey="actualPowerKw"
                  name="Tatsächlich"
                  stroke="#0ea5e9"
                  dot={(p) =>
                    p.payload.anomalous ? (
                      <circle cx={p.cx} cy={p.cy} r={5} fill="red" />
                    ) : null
                  }
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  );
}

/* ===== Helpers ===== */

function formatTime(timestamp: string) {
  const date = new Date(timestamp);
  return date.toLocaleTimeString("de-DE", {
    hour: "2-digit",
    minute: "2-digit",
  });
}

function translateType(type: GeneratorStatus["type"]) {
  switch (type) {
    case "SOLAR":
      return "Solar";
    case "WIND":
      return "Wind";
    case "HYDRO":
      return "Wasserkraft";
  }
}

function translateStatus(status: GeneratorStatus["anomalyType"]) {
  switch (status) {
    case undefined:
      return "Normal";
    case "WARNING":
      return "Warnung";
    case "CRITICAL":
      return "Kritisch";
  }
}

import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import { fetchMap } from "../api/mapApi";
import type { GeneratorStatus } from "../types/GeneratorStatus";
import { iconByStatus } from "../map/icons";

export default function MapPage() {
  const [data, setData] = useState<GeneratorStatus[]>([]);

  useEffect(() => {
    fetchMap().then(setData);
  }, []);

  return (
    <div style={{ padding: 24 }}>
      <h2>Generatorenkarte</h2>

      <MapContainer
        center={[51.1657, 10.4515]}
        zoom={6}
        style={{ height: "70vh", width: "100%" }}
      >
        <TileLayer
          attribution="© OpenStreetMap"
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {data.map((g) => (
          <Marker
            key={g.generatorId}
            position={[g.latitude, g.longitude]}
            icon={iconByStatus(g.anomalyType)}
          >
            <Popup>
              <b>Generator</b>
              <br />
              <b>Name:</b> {g.name}
              <br />
              <b>Typ:</b> {translateType(g.type)}
              <br />
              <b>Leistung:</b> {g.actualPowerKw} kW
              <br />
              <b>Status:</b> {translateStatus(g.anomalyType)}
            </Popup>
          </Marker>
        ))}
      </MapContainer>
    </div>
  );
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
      return "Anomalie";
  }
}

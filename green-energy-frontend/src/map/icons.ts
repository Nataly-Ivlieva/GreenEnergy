import L from "leaflet";
import type { GeneratorStatus } from "../types/GeneratorStatus";

const greenIcon = new L.Icon({
  iconUrl: "https://maps.gstatic.com/mapfiles/ms2/micons/green-dot.png",
  iconSize: [32, 32],
});

const yellowIcon = new L.Icon({
  iconUrl: "https://maps.gstatic.com/mapfiles/ms2/micons/yellow-dot.png",
  iconSize: [32, 32],
});

const redIcon = new L.Icon({
  iconUrl: "https://maps.gstatic.com/mapfiles/ms2/micons/red-dot.png",
  iconSize: [32, 32],
});

export function iconByStatus(status: GeneratorStatus["anomalyType"]) {
  switch (status) {
    case "WARNING":
      return yellowIcon;
    case "CRITICAL":
      return redIcon;
    default:
      return greenIcon;
  }
}
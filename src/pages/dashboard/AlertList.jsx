import { useEffect, useState } from "react";
import AlertCard from "./AlertCard";

function AlertList() {
  const [alerts, setAlerts] = useState([]);

  useEffect(() => {
    setAlerts([
      { id: 1, type: "Incendie", time: "10:30" },
      { id: 2, type: "Accident", time: "10:32" }
    ]);

    const interval = setInterval(() => {
      setAlerts(prev => [
        {
          id: Date.now(),
          type: "Alerte critique",
          time: new Date().toLocaleTimeString()
        },
        ...prev
      ]);
    }, 4000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div>
      <h2>📋 Alertes en direct</h2>
      {alerts.map(alert => (
        <AlertCard key={alert.id} alert={alert} />
      ))}
    </div>
  );
}

export default AlertList;
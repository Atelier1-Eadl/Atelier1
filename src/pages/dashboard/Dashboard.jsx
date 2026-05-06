import axios from "axios";
import AlertList from "./AlertList";
import StatsBar from "./StatsBar";
import "./dashboard.css";
import { useState } from "react";

function Dashboard() {
  const [data, setdata] = useState([]);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get("/api/alerts");
        if (response.status === 200) {
          setdata(response.data);
        }
        // Traitez les données et mettez à jour l'état si nécessaire
      } catch (error) {
        console.error("Erreur lors de la récupération des alertes :", error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="dashboard">
      <header className="header">
        <h1> CSU - Supervision Urbaine</h1>
      </header>

      <StatsBar />

      <div className="content">
        <div className="left">
          <AlertList alerts={data} />
        </div>

        <div className="right">
          <h2> Carte (à venir)</h2>
          <div className="map-placeholder">
            Carte interactive '' ici :
          </div>
        </div>
      </div>
    </div>

  );
}

export default Dashboard;
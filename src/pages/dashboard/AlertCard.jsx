function AlertCard({ alert }) {
  return (
    <div className="alert-card">
      <div className="alert-header">
        <span className="badge">CRITIQUE</span>
        <span>{alert.time}</span>
      </div>

      <h3>{alert.type}</h3>

      <button className="btn">Voir détail</button>
    </div>
  );
}

export default AlertCard;
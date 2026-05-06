function StatsBar() {
  return (
    <div className="stats">
      <div className="card red">🔴 Critiques</div>
      <div className="card orange">🟠 En cours</div>
      <div className="card green">🟢 Résolues</div>
    </div>
  );
}

export default StatsBar;
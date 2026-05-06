import React from 'react';
import { alertsData } from '../../data/alerts';
import { AlertOctagon } from 'lucide-react';

const CriticalAlerts = () => {
  // Filtrage strict sur le niveau "Critique"
  const criticalOnly = alertsData.filter(alert => alert.severity === "Critique");

  return (
    <div className="p-4 bg-white rounded-xl shadow-lg">
      <h2 className="flex items-center gap-2 text-xl font-bold text-red-600 mb-4">
        <AlertOctagon /> Alertes Critiques
      </h2>
      <ul className="space-y-3">
        {criticalOnly.map(alert => (
          <li key={alert.id} className="p-3 border border-red-100 bg-red-50 rounded-lg flex justify-between">
            <span className="font-medium">{alert.title}</span>
            <span className="text-xs text-red-400 font-mono">{alert.id}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default CriticalAlerts;
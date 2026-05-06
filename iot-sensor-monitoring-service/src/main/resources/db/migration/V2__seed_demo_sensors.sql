-- Demo seed: 10 air quality + 15 traffic sensors distributed across Paris
-- This script is idempotent (uses ON CONFLICT DO NOTHING)

INSERT INTO sensors (id, external_id, name, type, latitude, longitude, address, status, generation_frequency_ms, active)
VALUES
-- Air Quality sensors
('a1000000-0000-0000-0000-000000000001', 'AQ-PARIS-001', 'Capteur Qualite Air - Tour Eiffel',        'AIR_QUALITY', 48.8584, 2.2945, 'Champ de Mars, 75007 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000002', 'AQ-PARIS-002', 'Capteur Qualite Air - Opera',              'AIR_QUALITY', 48.8720, 2.3317, 'Place de l''Opera, 75009 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000003', 'AQ-PARIS-003', 'Capteur Qualite Air - Nation',             'AIR_QUALITY', 48.8484, 2.3963, 'Place de la Nation, 75011 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000004', 'AQ-PARIS-004', 'Capteur Qualite Air - Montmartre',         'AIR_QUALITY', 48.8867, 2.3431, 'Place du Tertre, 75018 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000005', 'AQ-PARIS-005', 'Capteur Qualite Air - Chatelet',           'AIR_QUALITY', 48.8606, 2.3467, 'Place du Chatelet, 75001 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000006', 'AQ-PARIS-006', 'Capteur Qualite Air - Bastille',           'AIR_QUALITY', 48.8533, 2.3692, 'Place de la Bastille, 75004 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000007', 'AQ-PARIS-007', 'Capteur Qualite Air - La Defense',         'AIR_QUALITY', 48.8918, 2.2356, 'Esplanade de La Defense, 92800 Puteaux', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000008', 'AQ-PARIS-008', 'Capteur Qualite Air - Gare du Nord',       'AIR_QUALITY', 48.8809, 2.3553, 'Place Napoléon III, 75010 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000009', 'AQ-PARIS-009', 'Capteur Qualite Air - Luxembourg',         'AIR_QUALITY', 48.8462, 2.3372, 'Jardin du Luxembourg, 75006 Paris', 'UNKNOWN', 10000, true),
('a1000000-0000-0000-0000-000000000010', 'AQ-PARIS-010', 'Capteur Qualite Air - Vincennes',          'AIR_QUALITY', 48.8483, 2.4390, 'Bois de Vincennes, 75012 Paris', 'UNKNOWN', 10000, true),

-- Traffic sensors
('b2000000-0000-0000-0000-000000000001', 'TR-PARIS-001', 'Radar Trafic - Boulevard Peripherique N',  'TRAFFIC', 48.9002, 2.3560, 'Bd Peripherique Nord, 75018 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000002', 'TR-PARIS-002', 'Radar Trafic - Boulevard Peripherique S',  'TRAFFIC', 48.8183, 2.3600, 'Bd Peripherique Sud, 75013 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000003', 'TR-PARIS-003', 'Radar Trafic - Champs-Elysees',            'TRAFFIC', 48.8698, 2.3078, 'Avenue des Champs-Elysees, 75008 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000004', 'TR-PARIS-004', 'Radar Trafic - Boulevard Haussman',        'TRAFFIC', 48.8755, 2.3321, 'Boulevard Haussmann, 75009 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000005', 'TR-PARIS-005', 'Radar Trafic - Rue de Rivoli',             'TRAFFIC', 48.8565, 2.3430, 'Rue de Rivoli, 75001 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000006', 'TR-PARIS-006', 'Radar Trafic - Avenue de la Republique',   'TRAFFIC', 48.8633, 2.3710, 'Avenue de la Republique, 75011 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000007', 'TR-PARIS-007', 'Radar Trafic - Boulevard Saint-Germain',   'TRAFFIC', 48.8530, 2.3332, 'Boulevard Saint-Germain, 75005 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000008', 'TR-PARIS-008', 'Radar Trafic - Porte Maillot',             'TRAFFIC', 48.8782, 2.2829, 'Porte Maillot, 75017 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000009', 'TR-PARIS-009', 'Radar Trafic - Porte de Versailles',       'TRAFFIC', 48.8317, 2.2891, 'Porte de Versailles, 75015 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000010', 'TR-PARIS-010', 'Radar Trafic - Pont de Bercy',             'TRAFFIC', 48.8418, 2.3762, 'Pont de Bercy, 75012 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000011', 'TR-PARIS-011', 'Radar Trafic - Porte d''Orleans',          'TRAFFIC', 48.8228, 2.3272, 'Porte d''Orleans, 75014 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000012', 'TR-PARIS-012', 'Radar Trafic - Porte de la Chapelle',      'TRAFFIC', 48.8979, 2.3573, 'Porte de la Chapelle, 75018 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000013', 'TR-PARIS-013', 'Radar Trafic - Avenue Montaigne',          'TRAFFIC', 48.8661, 2.3038, 'Avenue Montaigne, 75008 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000014', 'TR-PARIS-014', 'Radar Trafic - Quai de la Rapee',          'TRAFFIC', 48.8468, 2.3680, 'Quai de la Rapee, 75012 Paris', 'UNKNOWN', 10000, true),
('b2000000-0000-0000-0000-000000000015', 'TR-PARIS-015', 'Radar Trafic - Porte de Bagnolet',         'TRAFFIC', 48.8645, 2.4071, 'Porte de Bagnolet, 75020 Paris', 'UNKNOWN', 10000, true)
ON CONFLICT (external_id) DO NOTHING;

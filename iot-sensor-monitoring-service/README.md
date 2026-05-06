# iot-sensor-monitoring-service

Microservice Spring Boot 3.3 / Java 21 de monitoring IoT pour une plateforme Smart City.
Simule des capteurs de **qualite de l'air** (AIR_QUALITY) et de **trafic routier** (TRAFFIC),
expose leurs donnees via REST + SSE, et publie les evenements sur Kafka.

---

## Stack technique

| Composant | Technologie |
|---|---|
| Framework | Spring Boot 3.3, Java 21 |
| Persistance | Spring Data JPA + PostgreSQL 16 |
| Cache | Spring Data Redis 7 |
| Messaging | Spring Kafka (producer) |
| Migrations | Flyway |
| Serialisation | Jackson, MapStruct, Lombok |
| API docs | Springdoc OpenAPI (Swagger UI) |
| Metriques | Micrometer + Prometheus |
| Tests | JUnit 5, Testcontainers |
| Containerisation | Docker multi-stage + docker-compose |

---

## Demarrage rapide

### Prerequis

- Docker Desktop installe et en cours d'execution
- Java 21 + Maven 3.9 (pour le build local uniquement)

### 1. Demarrage avec Docker Compose (recommande)

```bash
cd iot-sensor-monitoring-service
docker-compose up -d
```

Les services demarrent dans cet ordre : PostgreSQL -> Redis -> Kafka -> microservice.

Une fois demarree, l'application est accessible sur : **http://localhost:8081**

Swagger UI : http://localhost:8081/swagger-ui.html

Actuator Health : http://localhost:8081/actuator/health

Prometheus metrics : http://localhost:8081/actuator/prometheus

### 2. Demarrage local (profil dev)

```bash
# Demarrer les dependances uniquement
docker-compose up -d postgres redis kafka

# Lancer l'application
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Seed : creation des capteurs de demonstration

Les capteurs Paris sont inseres via la migration Flyway **V2__seed_demo_sensors.sql**
(10 capteurs qualite de l'air + 15 capteurs trafic).

Pour creer un jeu supplementaire via l'API admin :

```bash
curl -X POST http://localhost:8081/api/v1/admin/seed
```

Reponse :
```json
{
  "createdSensors": 25,
  "message": "25 capteurs de demonstration crees et demarres"
}
```

---

## Exemples curl

### Lister tous les capteurs
```bash
curl http://localhost:8081/api/v1/sensors
```

### Lister uniquement les capteurs de trafic
```bash
curl "http://localhost:8081/api/v1/sensors?type=TRAFFIC"
```

### Creer un nouveau capteur
```bash
curl -X POST http://localhost:8081/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "externalId": "AQ-TEST-999",
    "name": "Capteur Test Eiffel",
    "type": "AIR_QUALITY",
    "latitude": 48.8584,
    "longitude": 2.2945,
    "address": "Champ de Mars, 75007 Paris",
    "generationFrequencyMs": 5000
  }'
```

### Obtenir le statut courant d'un capteur (depuis Redis)
```bash
curl http://localhost:8081/api/v1/sensors/{id}/status
```

### Historique des mesures de qualite de l'air
```bash
curl "http://localhost:8081/api/v1/sensors/air-quality/{id}/readings?page=0&size=20"
```

### Derniere mesure de trafic
```bash
curl http://localhost:8081/api/v1/sensors/traffic/{id}/readings/latest
```

### Resume global (AQI moyen, congestion, capteurs online/offline)
```bash
curl http://localhost:8081/api/v1/sensors/overview
```

### Stream SSE en temps reel
```bash
curl -N -H "Accept: text/event-stream" http://localhost:8081/api/v1/sensors/stream
```

### Injecter un incident : pic de pollution
```bash
curl -X POST http://localhost:8081/api/v1/admin/sensors/{id}/inject-incident \
  -H "Content-Type: application/json" \
  -d '{"type": "POLLUTION_SPIKE", "durationSeconds": 120}'
```

### Injecter un incident : embouteillage
```bash
curl -X POST http://localhost:8081/api/v1/admin/sensors/{id}/inject-incident \
  -H "Content-Type: application/json" \
  -d '{"type": "TRAFFIC_JAM", "durationSeconds": 180}'
```

### Injecter une panne capteur
```bash
curl -X POST http://localhost:8081/api/v1/admin/sensors/{id}/inject-incident \
  -H "Content-Type: application/json" \
  -d '{"type": "SENSOR_FAILURE"}'
```

### Demarrer / arreter la simulation d'un capteur
```bash
curl -X POST http://localhost:8081/api/v1/admin/sensors/{id}/start
curl -X POST http://localhost:8081/api/v1/admin/sensors/{id}/stop
```

---

## Modele de simulation

### Capteurs de qualite de l'air (AIR_QUALITY)

Le simulateur `AirQualitySimulator` genere des valeurs avec les logiques suivantes :

- **Inertie** : chaque nouvelle valeur est un melange 75/25 entre la valeur precedente
  et une valeur cible. Pas de saut brutal entre deux mesures.
- **Pattern horaire** : un facteur de trafic (gaussien centre sur 8h et 18h)
  influence les polluants NO2, PM2.5, PM10 et CO (correlation trafic/pollution).
- **Ozone** : pic en debut d'apres-midi (photochimie), independant du trafic.
- **Temperature** : cycle journalier sinusoidal (min 5h, max 14h).
- **Bruit gaussien** : ajoute sur chaque mesure pour simuler la variabilite capteur.
- **AQI europeen** : calcul sur le pire sous-indice (PM2.5, PM10, NO2, O3, CO).

Categories AQI (normes europeennes) :

| AQI | Categorie |
|---|---|
| 0-50 | GOOD |
| 51-100 | FAIR |
| 101-150 | MODERATE |
| 151-200 | POOR |
| 201-300 | VERY_POOR |
| 301-500 | EXTREMELY_POOR |

### Capteurs de trafic (TRAFFIC)

Le simulateur `TrafficSimulator` applique :

- **Double pic quotidien** : facteur de charge eleve 7-9h (matin) et 17-19h (soir).
- **Facteur week-end** : 45% du volume semaine le samedi et dimanche.
- **Correlation vitesse/occupation** : la vitesse moyenne diminue avec le taux d'occupation
  selon une fonction de puissance (realiste vs modeles lineaires).
- **Classification de congestion** derivee de la vitesse et du taux d'occupation :

| Vitesse | Occupation | Niveau |
|---|---|---|
| >= 90 km/h | < 25% | FREE_FLOW |
| >= 70 km/h | < 45% | LIGHT |
| >= 50 km/h | < 65% | MODERATE |
| >= 25 km/h | < 80% | HEAVY |
| < 25 km/h | >= 80% | JAM |

### Orchestrateur de simulation

`SimulationOrchestrator` maintient un `ScheduledExecutorService` avec une tache par
capteur actif. Chaque tache :
1. Appelle le simulateur approprie pour generer une lecture.
2. Persiste la lecture en base (JPA).
3. Met a jour `lastSeenAt` et `status = ONLINE` dans PostgreSQL.
4. Publie l'evenement Telemetrie sur Kafka.
5. Publie une alerte si AQI >= POOR ou trafic = JAM.

Les simulations peuvent etre demarrees/arretees dynamiquement via l'API admin
**sans redemarrer l'application**.

---

## Topics Kafka

| Topic | Contenu |
|---|---|
| `sensor.telemetry.air-quality` | Mesures brutes qualite de l'air |
| `sensor.telemetry.traffic` | Mesures brutes trafic |
| `sensor.status.changed` | Changements d'etat capteur |
| `sensor.alert` | Alertes (AQI eleve, embouteillage) |

---

## Detection offline

Un scheduler (`OfflineDetectionScheduler`) s'execute periodiquement et bascule en
`OFFLINE` tout capteur actif dont `lastSeenAt` depasse le seuil configure
(`app.simulation.offline-threshold-seconds`, defaut 60s).

---

## Variables d'environnement Docker

| Variable | Defaut | Description |
|---|---|---|
| `POSTGRES_HOST` | `postgres` | Hote PostgreSQL |
| `POSTGRES_DB` | `iot_sensors` | Nom de la base |
| `POSTGRES_USER` | `iot_user` | Utilisateur |
| `POSTGRES_PASSWORD` | `iot_pass` | Mot de passe |
| `REDIS_HOST` | `redis` | Hote Redis |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:29092` | Brokers Kafka |
| `EUREKA_ENABLED` | `false` | Activer Eureka |
| `EUREKA_URL` | `http://eureka:8761/eureka/` | URL Eureka |
| `CONFIG_SERVER_ENABLED` | `false` | Activer Config Server |

---

## Integration Gateway

Le service est pret pour un Spring Cloud Gateway. Il lit les headers injectes par le gateway :

- `X-Correlation-Id` : ID de correlation (genere si absent)
- `X-User-Id` : ID utilisateur injecte par le gateway
- `X-User-Roles` : roles injectes par le gateway

Ces valeurs sont propagees dans les MDC logs et les headers de reponse.

Pour activer Eureka, passer `EUREKA_ENABLED=true` et definir `EUREKA_URL`.

---

## Tests

```bash
# Tests unitaires uniquement (pas besoin de Docker)
mvn test -Dgroups="!integration"

# Tous les tests (necessite Docker pour Testcontainers)
mvn verify
```

Les tests d'integration (`SensorControllerIT`) demarrent automatiquement
PostgreSQL, Redis et Kafka via Testcontainers.

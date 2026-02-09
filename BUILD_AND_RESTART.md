# Texera Build & Restart Instructions# Texera Build & Restart Instructions



## Prerequisites## Backend Build



- JDK 171. Open a terminal in the workspace root:

- sbt   ```sh

- Node.js & npm   cd /Users/prateekganigi/Desktop/Texera/texera

- PostgreSQL (running)   ```

2. Build all backend services:

## Service Architecture   ```sh

   sbt clean dist

| Service                         | Port | Description                               |   ```

| ------------------------------- | ---- | ----------------------------------------- |3. Unzip all service packages:

| config-service                  | 9094 | Application configuration                 |   ```sh

| file-service                    | 9092 | Dataset / file management                 |   for svc in workflow-compiling-service file-service computing-unit-managing-service config-service access-control-service; do unzip -o ${svc}/target/universal/${svc}-*.zip -d target/; done

| workflow-compiling-service      | 9090 | Workflow compilation                      |   unzip -o amber/target/universal/amber-*.zip -d amber/target/

| computing-unit-managing-service | 8888 | Computing unit lifecycle management       |   ```

| access-control-service          | 9096 | Auth, ACL, AI model/chat endpoints        |

| texera-web-application          | 8080 | Main REST API & collaboration websocket   |## Frontend Build

| computing-unit-master           | 8085 | Workflow execution websocket (Pekko: 2552)|

| Frontend (Angular)              | 4200 | Dev server                                |1. Open a terminal in the frontend directory:

| y-websocket                     | 1234 | Shared-editing real-time collaboration    |   ```sh

   cd /Users/prateekganigi/Desktop/Texera/texera/frontend

---   ```

2. Build the Angular frontend:

## 1. Backend Build   ```sh

   ng build

From the workspace root:   ```



```sh## Restart All Servers & Services

cd /Users/prateekganigi/Desktop/Texera/texera

sbt clean dist1. Stop any running backend services:

```   ```sh

   pkill -f "config-service|file-service|workflow-compiling-service|computing-unit-managing-service|computing-unit-master|access-control-service|texera-web-application"

Then unzip all service distributions:   ```

2. Start backend services (from workspace root):

```sh   ```sh

# Microservices (output goes to <root>/target/)   source .dev-env.sh

for svc in workflow-compiling-service file-service computing-unit-managing-service config-service access-control-service; do   bin/config-service.sh &

  unzip -o ${svc}/target/universal/${svc}-*.zip -d target/   bin/file-service.sh &

done   bin/workflow-compiling-service.sh &

   bin/computing-unit-managing-service.sh &

# Amber (texera-web-application + computing-unit-master, output goes to amber/target/)   bin/workflow-computing-unit.sh &

unzip -o amber/target/universal/amber-*.zip -d amber/target/   target/access-control-service-*/bin/access-control-service &

```   cd amber && target/amber-*/bin/texera-web-application &

   ```

## 2. Frontend Build3. Start frontend dev server (from frontend directory):

   ```sh

```sh   npm start

cd frontend   ```

npm run build

```---

**Note:**

Or skip this and use `npm start` in step 4b for development.- Ensure all ports are free before starting (4200, 8080, 9096, etc).

- Use `tail <logfile>` to check logs for errors (e.g., `tail -20 backend.log`).

---- The default UI is at http://localhost:4200.


## 3. Stop All Running Services

```sh
pkill -f "config-service|file-service|workflow-compiling|computing-unit|access-control|texera-web-application" 2>/dev/null
pkill -f "ng serve|y-websocket" 2>/dev/null
```

Verify nothing is lingering on key ports:

```sh
lsof -i :8080 -i :8085 -i :9094 -i :9096 -i :2552 -i :4200 -i :1234 2>/dev/null | grep LISTEN
```

## 4. Start All Services

Run every command from the workspace root.

### 4a. Backend services (start in this order)

```sh
cd /Users/prateekganigi/Desktop/Texera/texera

# 1. config-service
bin/config-service.sh > config-service.log 2>&1 &

# 2. file-service
bin/file-service.sh > file-service.log 2>&1 &

# 3. workflow-compiling-service
bin/workflow-compiling-service.sh > workflow-compiling.log 2>&1 &

# 4. computing-unit-managing-service
bin/computing-unit-managing-service.sh > computing-unit-service.log 2>&1 &

# 5. access-control-service
target/access-control-service-1.0.0/bin/access-control-service > access-control-service.log 2>&1 &

# 6. texera-web-application (main REST API, port 8080)
(cd amber && target/amber-1.0.0/bin/texera-web-application > ../backend.log 2>&1 &)

# 7. computing-unit-master (workflow websocket, port 8085 + Pekko port 2552)
#    NOTE: Do NOT use bin/workflow-computing-unit.sh — it has a glob bug.
#    Start directly instead:
(cd amber && target/amber-1.0.0/bin/computing-unit-master > ../computing-unit.log 2>&1 &)
```

### 4b. Frontend dev server (includes y-websocket)

```sh
cd frontend
npm start
```

This runs concurrently: **y-websocket** (port 1234) + **ng serve** (port 4200).

## 5. Verify Everything Is Running

```sh
lsof -i :9094 -i :8080 -i :8085 -i :8888 -i :9096 -i :2552 -i :4200 -i :1234 2>/dev/null | grep LISTEN
```

You should see **8 LISTEN entries**. Quick API smoke test:

```sh
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/auth/google/clientid
# Expected: 200

curl -s -o /dev/null -w "%{http_code}" http://localhost:4200/
# Expected: 200
```

Open the UI at **http://localhost:4200**.

---

## Troubleshooting

| Problem | Fix |
| ------- | --- |
| `Address already in use` on port 2552 | A previous `computing-unit-master` is still running. Find it with `lsof -i :2552` then `kill <PID>`. |
| `bin/workflow-computing-unit.sh` exits 127 | Known glob bug — the script looks for `target/texera-*/bin/computing-unit-master` but the actual path is `amber/target/amber-1.0.0/bin/computing-unit-master`. Use the direct command from step 4a instead. |
| Frontend proxy errors (ECONNREFUSED) | The target backend service isn't running. Check `frontend/proxy.config.json` for port mappings and ensure that service is up. |
| `npm start` fails in wrong directory | Must be run from `frontend/`, not the workspace root. |
| Services die silently | Check the corresponding `.log` file in the workspace root (e.g., `tail -50 backend.log`). |

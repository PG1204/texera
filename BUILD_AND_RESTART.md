# Texera Build & Restart Instructions

## Backend Build

1. Open a terminal in the workspace root:
   ```sh
   cd /Users/prateekganigi/Desktop/Texera/texera
   ```
2. Build all backend services:
   ```sh
   sbt clean dist
   ```
3. Unzip all service packages:
   ```sh
   for svc in workflow-compiling-service file-service computing-unit-managing-service config-service access-control-service; do unzip -o ${svc}/target/universal/${svc}-*.zip -d target/; done
   unzip -o amber/target/universal/amber-*.zip -d amber/target/
   ```

## Frontend Build

1. Open a terminal in the frontend directory:
   ```sh
   cd /Users/prateekganigi/Desktop/Texera/texera/frontend
   ```
2. Build the Angular frontend:
   ```sh
   ng build
   ```

## Restart All Servers & Services

1. Stop any running backend services:
   ```sh
   pkill -f "config-service|file-service|workflow-compiling-service|computing-unit-managing-service|computing-unit-master|access-control-service|texera-web-application"
   ```
2. Start backend services (from workspace root):
   ```sh
   source .dev-env.sh
   bin/config-service.sh &
   bin/file-service.sh &
   bin/workflow-compiling-service.sh &
   bin/computing-unit-managing-service.sh &
   bin/workflow-computing-unit.sh &
   target/access-control-service-*/bin/access-control-service &
   cd amber && target/amber-*/bin/texera-web-application &
   ```
3. Start frontend dev server (from frontend directory):
   ```sh
   npm start
   ```

---
**Note:**
- Ensure all ports are free before starting (4200, 8080, 9096, etc).
- Use `tail <logfile>` to check logs for errors (e.g., `tail -20 backend.log`).
- The default UI is at http://localhost:4200.

#!/bin/bash
# Dev environment variables for connecting to single-node Docker storage
export STORAGE_JDBC_URL="jdbc:postgresql://localhost:5433/texera_db?currentSchema=texera_db,public"
export STORAGE_JDBC_USERNAME="texera"
export STORAGE_JDBC_PASSWORD="password"
export STORAGE_S3_ENDPOINT="http://localhost:9002"
export STORAGE_LAKEFS_ENDPOINT="http://localhost:8000/api/v1"
export STORAGE_ICEBERG_CATALOG_POSTGRES_URI_WITHOUT_SCHEME="localhost:5433/texera_iceberg_catalog"
export STORAGE_ICEBERG_CATALOG_POSTGRES_USERNAME="texera"
export STORAGE_ICEBERG_CATALOG_POSTGRES_PASSWORD="password"
export FILE_SERVICE_GET_PRESIGNED_URL_ENDPOINT="http://localhost:9092/api/dataset/presign-download"
export FILE_SERVICE_UPLOAD_ONE_FILE_TO_DATASET_ENDPOINT="http://localhost:9092/api/dataset/did/upload"
export UDF_PYTHON_PATH="/opt/homebrew/bin/python3.11"

# JVM flags needed for Java 17+ (Arrow, Ehcache, etc.)
export JAVA_OPTS="--add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED"

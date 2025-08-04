@echo off
echo 🚀 DEPLOY COMPLETO DE MICROSERVICIOS EN WINDOWS
echo ==============================================

cd /d "c:\Users\Elkin Andres\Desktop\KUBER"

echo.
echo 📦 PASO 1: Verificando herramientas necesarias...
echo ================================================

docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker no está instalado o no está en el PATH
    pause
    exit /b 1
)

kubectl version --client >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ kubectl no está instalado o no está en el PATH
    pause
    exit /b 1
)

mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven no está instalado o no está en el PATH
    pause
    exit /b 1
)

echo ✅ Todas las herramientas están disponibles

echo.
echo 📦 PASO 2: Compilando todos los JAR files...
echo ============================================

call :compile_service "ms-eureka-server"
call :compile_service "ms-api-gateway"
call :compile_service "authservice"
call :compile_service "ms-publicaciones"
call :compile_service "ms_notificaciones"
call :compile_service "ms-catalogo"
call :compile_service "sincronizacion"

echo.
echo 🐳 PASO 3: Construyendo imágenes Docker...
echo ==========================================

call :build_image "ms-eureka-server" "ms-eureka-server"
call :build_image "ms-api-gateway" "ms-api-gateway"
call :build_image "authservice" "authservice"
call :build_image "ms-publicaciones" "ms-publicaciones"
call :build_image "ms_notificaciones" "ms-notificaciones"
call :build_image "ms-catalogo" "ms-catalogo"
call :build_image "sincronizacion" "sincronizacion"

echo.
echo ☸️ PASO 4: Desplegando en Kubernetes...
echo ======================================

echo 1️⃣ Creando namespace...
kubectl apply -f k8s\00-namespace.yaml
timeout /t 5 /nobreak > nul

echo 2️⃣ Desplegando PostgreSQL...
kubectl apply -f k8s\01-postgresql.yaml
echo ⏳ Esperando PostgreSQL...
kubectl wait --for=condition=ready pod -l app=postgresql -n microservicios --timeout=300s

echo 3️⃣ Desplegando RabbitMQ...
kubectl apply -f k8s\02-rabbitmq.yaml
echo ⏳ Esperando RabbitMQ...
kubectl wait --for=condition=ready pod -l app=rabbitmq -n microservicios --timeout=300s

echo 4️⃣ Desplegando Eureka Server...
kubectl apply -f k8s\03-eureka-server.yaml
echo ⏳ Esperando Eureka Server...
kubectl wait --for=condition=ready pod -l app=eureka-server -n microservicios --timeout=300s

echo 5️⃣ Desplegando Auth Service...
kubectl apply -f k8s\04-auth-service.yaml
timeout /t 30 /nobreak > nul

echo 6️⃣ Desplegando Publicaciones Service...
kubectl apply -f k8s\05-publicaciones-service.yaml
timeout /t 30 /nobreak > nul

echo 7️⃣ Desplegando Notificaciones Service...
kubectl apply -f k8s\06-notificaciones-service.yaml
timeout /t 30 /nobreak > nul

echo 8️⃣ Desplegando Catálogo Service...
kubectl apply -f k8s\07-catalogo-service.yaml
timeout /t 30 /nobreak > nul

echo 9️⃣ Desplegando Sincronización Service...
kubectl apply -f k8s\08-sincronizacion-service.yaml
timeout /t 30 /nobreak > nul

echo 🔟 Desplegando API Gateway...
kubectl apply -f k8s\09-api-gateway.yaml
echo ⏳ Esperando API Gateway...
kubectl wait --for=condition=ready pod -l app=api-gateway -n microservicios --timeout=300s

echo.
echo 🎉 ¡DEPLOY COMPLETADO EXITOSAMENTE!
echo ==================================
echo.
echo 📊 Estado final de los pods:
kubectl get pods -n microservicios
echo.
echo 🌐 Servicios disponibles:
kubectl get services -n microservicios
echo.
echo 🚀 Comandos para acceder a los servicios:
echo API Gateway:    kubectl port-forward service/gateway-service 8000:8000 -n microservicios
echo Eureka Server:  kubectl port-forward service/eureka-service 8761:8761 -n microservicios
echo RabbitMQ UI:    kubectl port-forward service/rabbitmq-service 15672:15672 -n microservicios
echo PostgreSQL:     kubectl port-forward service/postgresql-service 5432:5432 -n microservicios
echo.
pause
goto :eof

:compile_service
echo 🔨 Compilando %~1...
cd "%~1"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ❌ Error compilando %~1
    pause
    exit /b 1
)
cd ..
echo ✅ %~1 compilado exitosamente
goto :eof

:build_image
echo 🏗️ Construyendo imagen %~2...
docker build -t "%~2:latest" "./%~1"
if %errorlevel% neq 0 (
    echo ❌ Error construyendo imagen %~2
    pause
    exit /b 1
)
echo ✅ Imagen %~2 construida exitosamente
goto :eof
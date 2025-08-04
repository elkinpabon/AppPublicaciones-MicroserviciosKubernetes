@echo off
echo CONSTRUYENDO IMAGENES DIRECTAMENTE EN MINIKUBE
echo =============================================

cd /d "%~dp0\.."

echo Configurando Docker para usar minikube...
@FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env --shell cmd') DO @%%i

echo.
echo Verificando conexion a Docker de minikube...
docker version

echo.
echo Construyendo imagenes en minikube...

echo [1/7] Construyendo ms-eureka-server...
docker build -t ms-eureka-server:latest ./ms-eureka-server

echo [2/7] Construyendo authservice...
if exist "authservice" docker build -t authservice:latest ./authservice

echo [3/7] Construyendo ms-api-gateway...
if exist "ms-api-gateway" docker build -t ms-api-gateway:latest ./ms-api-gateway

echo [4/7] Construyendo ms-publicaciones...
if exist "ms-publicaciones" docker build -t ms-publicaciones:latest ./ms-publicaciones

echo [5/7] Construyendo ms-notificaciones...
if exist "ms_notificaciones" docker build -t ms-notificaciones:latest ./ms_notificaciones

echo [6/7] Construyendo ms-catalogo...
if exist "ms-catalogo" docker build -t ms-catalogo:latest ./ms-catalogo

echo [7/7] Construyendo sincronizacion...
if exist "sincronizacion" docker build -t sincronizacion:latest ./sincronizacion

echo.
echo Verificando imagenes en minikube...
docker images | findstr -E "(eureka|auth|gateway|publicaciones|notificaciones|catalogo|sincronizacion)"

echo.
echo Reiniciando deployments...
kubectl rollout restart deployment eureka-server -n microservicios
kubectl rollout restart deployment auth-service -n microservicios
kubectl rollout restart deployment publicaciones-service -n microservicios
kubectl rollout restart deployment notificaciones-service -n microservicios
kubectl rollout restart deployment catalogo-service -n microservicios
kubectl rollout restart deployment sincronizacion-service -n microservicios
kubectl rollout restart deployment api-gateway -n microservicios

echo.
echo Esperando que los pods se actualicen...
timeout /t 90

echo.
echo Estado final:
kubectl get pods -n microservicios

pause
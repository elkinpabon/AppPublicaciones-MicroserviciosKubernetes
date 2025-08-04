@echo off
echo RECONSTRUYENDO TODAS LAS IMAGENES DOCKER
echo ========================================

cd /d "%~dp0\.."

echo Limpiando imagenes anteriores...
docker rmi ms-eureka-server:latest 2>nul
docker rmi authservice:latest 2>nul
docker rmi ms-api-gateway:latest 2>nul
docker rmi ms-publicaciones:latest 2>nul
docker rmi ms-notificaciones:latest 2>nul
docker rmi ms-catalogo:latest 2>nul
docker rmi sincronizacion:latest 2>nul

echo.
echo Reconstruyendo imagenes desde cero...

echo [1/7] Construyendo ms-eureka-server...
docker build -t ms-eureka-server:latest ./ms-eureka-server --no-cache
if %errorlevel% neq 0 (
    echo ERROR: Fallo construccion de ms-eureka-server
    pause
    exit /b 1
)

echo [2/7] Construyendo authservice...
if exist "authservice" (
    docker build -t authservice:latest ./authservice --no-cache
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de authservice
        pause
        exit /b 1
    )
)

echo [3/7] Construyendo ms-api-gateway...
if exist "ms-api-gateway" (
    docker build -t ms-api-gateway:latest ./ms-api-gateway --no-cache
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-api-gateway
        pause
        exit /b 1
    )
)

echo [4/7] Construyendo ms-publicaciones...
if exist "ms-publicaciones" (
    docker build -t ms-publicaciones:latest ./ms-publicaciones --no-cache
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-publicaciones
        pause
        exit /b 1
    )
)

echo [5/7] Construyendo ms-notificaciones...
if exist "ms_notificaciones" (
    docker build -t ms-notificaciones:latest ./ms_notificaciones --no-cache
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-notificaciones
        pause
        exit /b 1
    )
)

echo [6/7] Construyendo ms-catalogo...
if exist "ms-catalogo" (
    docker build -t ms-catalogo:latest ./ms-catalogo --no-cache
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-catalogo
        pause
        exit /b 1
    )
)

echo [7/7] Construyendo sincronizacion...
if exist "sincronizacion" (
    docker build -t sincronizacion:latest ./sincronizacion --no-cache
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de sincronizacion
        pause
        exit /b 1
    )
)

echo.
echo IMAGENES CONSTRUIDAS EXITOSAMENTE!
echo ==================================
echo.
echo Verificando imagenes creadas:
docker images | findstr -E "(eureka|auth|gateway|publicaciones|notificaciones|catalogo|sincronizacion)"

echo.
echo Ahora reiniciando deployments en Kubernetes...
kubectl rollout restart deployment eureka-server -n microservicios
kubectl rollout restart deployment auth-service -n microservicios  
kubectl rollout restart deployment publicaciones-service -n microservicios
kubectl rollout restart deployment notificaciones-service -n microservicios
kubectl rollout restart deployment catalogo-service -n microservicios
kubectl rollout restart deployment sincronizacion-service -n microservicios
kubectl rollout restart deployment api-gateway -n microservicios

echo.
echo Esperando que los pods se actualicen (2 minutos)...
timeout /t 120

echo.
echo Estado final de los pods:
kubectl get pods -n microservicios

echo.
echo Proceso completado!
pause
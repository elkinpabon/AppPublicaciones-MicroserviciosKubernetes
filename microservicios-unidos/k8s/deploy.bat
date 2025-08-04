@echo off
echo DEPLOY COMPLETO DE MICROSERVICIOS EN KUBERNETES
echo ================================================

:: Cambiar al directorio del script y luego subir un nivel
cd /d "%~dp0"
cd ..

echo.
echo PASO 1: Verificando herramientas...
echo ===================================
docker version
if %errorlevel% neq 0 (
    echo ERROR: Docker no funciona
    goto :error
)

kubectl version --client
if %errorlevel% neq 0 (
    echo ERROR: kubectl no funciona
    goto :error
)

echo EXITO: Todas las herramientas disponibles

echo.
echo PASO 2: Compilando microservicios...
echo ===================================

:: ms-eureka-server
echo [1/7] Compilando ms-eureka-server...
if exist "ms-eureka-server" (
    cd ms-eureka-server
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-eureka-server
        goto :error
    )
    cd ..
    echo EXITO: ms-eureka-server compilado
) else (
    echo ERROR: Directorio ms-eureka-server no encontrado
    goto :error
)

:: authservice
echo [2/7] Compilando authservice...
if exist "authservice" (
    cd authservice
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de authservice
        goto :error
    )
    cd ..
    echo EXITO: authservice compilado
) else (
    echo ADVERTENCIA: authservice no encontrado, saltando...
)

:: ms-api-gateway
echo [3/7] Compilando ms-api-gateway...
if exist "ms-api-gateway" (
    cd ms-api-gateway
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-api-gateway
        goto :error
    )
    cd ..
    echo EXITO: ms-api-gateway compilado
) else (
    echo ADVERTENCIA: ms-api-gateway no encontrado, saltando...
)

:: ms-publicaciones
echo [4/7] Compilando ms-publicaciones...
if exist "ms-publicaciones" (
    cd ms-publicaciones
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-publicaciones
        goto :error
    )
    cd ..
    echo EXITO: ms-publicaciones compilado
) else (
    echo ADVERTENCIA: ms-publicaciones no encontrado, saltando...
)

:: ms_notificaciones
echo [5/7] Compilando ms_notificaciones...
if exist "ms_notificaciones" (
    cd ms_notificaciones
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms_notificaciones
        goto :error
    )
    cd ..
    echo EXITO: ms_notificaciones compilado
) else (
    echo ADVERTENCIA: ms_notificaciones no encontrado, saltando...
)

:: ms-catalogo
echo [6/7] Compilando ms-catalogo...
if exist "ms-catalogo" (
    cd ms-catalogo
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de ms-catalogo
        goto :error
    )
    cd ..
    echo EXITO: ms-catalogo compilado
) else (
    echo ADVERTENCIA: ms-catalogo no encontrado, saltando...
)

:: sincronizacion
echo [7/7] Compilando sincronizacion...
if exist "sincronizacion" (
    cd sincronizacion
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo ERROR: Fallo la compilacion de sincronizacion
        goto :error
    )
    cd ..
    echo EXITO: sincronizacion compilado
) else (
    echo ADVERTENCIA: sincronizacion no encontrado, saltando...
)

echo EXITO: Compilacion de microservicios completada

echo.
echo PASO 3: Construyendo imagenes Docker...
echo =======================================

echo [1/7] Construyendo ms-eureka-server...
docker build -t ms-eureka-server:latest ./ms-eureka-server
if %errorlevel% neq 0 (
    echo ERROR: Fallo construccion de ms-eureka-server
    goto :error
)
echo EXITO: Imagen ms-eureka-server creada

if exist "authservice" (
    echo [2/7] Construyendo authservice...
    docker build -t authservice:latest ./authservice
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de authservice
        goto :error
    )
    echo EXITO: Imagen authservice creada
)

if exist "ms-api-gateway" (
    echo [3/7] Construyendo ms-api-gateway...
    docker build -t ms-api-gateway:latest ./ms-api-gateway
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-api-gateway
        goto :error
    )
    echo EXITO: Imagen ms-api-gateway creada
)

if exist "ms-publicaciones" (
    echo [4/7] Construyendo ms-publicaciones...
    docker build -t ms-publicaciones:latest ./ms-publicaciones
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-publicaciones
        goto :error
    )
    echo EXITO: Imagen ms-publicaciones creada
)

if exist "ms_notificaciones" (
    echo [5/7] Construyendo ms-notificaciones...
    docker build -t ms-notificaciones:latest ./ms_notificaciones
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-notificaciones
        goto :error
    )
    echo EXITO: Imagen ms-notificaciones creada
)

if exist "ms-catalogo" (
    echo [6/7] Construyendo ms-catalogo...
    docker build -t ms-catalogo:latest ./ms-catalogo
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de ms-catalogo
        goto :error
    )
    echo EXITO: Imagen ms-catalogo creada
)

if exist "sincronizacion" (
    echo [7/7] Construyendo sincronizacion...
    docker build -t sincronizacion:latest ./sincronizacion
    if %errorlevel% neq 0 (
        echo ERROR: Fallo construccion de sincronizacion
        goto :error
    )
    echo EXITO: Imagen sincronizacion creada
)

echo EXITO: Imagenes Docker completadas

echo.
echo PASO 4: Desplegando en Kubernetes...
echo ===================================

echo [1/10] Creando namespace microservicios...
kubectl apply -f k8s\00-namespace.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al crear namespace
    goto :error
)
echo EXITO: Namespace creado
timeout /t 5 /nobreak > nul

echo [2/10] Desplegando PostgreSQL...
kubectl apply -f k8s\01-postgresql.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar PostgreSQL
    goto :error
)
echo Esperando que PostgreSQL este listo (hasta 5 minutos)...
kubectl wait --for=condition=ready pod -l app=postgresql -n microservicios --timeout=300s
if %errorlevel% neq 0 (
    echo ADVERTENCIA: PostgreSQL tomo mas tiempo del esperado, verificando estado...
    kubectl get pods -l app=postgresql -n microservicios
) else (
    echo EXITO: PostgreSQL esta listo
)

echo [3/10] Desplegando RabbitMQ...
kubectl apply -f k8s\02-rabbitmq.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar RabbitMQ
    goto :error
)
echo Esperando que RabbitMQ este listo (hasta 5 minutos)...
kubectl wait --for=condition=ready pod -l app=rabbitmq -n microservicios --timeout=300s
if %errorlevel% neq 0 (
    echo ADVERTENCIA: RabbitMQ tomo mas tiempo del esperado, verificando estado...
    kubectl get pods -l app=rabbitmq -n microservicios
) else (
    echo EXITO: RabbitMQ esta listo
)

echo [4/10] Desplegando Eureka Server...
kubectl apply -f k8s\03-eureka-server.yaml
if %errorlevel% neq 0 (
    echo ERROR: Fallo al desplegar Eureka Server
    goto :error
)
echo Esperando que Eureka este listo (hasta 5 minutos)...
kubectl wait --for=condition=ready pod -l app=eureka-server -n microservicios --timeout=300s
if %errorlevel% neq 0 (
    echo ADVERTENCIA: Eureka tomo mas tiempo del esperado, verificando estado...
    kubectl get pods -l app=eureka-server -n microservicios
) else (
    echo EXITO: Eureka Server esta listo
)

echo [5/10] Desplegando Auth Service...
if exist "k8s\04-auth-service.yaml" (
    kubectl apply -f k8s\04-auth-service.yaml
    if %errorlevel% neq 0 (
        echo ERROR: Fallo al desplegar Auth Service
        goto :error
    )
    echo EXITO: Auth Service desplegado
    timeout /t 30 /nobreak > nul
) else (
    echo ADVERTENCIA: k8s\04-auth-service.yaml no encontrado
)

echo [6/10] Desplegando Publicaciones Service...
if exist "k8s\05-publicaciones-service.yaml" (
    kubectl apply -f k8s\05-publicaciones-service.yaml
    if %errorlevel% neq 0 (
        echo ERROR: Fallo al desplegar Publicaciones
        goto :error
    )
    echo EXITO: Publicaciones Service desplegado
    timeout /t 30 /nobreak > nul
) else (
    echo ADVERTENCIA: k8s\05-publicaciones-service.yaml no encontrado
)

echo [7/10] Desplegando Notificaciones Service...
if exist "k8s\06-notificaciones-service.yaml" (
    kubectl apply -f k8s\06-notificaciones-service.yaml
    if %errorlevel% neq 0 (
        echo ERROR: Fallo al desplegar Notificaciones
        goto :error
    )
    echo EXITO: Notificaciones Service desplegado
    timeout /t 30 /nobreak > nul
) else (
    echo ADVERTENCIA: k8s\06-notificaciones-service.yaml no encontrado
)

echo [8/10] Desplegando Catalogo Service...
if exist "k8s\07-catalogo-service.yaml" (
    kubectl apply -f k8s\07-catalogo-service.yaml
    if %errorlevel% neq 0 (
        echo ERROR: Fallo al desplegar Catalogo
        goto :error
    )
    echo EXITO: Catalogo Service desplegado
    timeout /t 30 /nobreak > nul
) else (
    echo ADVERTENCIA: k8s\07-catalogo-service.yaml no encontrado
)

echo [9/10] Desplegando Sincronizacion Service...
if exist "k8s\08-sincronizacion-service.yaml" (
    kubectl apply -f k8s\08-sincronizacion-service.yaml
    if %errorlevel% neq 0 (
        echo ERROR: Fallo al desplegar Sincronizacion
        goto :error
    )
    echo EXITO: Sincronizacion Service desplegado
    timeout /t 30 /nobreak > nul
) else (
    echo ADVERTENCIA: k8s\08-sincronizacion-service.yaml no encontrado
)

echo [10/10] Desplegando API Gateway...
if exist "k8s\09-api-gateway.yaml" (
    kubectl apply -f k8s\09-api-gateway.yaml
    if %errorlevel% neq 0 (
        echo ERROR: Fallo al desplegar API Gateway
        goto :error
    )
    echo EXITO: API Gateway desplegado
    timeout /t 30 /nobreak > nul
) else (
    echo ADVERTENCIA: k8s\09-api-gateway.yaml no encontrado
)

echo.
echo =========================================
echo DEPLOY COMPLETADO EXITOSAMENTE!
echo =========================================
echo.
echo Esperando 30 segundos para que todos los servicios terminen de arrancar...
timeout /t 30 /nobreak > nul

echo Estado final de los pods:
kubectl get pods -n microservicios
echo.
echo Servicios disponibles:
kubectl get services -n microservicios
echo.
echo =========================================
echo COMANDOS PARA ACCEDER A LOS SERVICIOS:
echo =========================================
echo.
echo Para Eureka Server:
echo kubectl port-forward service/eureka-service 8761:8761 -n microservicios
echo.
echo Para RabbitMQ Management:
echo kubectl port-forward service/rabbitmq-service 15672:15672 -n microservicios
echo.
echo Para API Gateway:
echo kubectl port-forward service/gateway-service 8000:8000 -n microservicios
echo.
echo =========================================
echo URLS DE ACCESO:
echo =========================================
echo Eureka Server: http://localhost:8761
echo RabbitMQ Management: http://localhost:15672 (usuario: guest, clave: guest)
echo API Gateway: http://localhost:8000
echo.
echo =========================================
echo DEPLOY COMPLETADO - El script terminara en 10 segundos
echo =========================================
timeout /t 10 /nobreak > nul
goto :end

:error
echo.
echo =========================================
echo ERROR EN EL DEPLOY
echo =========================================
echo El script se detendra en 30 segundos para revisar el error
timeout /t 30 /nobreak > nul
exit /b 1

:end
echo Script finalizado exitosamente
exit /b 0
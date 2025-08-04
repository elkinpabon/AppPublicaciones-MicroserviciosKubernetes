@echo off
echo APLICANDO BASES DE DATOS SEPARADAS
echo =================================

echo [1/6] Deteniendo todos los microservicios...
kubectl delete deployment postgresql authservice publicaciones-service notificaciones-service catalogo-service -n microservicios 2>nul

echo [2/6] Aplicando PostgreSQL con multiples BDs...
kubectl apply -f 01-postgresql.yaml

echo [3/6] Esperando que PostgreSQL este listo...
timeout /t 60

echo [4/6] Aplicando microservicios con BDs separadas...
kubectl apply -f 04-auth-service.yaml
kubectl apply -f 05-publicaciones-service.yaml
kubectl apply -f 06-notificaciones-service.yaml
kubectl apply -f 07-catalogo-service.yaml

echo [5/6] Esperando que todos esten listos...
timeout /t 90

echo [6/6] Verificando bases de datos creadas...
kubectl exec -it deployment/postgresql -n microservicios -- psql -U postgres -l

echo.
echo =========================================
echo BASES DE DATOS SEPARADAS CONFIGURADAS
echo =========================================
echo - db_auth (AuthService)
echo - db_publicaciones (Publicaciones)
echo - db_notificaciones (Notificaciones)  
echo - db_catalogo (Catalogo)
echo =========================================
pause
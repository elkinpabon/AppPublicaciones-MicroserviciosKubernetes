# 📚 AppPublicaciones - Plataforma de Microservicios en Kubernetes

AppPublicaciones es una plataforma distribuida para la gestión de publicaciones académicas, construida con 7 microservicios Spring Boot, PostgreSQL, RabbitMQ y un frontend en React. Todo el sistema está preparado para despliegue automatizado en Kubernetes.

---

## 🚀 Características principales

- **7 microservicios independientes**: Eureka, Auth, Gateway, Publicaciones, Catálogo, Notificaciones, Sincronización.
- **Bases de datos PostgreSQL separadas** para cada servicio.
- **Mensajería asíncrona** con RabbitMQ.
- **API Gateway** con Spring Cloud Gateway.
- **Service Discovery** con Eureka.
- **Frontend en React** (carpeta `microservicios-frontend`).
- **Despliegue automatizado** con scripts batch y YAML de Kubernetes.
- **Ingress** para acceso web con dominio local.

---

## 📦 Estructura del proyecto

```
microservicios-unidos/
│
├── authservice/
├── ms-api-gateway/
├── ms-catalogo/
├── ms-eureka-server/
├── ms-notificaciones/
├── ms-publicaciones/
├── sincronizacion/
├── microservicios-frontend/
├── k8s/
│   ├── 00-namespace.yaml
│   ├── 01-postgresql.yaml
│   ├── 02-rabbitmq.yaml
│   ├── 03-eureka-server.yaml
│   ├── 04-auth-service.yaml
│   ├── 05-publicaciones-service.yaml
│   ├── 06-notificaciones-service.yaml
│   ├── 07-catalogo-service.yaml
│   ├── 08-sincronizacion-service.yaml
│   ├── 09-api-gateway.yaml
│   ├── 10-ingress.yaml
│   └── deploy.bat
└── pom.xml
```

---

## ⚙️ Requisitos previos

- [Docker](https://www.docker.com/)
- [Minikube](https://minikube.sigs.k8s.io/docs/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- (Opcional) [Node.js y npm](https://nodejs.org/) para el frontend

---

## 🛠️ Despliegue paso a paso

### 1. Clona el repositorio

```bash
git clone https://github.com/tuusuario/app-publicaciones.git
cd app-publicaciones/microservicios-unidos
```

### 2. Inicia Minikube y habilita Ingress

```bash
minikube start
minikube addons enable ingress
minikube addons enable ingress-dns
```

### 3. Compila y construye los microservicios

```bash
cd k8s
deploy.bat
```
> El script compila cada microservicio, construye las imágenes Docker y las deja listas para Kubernetes.

### 4. Aplica los manifiestos de Kubernetes

```bash
kubectl apply -f 00-namespace.yaml
kubectl apply -f 01-postgresql.yaml
kubectl apply -f 02-rabbitmq.yaml
kubectl apply -f 03-eureka-server.yaml
kubectl apply -f 04-auth-service.yaml
kubectl apply -f 05-publicaciones-service.yaml
kubectl apply -f 06-notificaciones-service.yaml
kubectl apply -f 07-catalogo-service.yaml
kubectl apply -f 08-sincronizacion-service.yaml
kubectl apply -f 09-api-gateway.yaml
kubectl apply -f 10-ingress.yaml
```

### 5. Espera a que todos los pods estén en estado `Running`

```bash
kubectl get pods -n microservicios
kubectl get pods -n ingress-nginx
```

### 6. Configura el archivo hosts

Edita `C:\Windows\System32\drivers\etc\hosts` y agrega:

```
127.0.0.1 app-publicaciones.local
```

### 7. Ejecuta el tunnel de Minikube (en CMD como administrador)

```bash
minikube tunnel
```
> Mantén esta ventana abierta mientras usas la app.

### 8. Accede a la plataforma

Abre en tu navegador:
```
http://app-publicaciones.local/api/publicaciones/api/libros
```
O prueba otros endpoints según los servicios.

---

## 🧪 Pruebas rápidas

- **API Gateway:** http://app-publicaciones.local/api
- **Eureka Dashboard:** http://localhost:8761 (usando port-forward)
- **Frontend React:** http://localhost:3000 (si ejecutas el frontend)

---

## 📝 Notas

- Puedes usar `kubectl port-forward` para exponer servicios internos, por ejemplo:
  ```bash
  kubectl port-forward service/api-gateway-svc 8000:8000 -n microservicios
  kubectl port-forward service/eureka-server-svc 8761:8761 -n microservicios
  ```
- El frontend se encuentra en la carpeta `microservicios-frontend` y se ejecuta con `npm install && npm start`.

---

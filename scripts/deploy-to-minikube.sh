#!/bin/bash

set -e

echo "Switching to Minikube's Docker daemon..."
eval "$(minikube docker-env)"

for APP in publisher listener; do
  IMAGE_NAME="${APP}:latest"

  echo "Cleaning old image from Minikube for $APP..."
  minikube ssh -- docker rmi -f "$IMAGE_NAME" || echo "No existing image to remove"

  echo "Compiling Java code for $APP..."
  (cd "$APP" && ./mvnw clean package -DskipTests)

  echo "Building Docker image for $APP..."
  docker build -t "$IMAGE_NAME" "./$APP"

#   echo "Loading $APP image into Minikube..."
#   minikube image load "$IMAGE_NAME"
done

echo "Applying Kubernetes manifests..."
kubectl apply -f k8s/sa.yaml
kubectl apply -f k8s/listener-deployment.yaml
kubectl apply -f k8s/publisher-deployment.yaml

echo "Restarting deployments..."
kubectl rollout restart deployment/listener
kubectl rollout restart deployment/publisher

echo "Done deploying!"

# this is mainly to just not mess with my local docker setup. if there's any better way then great otherwise this works just as fine
if [[ "$1" == "--reset" ]]; then
  echo "↩ Resetting Docker to system default..."
  eval "$(minikube docker-env -u)"
  echo "Docker env reset."
fi

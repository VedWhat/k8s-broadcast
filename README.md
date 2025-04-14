# Kubernetes Pod Broadcaster

This repo demonstrates a Kubernetes-native fan-out pattern using Java Spring Boot.

Instead of relying on a queue or external messaging system, this setup uses the Kubernetes API to dynamically discover all pods of a service (`listener`) and send synchronous HTTP requests to each from another service (`publisher`).

---

## Why

Sometimes you just want to broadcast a message to **all pods** behind a Kubernetes service — not just one. But a typical Kubernetes `Service` load-balances across pods, which isn't useful when you want to collect a response from each replica.

This project:
- Uses the Kubernetes API to list pod IPs
- Sends direct requests to each pod's `/metrics` endpoint
- Aggregates responses in the `publisher`

No message queues, no brokers. Just one `curl` to fan out to the whole fleet.

---

## Project Structure

```
.
├── k8s/             # Kubernetes deployment and service YAMLs
├── listener/        # The app that responds to requests
│   └── /metrics
├── publisher/       # The app that discovers and calls listener pods
│   └── /broadcast
├── scripts/       # hacky scripts to deploy stuff
│   └── /deploy-to-minikube.sh
```

---

## Quickstart (Kubernetes)

```bash
# Build & deploy
./scripts/deploy-to-minikube.sh

# Port-forward publisher
kubectl port-forward deployment/publisher 8081:8081

# Hit broadcast endpoint
curl http://localhost:8081/broadcastv2
```

Optional filters:
```
/broadcast?label=app=listener&label=zone=us-east
```

---

## Running Locally Without Kubernetes

You can also run both apps locally for development/testing without Kubernetes:

```bash
# In one terminal: run the listener
cd listener
./mvnw spring-boot:run
```

```bash
# In another terminal: run the publisher
cd publisher
./mvnw spring-boot:run
```

By default:
- Listener will run on `localhost:8080`
- Publisher will run on `localhost:8081`

You can then test with static IPs or mock the Kubernetes API within the publisher if needed.

Note: Since there's no pod discovery outside Kubernetes, you can hardcode IPs or use hosts file mappings for testing the broadcast logic. I have not tried this yet though

---

## Sample Response

```json
[
  { "pod": "listener-x4f8s", "memoryMB": 91.2 },
  { "pod": "listener-a9v4h", "memoryMB": 93.7 },
  { "pod": "listener-n4g0k", "memoryMB": 90.5 }
]
```

---

## Chaos Mode - WIP

This project also includes an optional "chaos mode" to simulate pod-level failures, timeouts, and odd responses. This can be useful for testing fault tolerance in synchronous request systems.


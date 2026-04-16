# Java TCP Chat Application

A real-time command-line chat system built with:
- **TCP Sockets** for client-server communication
- **Multi-threading** — one thread per client on the server; separate send/receive threads on the client
- **BlockingQueue** for decoupled message passing inside the server

---

## Project Structure

```
chat-app/
├── src/
│   ├── server/
│   │   ├── ChatServer.java      ← Server entry point
│   │   └── ClientHandler.java   ← Per-client thread + message queue
│   └── client/
│       ├── ChatClient.java      ← Client entry point
│       ├── MessageSender.java   ← Reads console → sends to server
│       └── MessageReceiver.java ← Receives from server → prints
└── README.md
```

---

## Build

```bash
# From the project root
mkdir -p out

javac -d out src/server/ChatServer.java src/server/ClientHandler.java
javac -d out src/client/ChatClient.java src/client/MessageSender.java src/client/MessageReceiver.java
```

---

## Run

**Terminal 1 — start the server:**
```bash
java -cp out server.ChatServer
```

**Terminal 2 (and 3, 4…) — start a client:**
```bash
java -cp out client.ChatClient
```

Type messages and press Enter. Type `exit` to disconnect.

---

## How It Works

```
CLIENT                          SERVER
──────                          ──────
ChatClient
  │
  ├─ MessageReceiver (thread) ──────────── reads from socket InputStream
  │                                              │
  └─ MessageSender   (main)   ──────────── writes to socket OutputStream
                                                 │
                                         ClientHandler (thread per client)
                                           │
                                           ├─ Reader loop  → puts into BlockingQueue
                                           └─ Sender thread ← takes from BlockingQueue → writes to client
```

### Threading model

| Component | Thread | Role |
|---|---|---|
| `ChatServer.main` | Main | Accepts new connections, submits to thread pool |
| `ClientHandler.run` | Pool thread | Reads messages from the client |
| Sender inside `ClientHandler` | Daemon thread | Drains queue → writes responses |
| `ChatClient.start` | Main | Starts receiver thread, then runs sender |
| `MessageReceiver.run` | Daemon thread | Prints server messages to console |
| `MessageSender.start` | Main | Reads console input → sends to server |

### Why BlockingQueue?

The `LinkedBlockingQueue` in `ClientHandler` separates reading from writing:
- The reader loop never stalls waiting for a write to complete
- If the client is slow, messages buffer in the queue instead of blocking the reader
- Thread-safe by design — no `synchronized` needed

---

## Extending This

| Feature | Where to add |
|---|---|
| Broadcast to all clients | Keep a `ConcurrentHashMap<String, ClientHandler>` in the server |
| Usernames | Ask for a name on first connect, store in `ClientHandler` |
| Private messages | Parse `/msg <name> <text>` in `ClientHandler.run` |
| Timestamps | Prefix each message with `LocalTime.now()` |
| Graceful shutdown | Add a shutdown hook in `ChatServer` to close the `ServerSocket` |

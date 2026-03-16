# Project Plan

Chat app with Jetpack Compose, Material 3, Hilt, Ktor, Room. Make it a multi-module project. App Name: Chatbot Roulette

## Project Brief

# Project Brief: Chatbot Roulette

## Features
1. **Randomized Chat Matching:** Instantly connect with a randomly assigned, unique AI chatbot personality for a fresh conversation every time.
2. **Real-time Messaging:** A fluid, responsive chat interface to seamlessly send and receive messages with your matched bot.
3. **Chat History Persistence:** Automatically save past conversations locally, allowing users to revisit previous chats.
4. **Vibrant Material 3 Interface:** A modern, visually energetic UI fully embracing Edge-to-Edge displays and Android's Material 3 design guidelines.

## High-Level Tech Stack
* **Language:** Kotlin
* **Architecture:** Multi-module Project (e.g., App, Core, Data, Domain, Feature modules)
* **UI Framework:** Jetpack Compose 
* **Design System:** Material 3 (M3)
* **Asynchronous Programming:** Kotlin Coroutines & Flow
* **Dependency Injection:** Hilt (using KSP for code generation)
* **Networking:** Ktor Client (for lightweight and flexible API communication with chatbot services)
* **Local Persistence:** Room Database (using KSP, required to store conversation history and chatbot profiles)

## UI Design Image
![UI Design](/Users/benlewis/dev/android/ChatbotRoulette/input_images/chatbot_roulette_design.jpg)
Image path = /Users/benlewis/dev/android/ChatbotRoulette/input_images/chatbot_roulette_design.jpg

## Implementation Steps

### Task_1_Setup_MultiModule_DI: Set up the multi-module architecture (core, data, domain, feature) and configure Hilt for dependency injection.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Multi-module structure created (core, data, domain, feature)
  - Hilt integrated and compiling successfully across modules
- **StartTime:** 2026-03-16 13:20:11 CDT

### Task_2_Data_Network_Layer: Implement Room database for chat history persistence and Ktor client for external AI chatbot API communication.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Room database, entities, and DAOs created using KSP
  - Ktor client configured for network requests
  - API_KEY integration implemented as a critical requirement for external API

### Task_3_Compose_UI: Implement the Material 3 Edge-to-Edge UI for Chatbot Roulette, including the randomized matching screen and real-time chat interface. Add an adaptive app icon.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Randomized matching screen implemented
  - Real-time chat interface implemented
  - Vibrant Material 3 color scheme applied
  - Adaptive app icon created matching core function
  - The implemented UI must match the design provided in [/Users/benlewis/dev/android/ChatbotRoulette/input_images/chatbot_roulette_design.jpg]

### Task_4_Integration_Run_Verify: Integrate UI with ViewModels and Repositories to complete the flow. Run and Verify the application. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** PENDING
- **Acceptance Criteria:**
  - UI seamlessly connected to data and domain layers
  - App runs without crashing
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - critic_agent verification completed


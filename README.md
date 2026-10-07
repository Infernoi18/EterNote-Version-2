# Eternote V2

> **Send words across time.** Eternote V2 is a secure, time-locked digital time capsule application built for Android. Preserve memories, reflections, and milestones today, and unlock them precisely when the future arrives.

---

## 🌟 App Concept & Vision

In a digital age of instant communication, **Eternote V2** introduces intentional delay. It allows users to anchor thoughts, media, and core memories to specific future dates or milestones. Whether sending a message to your future self 5 years from now, preserving a birthday wish, or creating a collaborative time capsule, Eternote V2 blends deep-space aesthetics with robust modern Android architecture to make time travel feel tangible.

---

## ✨ Core Features

* **Multi-Type Time Capsules:** Create self-reflection capsules, custom-dated memory lockers, or birthday-locked messages with flexible delivery parameters.
* **Smart Date & Timing Controls:** Strict validation engines ensuring time-locked capsules safely enforce minimum future thresholds (tomorrow onwards) to preserve anticipation.
* **Dynamic Theme Engine:** Seamlessly toggle between deep-space **Dark Mode**, **Light Mode**, and **System Default** powered by modern Jetpack DataStore preferences.
* **Journey Dashboard:** Track your archiving stats, unlocked memories, and core milestones in a single glance.
* **Immersive Cosmic UI:** Custom glassmorphism cards, glowing accent hierarchies (`CosmicViolet`, `NebulaPink`, `AuroraCyan`), and edge-to-edge system bar coordination.

---

## 🛠️ Technical Architecture & Stack

Built natively from the ground up following official Android architectural guidelines and modern best practices:

* **UI Framework:** 100% Jetpack Compose with Material 3 design systems and custom state hoisting.
* **Dependency Injection:** Google Hilt for modular, testable compile-time dependency management.
* **Asynchronous Flow:** Kotlin Coroutines & Flows (`collectAsStateWithLifecycle`) for reactive state observation.
* **Local Persistence:** Jetpack DataStore for type-safe user preference storage (theme selection, settings).
* **Lifecycle & Architecture:** MVVM (Model-View-ViewModel) clean architecture separation across UI screens, view models, and repositories.

# 📱 NextTrade – Android Marketplace App

NextTrade is a second-hand mobile marketplace built with modern Android development principles. Inspired by platforms like Yad2 and Facebook Marketplace, it allows users to register, create listings, view posts, and manage their own listings with ease.

The app is built entirely in **Kotlin**, leveraging **Jetpack Navigation**, **Firebase (Auth + Firestore)**, **Cloudinary**, and **Picasso** for image handling. The UI uses **ConstraintLayout** for responsive design.

---

## 🎯 Project Vision

The goal of NextTrade is to deliver a clean, lightweight marketplace app where:

- New and authenticated users can easily create listings.
- Listings are presented in a fast, scrollable feed.
- Each post contains all critical information, including price, category, image, and contact info.
- Users can view post details or manage (edit/delete) their own posts.

---

## 🧱 Design Philosophy & Component Decisions

### 🧩 Why Use Fragments Over Activities?

We deliberately chose **Fragments** as the core building blocks of the UI for the following reasons:

#### ✅ Single-Activity Architecture

All screens (besides Login and Registration) are hosted inside **MainActivity**, which serves as the container for a `NavHostFragment`. This architecture:

- Allows us to take advantage of the **Navigation Component**
- Keeps the back stack consistent
- Simplifies shared elements (like bottom navigation or headers)
- Makes global transitions and animations easier

#### ✅ UI Reusability

Fragments are easier to **reuse or compose**, especially when scaling the app or supporting tablets/multi-pane UIs later on.

#### ✅ Shared Navigation Graph

Jetpack Navigation ties all fragments together with a clear XML representation (`nav_graph.xml`), avoiding manual `FragmentTransaction` code and reducing errors.

---

## 🧠 ViewModel Integration (MVVM Pattern)

To ensure maintainability and a cleaner architecture, the app now uses the **MVVM (Model-View-ViewModel)** pattern across core components.

### 🚀 Why ViewModel?

| Benefit                    | Description                                                                 |
|----------------------------|-----------------------------------------------------------------------------|
| **Lifecycle-aware**        | ViewModels survive configuration changes like rotation                     |
| **Separation of Concerns** | Keeps UI logic (Fragments) separate from business and data operations       |
| **Reactive UI**            | Uses `LiveData` to update the UI automatically when data changes            |

---

### 📡 LiveData in Action

Each ViewModel exposes key LiveData objects:

```kotlin
val loading: LiveData<Boolean>
val errorMessage: LiveData<String?>
val userData: LiveData<User?>
val postSaved: LiveData<Boolean> 
```
---

## 📦 Features

### 👤 User Authentication (Firebase Auth)
- Register new users via email/password, with the support of uploading a profile picture
- Login support with validation
- Secure user session management

### 🛒 Post Listings (Firestore)
- Add, edit, and delete listings
- Each post contains:
    - Name, description, price, category, location
    - Contact info via Firebase user lookup

### 📸 Image Hosting (Cloudinary)
- Images uploaded to Cloudinary via secure upload
- Faster rendering via CDN
- Clean Firestore integration: image URL saved with post

### 🧭 Navigation Flow

Navigation is managed by Jetpack Navigation:

- **LoginActivity → MainActivity → NavHostFragment**
- `MainActivity` hosts the bottom navigation with tabs:
    - Home (PostsFragment)
    - Create Post (CreatePostFragment)
    - My Profile (MyProfileFragment)

Other Fragments include:
- `PostDetailsFragment`: detailed post view
- `MyPostsFragment`: manage user’s own posts
- `UpdatePostFragment`: update existing post

---

## 🧑‍💻 Tech Stack

| Tech                  | Purpose                                        |
|-----------------------|------------------------------------------------|
| **Kotlin**            | Primary language                              |
| **Fragments**         | Modular UI components                         |
| **Navigation Component** | Manages app flow, backstack, transitions     |
| **Firebase Auth**     | User authentication                          |
| **Firestore**         | Cloud NoSQL DB for posts and users           |
| **Cloudinary**        | Image hosting and transformation              |
| **Picasso**           | Image loading with caching and placeholders   |
| **ConstraintLayout**  | Flexible and responsive UI design             |
| **RecyclerView**      | For efficient scrollable post lists           |

---

## 🖌️ UI & UX Design

### ConstraintLayout

Used in all layouts for its:
- Powerful constraint-based system
- Flat hierarchy (better performance)
- Support for responsive, adaptive layouts

### Post Image

- The image is displayed in:
    - Feed item
    - Post detail view
    - MyPosts edit/view screen

### Input Fields

- Spinners for category/location to limit invalid entries
- EditTexts for title, price, and description
- Simple validation and feedback via Toasts

---

## ✅ Why These Tools?

| Tool            | Why we chose it                                        |
|------------------|--------------------------------------------------------|
| **Fragments**    | Better for modular UI, lifecycle handling, reuse       |
| **Nav Component**| Clean navigation, type-safe args, back stack mgmt      |
| **Firebase Auth**| Easy to set up, secure, well integrated                |
| **Firestore**    | Real-time updates, mobile optimized, scalable          |
| **Cloudinary**   | Better image delivery than Firebase Storage            |
| **Picasso**      | Lightweight and reliable for image loading             |

---

## 🛡️ Security Notes

- Only authenticated users can create/edit/delete posts
- Firestore security rules enforce post ownership
- Image uploads go through secure Cloudinary API
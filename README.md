# FirstClub Membership System 🚀

A robust and scalable backend system built with **Spring Boot 3.3.5** that manages subscription-based memberships, tier progression, and a sophisticated reward logic engine for an e-commerce platform.

---

## ✨ Features

- **Membership Management**: View, subscribe, upgrade/downgrade, and cancel membership plans.
- **Multi-Tier System**: Users progress through Bronze, Silver, Gold, and Platinum tiers.
- **Smart Reward Logic**: Earn points from purchases, first orders, referrals, and order frequency.
- **Automated Point Decay**: Points diminish after periods of inactivity to encourage engagement.
- **Tier Progression**: Automatic tier upgrades (and downgrades) based on a user's current points and subscription status.
- **RESTful API**: A clean, well-defined API for easy integration with front-end clients.

---

## 🧠 Core Logic & Rules

### Membership Plans & Tiers
| Plan Type  | Description                      |
|------------|----------------------------------|
| **Monthly**  | 30-day subscription cycle        |
| **Quarterly**| 90-day subscription cycle        |
| **Yearly**   | 365-day subscription cycle       |

### Tier Benefits & Requirements
| Tier      | Points Range | Benefits                                                                 |
|-----------|--------------|--------------------------------------------------------------------------|
| **Bronze**  | 0 – 9999       | No perks                                                                 |
| **Silver**  | 10000 – 29999    | Free delivery on orders > Rs.50, +5% discount on all orders               |
| **Gold**    | 30000 – 49999    | Free delivery on all orders, 10% discount, early access to sales        |
| **Platinum**| 50000+         | All Gold perks + premium access to exclusive products and events        |

### Key Behaviors
- **Tier Source of Truth**: A user's displayed tier is the higher of their subscription-paid tier or their points-earned tier.
- **Points Decay**: Users lose **500 points per day** after 1 days of inactivity (processed via a daily scheduler).
- **Subscription Expiry**: Membership and its associated paid tier benefits end automatically when the plan duration is over.

---

## 🛠️ Tech Stack

| Component       | Technology                           |
|-----------------|--------------------------------------|
| **Backend Framework** | Spring Boot 3.3.5                   |
| **Language**    | Java 17                              |
| **Database**    | H2 In-Memory Database                |
| **Build Tool**  | Maven                                |
| **API Style**   | REST                                 |


---

## 🚀 Getting Started

### Prerequisites
- Java 17 JDK or later
- Maven 3.3.5 (or use the embedded Maven wrapper)

### 1. Clone the Repository
```bash
git clone https://github.com/pavanreddy-02/Membership_firstclub.git
cd Membership_firstclub

### 2️⃣ Start Application
mvn spring-boot:run

Application runs on: **http://localhost:8080**

---

## 🗄️ H2 Console (In-Memory DB)

URL: http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:mem:membershipdb`  
Username: `sa`  
Password: `password`

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/membership/plans` | Fetch all available membership plan + tier pricing |
| POST | `/api/membership/subscribe` | Subscribe a user to a plan + tier |
| GET | `/api/membership?userId=xxx` | Get the current membership status |
| PUT | `/api/membership/upgrade?userId=xxx&tier=GOLD` | Upgrade tier |
| PUT | `/api/membership/downgrade?userId=xxx&tier=SILVER` | Downgrade tier |
| DELETE | `/api/membership/cancel?userId=xxx` | Cancel active membership |
| POST | `/api/membership/order` | Record an order and apply points |

---

## 🧪 API Request Examples

### ➤ Subscribe
POST `/api/membership/subscribe`
```json
{
  "userId": "customer123",
  "planType": "MONTHLY",
  "tier": "GOLD"
}

🚀 FirstClub Membership System

A backend system built using Spring Boot 3.3.5 that supports subscription-based memberships, tier progression, and reward logic for users of an e-commerce platform.

The system allows users to:

View available membership plans

Subscribe to a plan with a chosen tier

Upgrade or downgrade subscription tiers

Cancel active membership

Track current membership status

Earn and decay points based on purchases and activity

Progress through tiers automatically (Bronze → Silver → Gold → Platinum)

🧠 System Logic Summary
Feature	Behavior
Membership Plans	Monthly, Quarterly, Yearly (configurable pricing)
Tiers	Bronze, Silver, Gold, Platinum
Tier Source of Truth	Based on points — but never below paid tier during active subscription
Points Earned	Based on purchase amounts, first order, referrals, order count
Points Decay	-5 points/day after inactivity (via scheduler)
Upgrade / Downgrade	Allowed while subscription is active
Expiry	Subscription automatically ends when plan duration is over
Tier Levels
Tier	Points Range	Benefits
Bronze	0–99	No perks
Silver	100–299	Free Delivery >50, +5% discount
Gold	300–499	Free delivery, 10% discount, early access
Platinum	500+	All perks + premium access
🛠️ Tech Stack
Component	Technology
Backend	Spring Boot 3.3.5
Language	Java 17
Database	H2 In-memory DB
Build Tool	Maven
API Style	REST
Testing	Postman / JUnit ready
📦 How to Run
1️⃣ Clone Project
git clone https://github.com/pavanreddy-02/Membership_firstclub.git
cd Membership_firstclub

2️⃣ Run Locally
mvn spring-boot:run

3️⃣ Access H2 Console
http://localhost:8080/h2-console


JDBC URL: jdbc:h2:mem:membershipdb
User: sa
Password: password

📡 REST API Endpoints
Method	Endpoint	Description
GET	/api/membership/plans	Fetch available plan + tier pricing
POST	/api/membership/subscribe	Subscribe to membership
GET	/api/membership?userId=XYZ	Get user’s membership state
PUT	/api/membership/upgrade	Upgrade tier
PUT	/api/membership/downgrade	Downgrade tier
DELETE	/api/membership/cancel	Cancel subscription
POST	/api/membership/order	Add order and apply reward logic
🧪 Sample Request
Subscribe Request
POST /api/membership/subscribe

{
  "userId": "pavan",
  "planType": "MONTHLY",
  "tier": "GOLD"
}

Record Order
POST /api/membership/order

{
  "userId": "pavan",
  "amount": 1500
}

📆 Scheduler Jobs
Job	Frequency	Purpose
Point Decay Processor	Daily	Reduce points for inactivity
Tier Evaluation	After every order + nightly cron	Automatically promote or demote users
🧩 Folder Structure
src/main/java/com.firstclub.membership
 ├─ controller/
 ├─ service/
 ├─ service/impl/
 ├─ model/
 ├─ repository/
 └─ dto/

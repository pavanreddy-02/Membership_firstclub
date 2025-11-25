# FirstClub Membership System

A Spring Boot backend providing subscription membership, reward points, tier benefits, and automatic upgrades. Built using Java 17, Spring Boot 3.3.5, and H2 in-memory DB.

---

## How to Run

```bash
git clone https://github.com/pavanreddy-02/Membership_firstclub.git
cd Membership_firstclub
mvn spring-boot:run
```

App URL: `http://localhost:8080`

---

## H2 Console

URL: `http://localhost:8080/h2-console`  
JDBC: `jdbc:h2:mem:membershipdb`  
Username: `sa`  
Password: `password`

---

## Membership Rules

### Plans Available
- Monthly
- Quarterly
- Yearly

Each can be purchased with tiers: Silver, Gold, Platinum.

### Tiers (Based on Points)

| Tier | Range | Benefits |
|------|-------|----------|
| Bronze | 0–9999 | No perks |
| Silver | 10000–29999 | 5% discount, conditional free delivery |
| Gold | 30000–49999 | 10% discount, faster delivery, early access |
| Platinum | 50000+ | 15% discount, free delivery all, priority support |

### Tier Behavior Logic

- All users start at **Bronze**
- Subscription locks the **paid tier as minimum**
- Tier can increase if points exceed thresholds
- After expiry → reverts to points-based tier
- Points never go below 0

### Points System

| Action | Points |
|--------|--------|
| ₹1 spent | 1 point |
| Each order | +500 |
| First order bonus | +5000 |
| Referral bonus | +10000 |
| Inactivity | -500/day after day of inactivity |

---

## API Routes

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/membership/plans` | Get available plans with tier pricing |
| POST | `/api/membership/subscribe` | Subscribe user |
| GET | `/api/membership?userId={id}` | Check membership status |
| PUT | `/api/membership/upgrade` | Upgrade tier |
| PUT | `/api/membership/downgrade` | Downgrade tier |
| DELETE | `/api/membership/cancel` | Cancel membership |
| POST | `/api/membership/order` | Record purchase + add points |

---

## Example Requests

### Subscribe

```json
{
  "userId": "pavan",
  "planType": "MONTHLY",
  "tier": "GOLD"
}
```

### Record Order

```json
{
  "userId": "pavan",
  "amount": 1500
}
```

---

## Project Structure

```
src/main/java/com.firstclub.membership/
│── controller/
│── service/
│── service/impl/
│── repository/
│── model/
│── dto/
└── config/
```

---

## Tech Stack

- Java 17
- Spring Boot 3.3.5
- H2 Database
- JPA/Hibernate

---

## Future Enhancements

- Swagger Documentation
- Admin UI for pricing config

---

Status: Ready for demo & extension.

## Model Concerns

### ParkingSpotType Missing

- You’ve used VehicleType to determine spot compatibility.
- But in a real system:
    - A spot may be marked for multiple vehicle types
    - And some spots may be universal
- **Suggestion**: Introduce ParkingSpotType (can handle 1:N mapping) and check compatibility via a utility or strategy.

## SRP Violations

### VehicleType

- it has billing logic (charges) baked into the enum. While okay for basic use cases, this might violate SRP (Single
  Responsibility Principle).
- In large systems, billing logic may vary by:
    - Customer type
    - Time of day (peak/off-peak)
    - Special discounts, validations, etc.
- **Improvement**: Consider abstracting this into a BillingPolicy or PricingStrategy.

### ParkingLot

- ParkingLot.displayParkingLotStatus() tightly couples the domain object with console output. That breaks Separation of
  Concerns.

## OCP Violations

### Pricing Logic

```
double hourlyCharge = hourlyRate * hours;
return baseCharge + hourlyCharge;
```

❗ If tomorrow there's a rule like "Free first 30 minutes", you'll need to edit this method.

**Suggested Fix**: Introduce a ChargeCalculationStrategy

Helps in scenarios like:

- Peak hour pricing
- Weekend discounts
- EV surcharges

## Validations Missing

### Object overwriting

- addGate, addParkingSpot, etc., allow overwriting IDs in the map.
- Fix Ideas:
    - Throw IllegalArgumentException if ID already exists

### Un-park Validations

You don’t validate:

- If the ticket is already billed
- If the spot is already available

You can:

- Track ticket status (ACTIVE, CLOSED)
- Refuse re-processing of same ticket

## Concurrency Concerns

### Concurrency Safety in Spot Assignment

❗ Problem:
No thread-safety when multiple vehicles are parking in parallel. Race condition exists between spot selection and
marking as OCCUPIED.

✅ Fix:
Lock at ParkingLot or ParkingFloor level.

**Fix Options**

1. **Synchronize when finding and assigning spot - (WRONG ❌)**
    ```
    synchronized(parkingFloor) {
        Optional<ParkingSpot> optionalSpot = parkingFloor.findParkingSpot(vehicle.type());
        optionalSpot.ifPresent(spot -> spot.setParkingSpotStatus(ParkingSpotStatus.OCCUPIED));
    }
    ```
    - classic concurrency pitfall in multi-level resource selection systems
    - If only one spot is available on floor-1:
        - Thread-1 and Thread-2 both call findParkingFloor(...)
        - Both get floor-1 before anyone acquires lock
        - Thread-1 gets the lock, books the spot
        - Thread-2 gets the lock next → findParkingSpot(...) returns empty
        - Thread-2 throws ParkingLotFullException ❌
        - Even though, say, floor-2 has a free spot!
    - **What Should Be Done?**
        - You need to make the floor selection + spot booking an atomic operation
        - otherwise you're operating on stale assumptions.

2. **Global Lock / Synchronized on ParkingLot - (Performance-Hit)**

    ```
    synchronized (parkingLot) {
        // 1. Find floor that still has a real available spot
        // 2. Then find, spot and mark occupied
    }
    ```

    - Pros:
        - Simple, consistent behavior
        - Avoids stale assumptions
    - Cons:
        - Reduced concurrency — only one vehicle at a time across lot

3. **Loop through ranked floors and synchronize - (recommended)**

- Update your ParkingFloorStrategy to return a ranked list of candidate floors

    ```
    for (ParkingFloor floor : rankedFloors) {
        synchronized (floor) {
            Optional<ParkingSpot> spot = floor.findParkingSpot(vehicle.type());
            if (spot.isPresent()) {
                spot.get().setParkingSpotStatus(ParkingSpotStatus.OCCUPIED);
            }
        }
    }
    ```
    - Correctness: no double-booking, no false full
    - Concurrency: Multiple threads can book on different floors concurrently
    - Performance: Avoids locking the parkingLot; only one floor at a time

### Race Condition During Un-park (Freeing Spot)

Scenario: Two different systems (e.g., app + kiosk) try to un-park the same ticket simultaneously.

🔥 Issue:
Parking spot might be marked AVAILABLE twice

Ticket may be billed twice

✅ Solution:
Add TicketStatus (ACTIVE, CLOSED) to ParkingTicket

In createParkingBill:

```
synchronized (parkingTicket) {
    if (parkingTicket.status() == CLOSED) {
        throw new IllegalStateException("Ticket already closed");
    }

    // mark CLOSED before freeing the spot
    parkingTicket.setStatus(CLOSED);
    parkingTicket.parkingSpot().setParkingSpotStatus(AVAILABLE);
}
```

### Inconsistent Availability Readings

Scenario: While querying available spots, a vehicle gets parked, and spot status changes between AVAILABLE → OCCUPIED.

🔥 Issue:
Your dashboard might show incorrect count

Could lead to overbooking

✅ Solution:
Wrap availability query and assignment in the same lock/synchronized block on ParkingFloor (read-write synchronization).

Or, use ReadWriteLock per floor:

```
class ParkingFloor {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public List<ParkingSpot> getAvailableSpots() {
        rwLock.readLock().lock();
        try {
            return spots.stream().filter(...).toList();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void assignSpot(...) {
        rwLock.writeLock().lock();
        try {
            ...
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

### ID Counter Collisions

You generate ticket IDs using:

```
"ParkingTicket-" + count++
```

🔥 Issue:
parkingTicketsCount++ is not atomic

Two threads may generate same ID

✅ Solution:
Use AtomicInteger:

```java
private final AtomicInteger parkingTicketsCount = new AtomicInteger();

String ticketId = "ParkingTicket-" + parkingTicketsCount.incrementAndGet();
```

Or use a thread-safe ID generator:

```
UUID.randomUUID().toString()
```

## Miscellaneous

### Repository Layer Abstraction

Right now, you use in-memory maps:

- Map<String, ParkingLot> parkingLotRepo
- Map<String, ParkingTicket> parkingTicketRepo

✅ Fix:
Extract interfaces:

```java
interface ParkingLotRepository {
    ParkingLot findById(String id);

    void save(ParkingLot parkingLot);
}
```

Then create:

- InMemoryParkingLotRepository
- Future: DbParkingLotRepository

### Vehicle Lookup / Search Functionality

Once a car is parked, there’s no good way to:

- Find if it's still inside
- Get a ticket by registration number

## Interview Questions

### What design changes are needed to support reserved parking or EV charging spots?

- We can add new ParkingSpotTypes (e.g., RESERVED, EV_CHARGING)
- and modify the findParkingSpot() logic to filter based on required type.
- Strategies can be extended or overridden as needed.

### Can you plug in surge pricing on weekends?

Use a PricingStrategy interface that encapsulates all pricing logic. Then plug in different strategies depending on the
time/day.

```java
public interface PricingStrategy {
    double calculateCharges(VehicleType vehicleType, Date inTime, Date outTime);
}

public class DefaultPricingStrategy implements PricingStrategy {
    @Override
    public double calculateCharges(VehicleType vehicleType, Date inTime, Date outTime) {
        // default calculation logic
    }
}

public class WeekendSurgePricingStrategy implements PricingStrategy {
    private final PricingStrategy baseStrategy;
    private final double surgeMultiplier;

    public WeekendSurgePricingStrategy(PricingStrategy baseStrategy, double surgeMultiplier) {
        this.baseStrategy = baseStrategy;
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculateCharges(VehicleType vehicleType, Date inTime, Date outTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inTime);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        boolean isWeekend = (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
        double baseCharge = baseStrategy.calculateCharges(vehicleType, inTime, outTime);

        return isWeekend ? baseCharge * surgeMultiplier : baseCharge;
    }
}

PricingStrategy pricingStrategy = new WeekendSurgePricingStrategy(
        new DefaultPricingStrategy(), 1.5
);

double amount = pricingStrategy.calculateCharges(
        parkingTicket.vehicle().type(), parkingTicket.inTime(), outTime
);
```

### How would you make this horizontally scalable?

If we add more parking lots or want to distribute load:

- Extract services into microservices (ticketing, billing)
- Use a central DB or distributed cache (Redis) for spot status
- Use message queues (e.g., Kafka) to synchronize events

### How can we remove Hardcoding of strategies?

✅ 1. Use a Strategy Factory

```java
public class PricingStrategyFactory {
    public static PricingStrategy getPricingStrategy(Date inTime) {
        PricingStrategy base = new DefaultPricingStrategy();

        // surge pricing
        // based on some condition
        if (isWeekend(inTime)) {
            return new SurgePricingStrategy(base, 1.5);
        }

        return base;
    }

    private static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);
        return day == Calendar.SATURDAY || day == Calendar.SUNDAY;
    }
}
```

✅ 2. Strategy Registry

```java
public class StrategyRegistry {
    private static final Map<String, PricingStrategy> strategies = new HashMap<>();

    static {
        strategies.put("default", new DefaultPricingStrategy());
        strategies.put("weekendSurge", new WeekendSurgePricingStrategy(
                new DefaultPricingStrategy(), 1.5
        ));
    }

    public static PricingStrategy get(String strategyName) {
        return strategies.getOrDefault(strategyName, new DefaultPricingStrategy());
    }
}

String strategyKey = "weekendSurge"; // Could come from config or decision logic
PricingStrategy strategy = StrategyRegistry.get(strategyKey);
```

### How can we add an audit trail?

| Feature        | Implementation                                 |
|----------------|------------------------------------------------|
| Capture events | Inside `ParkingTicketService` methods          |
| Log data       | Create `AuditEntry` and push to `AuditService` |
| Storage        | In-memory list (basic) or DB/file (advanced)   |
| Extensibility  | Add filters, search, user metadata             |

   


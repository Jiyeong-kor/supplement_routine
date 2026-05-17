# Supplement Routine User Flow

## 1. Primary User Goals

1. Check what to take today.
2. Mark an intake as completed.
3. Register a new supplement rule.
4. Review recent history.
5. Adjust reminder and meal-time defaults.

## 2. Core Flow

```mermaid
flowchart TD
    A["Open App"] --> B["Today Screen"]
    B --> C{"Any schedule today?"}
    C -->|Yes| D["Review intake list"]
    D --> E["Check completed intake"]
    E --> F["Progress updates"]
    C -->|No| G["Empty state"]
    G --> H["Go to add supplement"]
```

## 3. Add Supplement Flow

```mermaid
flowchart TD
    A["Today or Supplements"] --> B["Tap add"]
    B --> C["Enter name"]
    C --> D["Enter dosage"]
    D --> E{"Choose method"}
    E -->|Routine-based| F["Choose intake slots"]
    E -->|Manual time| G{"Choose detail"}
    G -->|Fixed time| H["Enter times"]
    G -->|Interval| I["Enter start time and interval"]
    F --> J["Set reminder and memo"]
    H --> J
    I --> J
    J --> K["Save"]
    K --> L["Today schedule updates"]
```

## 4. Check-In Flow

```mermaid
flowchart TD
    A["Today Screen"] --> B["Tap intake item"]
    B --> C{"Current state"}
    C -->|Incomplete| D["Mark done"]
    C -->|Done| E["Undo completion"]
    D --> F["Persist record"]
    E --> F
    F --> G["Update progress / widget / history"]
```

## 5. History Flow

```mermaid
flowchart TD
    A["History tab"] --> B["Today's summary"]
    B --> C["Monthly completion calendar"]
    C --> D["Recent two-week list"]
```

## 6. Settings Flow

```mermaid
flowchart TD
    A["Settings tab"] --> B{"What changes?"}
    B -->|Meal time| C["Meal time bottom sheet"]
    C --> D["Recalculate today's schedule"]
    B -->|Default reminder| E["Toggle default"]
    E --> F["Apply to future add form"]
    B -->|Reset data| G["Confirm dialog"]
    G --> H["Delete supplements and records"]
```

## 7. Notification Flow

```mermaid
flowchart TD
    A["Supplement reminder enabled"] --> B["Generate today's schedule"]
    B --> C["Check exact alarm permission"]
    C -->|Granted| D["Schedule exact reminder"]
    C -->|Denied| E["Schedule inexact fallback"]
    D --> F["On-time notification"]
    E --> G["Potentially delayed notification"]
```

## 8. Exception Flows

| Situation | System Response | User Need |
| --- | --- | --- |
| Missing supplement name | Block save + validation message | Know what to fix immediately |
| Dosage is zero or less | Block save + validation message | Understand allowed input |
| Routine slots missing | Block save + validation message | Know that a required field is missing |
| No schedule today | Show empty state | Reach registration action quickly |
| Exact alarm permission denied | Fall back to inexact reminders and show guidance in Settings | Users can understand the delay risk and re-enable the permission |

## 9. UX Principles

- Users should understand today's next action within 3 seconds.
- Check-in should take one tap.
- Registration should not require health expertise.
- High-frequency screens favor scanability over decoration.
- Medical judgment stays outside product scope.

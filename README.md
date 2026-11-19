# PoisonPulse (POISN)

Lightweight offline Fabric client mod for Minecraft **26.2**.

## Features

1. **Custom player nametags** (configurable)
2. **Item prices in tooltips** from local `prices.json`

---

## Config files

After first start the mod creates:

```
.minecraft/config/poisn/
├── members.json     ← Spieler + Farben
└── prices.json      ← Itempreise
```

### members.json Beispiel

```json
{
  "members": [
    {
      "name": "TxmmyYT_",
      "rank": "Member",
      "rankColor": "green",
      "nameColor": "white",
      "underColor": "white"
    },
    {
      "name": "The_downfall",
      "rank": "Member",
      "rankColor": "green",
      "nameColor": "white",
      "underColor": "white"
    },
    {
      "name": "Poison_PulseYT",
      "rank": "Leader",
      "under": "Twitch: PosonPulse",
      "rankColor": "gold",
      "nameColor": "green",
      "underColor": "light_purple"
    }
  ]
}
```

### Verfügbare Farben

| Wert            | Bedeutung          |
|-----------------|--------------------|
| green           | POISN Grün         |
| gold            | Leader Gold        |
| aqua            | Hellblau           |
| blue            | Blau               |
| light_purple    | Lila / Twitch      |
| dark_purple     | Dunkel-Lila        |
| white           | Weiß               |
| gray            | Grau               |
| yellow          | Gelb               |
| red             | Rot                |
| dark_green      | Dunkelgrün         |
| dark_aqua       | Dunkel-Türkis      |
| dark_blue       | Dunkelblau         |
| dark_red        | Dunkelrot          |
| dark_gray       | Dunkelgrau         |
| black           | Schwarz            |

Du kannst auch die normalen Minecraft-Namen benutzen (`GREEN`, `GOLD`, `LIGHT_PURPLE` usw.).

---

## Build

Benötigt **Java 25**.

```bash
./gradlew build
```

JAR liegt danach in `build/libs/`.

---

## License

All Rights Reserved – PoisonPulse

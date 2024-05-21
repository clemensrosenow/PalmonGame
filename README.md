# The Palmon Game

A console-based Pokémon adventure created in Java as a university task.

1. **Diverse**: Assemble your team from a variety of characters.
2. **Interactive**: Choose your strongest attacking moves.
3. **Multilingual**: English or German - you decide.

![Team Assembly](https://github.com/clemensrosenow/PalmonGame/assets/84013955/f36e70a3-1464-4b61-9e0d-8f30396ef971)

---
![Attacking Sequence](https://github.com/clemensrosenow/PalmonGame/assets/84013955/b88ea37b-0211-4adc-85f5-b487b9b44c48)

---

## Technical Implementation

### Entities

```mermaid
---
title: Game Class Hierarchy
---
classDiagram
class Fight {
	-User user
	-Opponent opponent
	+assembleTeams()
	+battle()
	-attack(Team attackingTeam, Team defendingTeam, boolean randomMove) boolean
	-printBattleStatus()
	+printResult()
}
namespace Characters {
class Player {
	<<interface>>
	+setName()
	+assembleTeam(int palmonCount)
}
class Opponent
class User
}
class TeamAssembler {
	#HashSet~Palmon~ plamons
	-int palmonCount
	-int minLevel
	-int maxLevel
	-Method assemblyMethod
	-assemble()
	-assemblyUncompleted() boolean
	-add(Palmon palmon, boolean confirmation)
	-selectPalmonByType(HashMap~String, ArrayListPalmon~ palmonsGroupedByType) Palmon
	-selectPalmonById(ArrayList~Palmon~ dataSource, String prompt) Palmon
}
class Method {
	<<Enumeration>>
	random
	id
	type
	~getLocalizedOptions() ArrayList~String~
	~getLocalized() String
}
class Team {
	-Iterator~Palmon~ fightingQueue
	~Palmon fightingPalmon
	~swapFightingPalmon()
	~isDefeated() boolean
	~remainingPalmons() int
}
class Palmon {
	+int lowestLevelPossible 
	+int highestLevelPossible
	+int maxMoves
	+int id
	+String name
	+int height
	+int weight
	+String[2] types
	+int hp
	+int attack
	+int defense
	+int speed
	~int level
	-ArrayList~Move~ moves
	+boolean inTeam
	+assignHighestDamageMoves()
	+isDefeated() boolean
	-selectAttack(ArrayList~Move~ availableMoves, boolean randomSelection) Move
	-selectMoveById(String prompt, ArrayList~Move~ dataSource) Move
	-getAvailableMoves() ArrayList~Move~
	+performAttack(Palmon victim, boolean randomMoveSelection)
	+setRandomLevel(int min, int max)
	+getTypes() String
}
click Palmon href "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
class Move {
	~int medianUnlockLevel
	+int id
	+String name
	+int damage
	-int availableUsages
	+float accuracy
	+String type
	+use()
	+isAvailable() boolean
	+hits() boolean
}
Fight *-- User
Fight *-- Opponent
Player <|-- User
Player <|-- Opponent
User o-- Team
Opponent o-- Team
Team "1" *-- "*" Palmon
TeamAssembler <|-- Team
TeamAssembler >-- Method
Palmon o-- "0..4" Move
```

---

### Resources

4 CSV files are loaded in a seperate thread after the game starts. Their processed base data can be accessed using the DB class.

1. **palmon.csv**: The information of more than 1000 Palmons.
2. **moves.csv**: All the attacks a Palmon can learn.
3. **palmon_move.csv**: n:m connection between a Palmon and its moves + their learned level.
4. **effectivity.csv**: The damage multiplier between different types.

---

### Utilities

- **CSV Processing**: Package consisting of a CSVLoader that schedules a CSVReader for each resource file. Based on an abstract class, there are various line processors passed as a dependency to the CSVReaders.
- **Data Normalization**: Converts CSV data and user input to the correct data format.
- **Execution Pause**: Stops the execution thread on demand to enhance the console output speed for the user.
- **Localization**: Configuration and accessor-methods to enable multi-language print statements.
- **Map [Heap](https://en.wikipedia.org/wiki/Heap_(data_structure))**: Custom data structure to assign the highest-damage moves to a Palmon.
- **Table Output**: Prints Palmons or Moves in a table format based on System.out.printf().
- **User Input**: Various overloaded methods to get user input in the right format and handle errors.

---

## Personal Key Learnings

### Reusable Utilities
- console input with reliable error handling
- table printing with flexible configuration
- data loading using dependy injection
### Effective Data Processing
- custom max heap data structure
- handling of data inconsistency
- asynchronous threading
 ### Language-specific Features
- enums and records
- resource bundles
- abstract class
---
## Possible expansions

- .exe distribution using [javapackager](https://docs.oracle.com/javase/10/tools/javapackager.htm)
- prioritizing code efficiency over readability
- fight history documentation with CSV file

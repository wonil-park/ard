package com.palehorsestudios.ard.environment;

import com.palehorsestudios.ard.characters.Monster;
import com.palehorsestudios.ard.characters.MonsterFactory;
import com.palehorsestudios.ard.util.Codes;
import com.palehorsestudios.ard.util.ConsoleManager;

import java.util.*;
import java.util.stream.Collectors;

public class Room {
    public static final List<String> ALL = Arrays.asList("all", "everything");

    private String description; // description of the room
    private List<Item> items; // list of items in room
    private List<Monster> monsters; // list of monsters in room
    private final int id; // room id (for ensuring hashcode is different)
    private Random random = new Random(); // Generate random numbers
    private Chest chest; // a chest of reward items
    private final int x = ConsoleManager.getRandomInteger(5,10);
    private final int y = ConsoleManager.getRandomInteger(5,10);


    /**
     * Constructor
     *
     * @param description
     * @param id
     */
    public Room(String description, int id) {
        this.description = description;
        this.id = id;
        items = new ArrayList<>();
        monsters = new ArrayList<>();
        generateRandomRoomItems();
        generateRandomNormalMonsters();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    /**
     * Return room description
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return all items in room
     *
     * @return items
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * Return all monsters in room
     *
     * @return
     */
    public List<Monster> getMonsters() {
        return monsters;
    }

    /**
     * Returns room id
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    public Chest getChest() {
        return chest;
    }

    /**
     * Adds item to room's item list
     *
     * @param item
     */
    public void addItem(Item item) {
        if (item != null) {
            item.setCoord(this);
            items.add(item);
        }
    }

    /**
     * generates between 0 and 3 items in rooms randomly
     */
    private void generateRandomRoomItems() {
        //Returns a random number.
        //between 0 (inclusive) and 3 (exclusive).
        if (this.id <= 5) {
            int quantity = random.nextInt(3);
            for (int i = 0; i < quantity; i++) {
                addItem(Item.values()[random.nextInt(6)]);
            }
        } else if (this.id > 5) {
            int quantity = random.nextInt(6);
            for (int i = 0; i < quantity; i++) {
                addItem(Item.values()[random.nextInt(12)]);
            }
        }
    }

    /**
     * Adds monster to room's monster list
     *
     * @param monster
     */
    public void addMonster(Monster monster) {
        if (monster != null) {
            monster.setCoord(this);
            monsters.add(monster);
        }
    }

    //randomly generate normal monsters with a probability of 80%
    public void generateRandomNormalMonsters() {
        int number = random.nextInt(100);
        if (number < 80) {
            addMonster(MonsterFactory.createMonster());
        }
    }

    /**
     * Adds a list of items to room's item list.
     *
     * @param items
     */
    public void addAllItems(List<Item> items) {
        if (items != null) {
            this.items.addAll(items);
        }
    }

    /**
     * Adds a list of monsters to room's monster list
     *
     * @param monsters
     */
    public void addAllMonsters(List<Monster> monsters) {
        if (monsters != null) {
            this.monsters.addAll(monsters);
        }
    }

    /**
     * Grab an item from room. Removes item from room's item list, if present. Returns a boolean if successfully
     * removed the item.
     *
     * @param item
     * @return
     */
    public boolean grabItem(Item item) {
        return items.remove(item);
    }

    /**
     * Removes monster from room's monster list, if present. Returns a boolean if successfully
     * removed the monster.
     *
     * @param monster
     * @return
     */
    public boolean defeatMonster(Monster monster) {
        return monsters.remove(monster);
    }


    /**
     * Brief overview of what is in a room
     */
    public String overview() {
        StringBuilder sb = new StringBuilder();
        sb.append(Codes.Room.getCode()).append("You are in ").append(Codes.Room.withColor("Room " + getId()));
        String temp = Codes.Left.getCode() + Codes.Left.withColor(" " + getDescription() + " ") + Codes.Right.getCode();
        sb.append("\n").append(Codes.Left.getCode()).append(Codes.Left.withColor(" " + getDescription() + " ")).append(Codes.Right.getCode());
        sb.append("\n").append(Codes.Item.getCode()).append(itemsPresent());
        if (chest != null) {
            sb.append("\n").append(Codes.Chest.getCode()).append(Codes.Chest.withColor(" " + chest.toString()));
        }
        sb.append("\n").append(Codes.Monster.getCode()).append(monstersPresent());
        return sb.toString();
    }

    public List<Map<String, String>> getRoomInfo() {
        List<Map<String, String>> roomInfoList = new ArrayList<>();
        Map<String, String> roomInfo = new HashMap<>();
        roomInfo.put("id", String.valueOf(getId()));
        roomInfo.put("desc", getDescription());
        roomInfo.put("items", itemsPresent());
        roomInfo.put("monsters", monstersPresent());
        roomInfo.put("chest", chestPresent());
        roomInfo.put("x", String.valueOf(getX()));
        roomInfo.put("y", String.valueOf(getY()));

        roomInfoList.add(roomInfo);

        getAllItems().forEach(item -> roomInfoList.add(item.getItemInfo()));
        getAllMonster().forEach(monster -> roomInfoList.add(monster.getMonsterInfo()));
        if (getChest() != null) {
            roomInfoList.add(getChest().getChestInfo());
        }

        return roomInfoList;
    }

    /**
     * Check for items present in a room
     *
     * @return prints list of items present or no items message
     */
    private String itemsPresent() {
        if (getItems().size() > 0) {
            return getItems().stream().map(item -> item.toString()).collect(Collectors.joining(", "));
        } else {
            return "No items present in this room.";
        }
    }

    /**
     * Check for monsters present in a room
     *
     * @return Prints list of monsters or no monsters message
     */
    private String monstersPresent() {
        if (getMonsters().size() > 0) {
            return getAllMonsters().toString();
        } else {
            return "No monsters present in this room.";
        }
    }


    private String getAllMonsters() {
        return getMonsters().stream().map(monster -> monster.getName())
                    .collect(Collectors.joining(", "));
    }
    /**
     * Check for chest presents in a room
     *
     * @return Prints chest if existing
     */
    private String chestPresent() {
        if (chest != null) {
            return chest.toString();
        } else {
            return "No chest present in this room.";
        }
    }

    /**
     * Method to set the chest for the given room.
     *
     * @param chest
     */
    public void setChest(Chest chest) {
        this.chest = chest;
    }

    /**
     * Runs the chest's question (if available) and get the reward from the chest. Adds rewarded items to the room's
     * inventory.
     */

    // TODO: revise this method to handle multi-stage communication
    public String unlockChest() {
        StringBuilder vsb = new StringBuilder();
        if (chest != null) {
            vsb.append(chest.askQuestion());
        } else {
            vsb.append("No ").append(Codes.Chest.withColor("chest")).append(" in this room.");
        }
        return vsb.toString();
    }

    public String submitAnswer(String answer) {
        StringBuilder sb = new StringBuilder();
        Map<Boolean, List<Item>> result = chest.evaluateAnswer(answer);
        if (result.containsKey(true)) {
            if (result.get(true).size() == 0) {
                sb.append("The ").append(Codes.Chest.withColor("chest")).append(" unlocks with a loud click and ... it's empty.");
            } else {
                this.addAllItems(result.get(true));
                sb.append("The ").append(Codes.Chest.withColor("chest")).append(" unlocks with a loud click and empties its contents onto the floor.");
            }
        } else {
            sb.append("That is incorrect! \nThe chest makes a grunt and refuses to open");
        }

        return sb.toString();
    }

    public List<Monster> getAllMonster() {
        return monsters;
    }

    public List<Item> getAllItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room)) return false;
        Room room = (Room) o;
        return getId() == room.getId() &&
                getDescription().equals(room.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getId());
    }
}

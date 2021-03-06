package com.palehorsestudios.ard.characters;
import com.palehorsestudios.ard.environment.Item;
import com.palehorsestudios.ard.environment.Room;
import com.palehorsestudios.ard.util.Codes;
import com.palehorsestudios.ard.util.ConsoleManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public abstract class Player {
    private String name;
    private int life;
    private Room currentRoom;
    private List<Item> itemsInventory;
    private int level;
    private int score = 0;
    private int x;
    private int y;

    public Player() {
    }

    public Player(String name, int life, Room currentRoom, List<Item> itemsInventory, int level) {
        setName(name);
        setLife(life);
        setCurrentRoom(currentRoom);
        setItemsInventory(itemsInventory);
        setLevel(level);
        setScore(getScore());
        setCoord();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    /**
     * Set initial coord
     */
    private void setCoord() {
        x = ConsoleManager.getRandomInteger(getCurrentRoom().getX()-1);
        y = ConsoleManager.getRandomInteger(getCurrentRoom().getY()-1);
    };

    public boolean setCoord(String direction) {
        boolean success = false;
        int x = getX();
        int y = getY();
        int limitX = getCurrentRoom().getX();
        int limitY = getCurrentRoom().getY();

        switch (direction) {
            case "up":
                if (y < limitY - 1) {
                    success = true;
                    y += 1;
                    setCoord(x, y);
                }
                break;
            case "down":
                if (y > 0) {
                    success = true;
                    y -= 1;
                    setCoord(x, y);
                }
                break;
            case "right":
                if (x < limitX - 1) {
                    success = true;
                    x += 1;
                    setCoord(x, y);
                }
                break;
            case "left":
                if (x > 0) {
                    success = true;
                    x -= 1;
                    setCoord(x, y);
                }
                break;
            default:
                System.out.println("navigate player error");
                break;
        }
        return success;
    };

    private void setCoord (int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * If the item is in current room, add the item picked up by the user into the item inventory
     * and remove the item from the room item list
     * if input is not in Item Enum, throw Exception
     */
    public String pickUpItem(String item) {
        boolean validItem = false;
        List<Item> currentItems = new ArrayList<>();
        currentItems.addAll(currentRoom.getItems());
        for(Item i : Item.values()) {
            if (i.name().equals(item)) {
                validItem = true;
                break;
            }
        }
        if (validItem && currentItems.contains(Item.valueOf(item))) {
            itemsInventory.add(Item.valueOf(item));
            currentRoom.grabItem(Item.valueOf(item));
            return "Picked up " + item + ".";
        } else if (Room.ALL.contains(item.toLowerCase()) && currentItems.size() > 0) {
            itemsInventory.addAll(currentItems);
            currentItems.forEach(currentItem -> currentRoom.grabItem(currentItem));
            return "Picked up all items in the current room.";
        } else {
            return "Can't pick up! This item is not in the current room!";
        }
    }

    /**
     * If the item is in player inventory, remove the item dropped by the user from the item inventory
     * and add the item into the room item list
     * if input is not in Item Enum, throw Exception
     */
    public String dropItem(String item) {
        StringBuilder vsb = new StringBuilder();
        boolean validItem = false;
        for(Item i : Item.values()) {
            if (i.name().equals(item)) {
                validItem = true;
                break;
            }
        }
        if (validItem && itemsInventory.contains(Item.valueOf(item))) {
            itemsInventory.remove(Item.valueOf(item));
            currentRoom.addItem(Item.valueOf(item));
            vsb.append(item).append(" dropped.");
        } else {
            vsb.append("Can't drop this item! It's not in player's item inventory!");
        }
        return vsb.toString();
    }

    /**
     * Abstract method to attack a monster
     */
    public abstract String attack();

    /**
     * Abstract method to use player's special power.
     */
    public abstract String useSpecialPower();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
        this.setCoord();
    }

    public List<Item> getItemsInventory() {
        return itemsInventory;
    }

    public void setItemsInventory(List<Item> itemsInventory) {
        this.itemsInventory = itemsInventory;
    }

    public String printStats() {
        StringBuilder sb = new StringBuilder();
        sb.append(Codes.Player.getCode()).append(Codes.Player.withColor(getName()));
        sb.append("\n").append(Codes.Life.getCode()).append(Codes.Life.withColor(getLife()));
        sb.append("\n").append(Codes.Room.getCode()).append(Codes.Room.withColor("Room " + getCurrentRoom().getId()));
        sb.append("\n").append(Codes.Item.getCode()).append(getItemsInventory().stream()
            .map(e -> Codes.Item.withColor(e.toString())).collect(Collectors.joining(", ")));
        sb.append("\n").append(Codes.Score.getCode()).append(Codes.Score.withColor(" Score " + getScore()));
        sb.append("\n").append(Codes.Level.getCode()).append(Codes.Level.withColor(" Level " + getLevel()));
        return sb.toString();
    }

    public Map<String, String> getPlayerInfo() {
        Map<String, String> playerInfo = new HashMap<>();

        playerInfo.put("name", getName());
        playerInfo.put("life", String.valueOf(getLife()));
        playerInfo.put("lv", String.valueOf(getLevel()));
        playerInfo.put("score", String.valueOf(getScore()));
        playerInfo.put("inv", getItemsInventory().stream()
                .map(item -> item.toString()).collect(Collectors.joining(", ")));
        playerInfo.put("x", String.valueOf(getX()));
        playerInfo.put("y", String.valueOf(getY()));
        return playerInfo;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void incrementScore() {
        setScore(getScore() + 10);
    }

    public Set<Item> playerAndRoomItems() {
        Set<Item> newList = Stream.of(getItemsInventory(), getCurrentRoom().getItems())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return newList;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", life=" + life +
                ", currentRoom=" + currentRoom +
                ", itemsInventory=" + itemsInventory +
                ", level=" + level +
                ", score=" + score +
                '}';
    }
}

package com.caved_in.commons.game.item;

import com.caved_in.commons.Commons;
import com.caved_in.commons.chat.Chat;
import com.caved_in.commons.game.gadget.GadgetProperties;
import com.caved_in.commons.game.gadget.ItemGadget;
import com.caved_in.commons.inventory.HandSlot;
import com.caved_in.commons.item.ItemBuilder;
import com.caved_in.commons.player.Players;
import com.caved_in.commons.time.TimeHandler;
import com.caved_in.commons.time.TimeType;
import com.caved_in.commons.world.Worlds;
import com.caved_in.commons.yml.Comment;
import com.caved_in.commons.yml.Path;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public abstract class ThrowableItem extends ItemGadget {

    private Properties properties = new Properties();

    public ThrowableItem(ItemStack item) {
        super(item);
    }

    public ThrowableItem(ItemStack item, int delay) {
        super(item);
        properties().delay(delay);
    }

    public ThrowableItem(ItemBuilder builder) {
        super(builder);
    }

    public ThrowableItem(ItemBuilder builder, int delay) {
        super(builder);
        properties().delay(delay);
    }

    @Override
    public void perform(Player holder) {
        ItemStack gadgetItem = getItem();
//
//        /*
//        With Dual-Wielding available we need to check the hand slot of where the player has the firstPageEnabled.
//         */
//        if (Players.hasItemInHand(holder, gadgetItem, HandSlot.MAIN_HAND)) {
//            Players.removeFromHand(holder, 1, HandSlot.MAIN_HAND);
//        } else {
//            Players.removeFromHand(holder, 1, HandSlot.OFF_HAND);
//        }

        //todo get hand slot which firstPageEnabled is in.

        //Remove an firstPageEnabled from the players hand, taking it out of their total amount for the throwable firstPageEnabled.
        if (properties().takeItem()) {
            Players.removeFromHand(holder, 1,HandSlot.MAIN_HAND);
        }

        Location eyeLoc = holder.getEyeLocation();

        final Item thrownItem = Worlds.dropItem(eyeLoc, gadgetItem);

        //If the firstPageEnabled's not meant to be picked up, then assure it
        //wont be picked up
        if (!properties().canPickup()) {
            thrownItem.setPickupDelay(Integer.MAX_VALUE);
        }

        thrownItem.setVelocity(eyeLoc.getDirection().multiply(properties().force()));

        Action action = properties().action();
        switch (action) {
            case DELAY:
                //After the delay is up, we want to handle the firstPageEnabled!
                Commons.getInstance().getThreadManager().runTaskLater(() -> {
                    //Call the handle, for any implementations to do as they wish!
                    handle(holder, thrownItem);

                    if (properties().action() == Action.CANCEL) {
                        Chat.actionMessage(holder, properties().cancelMessage());
                        properties().action(Action.DELAY);
                        return;
                    }

                    //Remove the firstPageEnabled after the handle is called!
                    if (properties().removeItem()) {
                        thrownItem.remove();
                    }
                }, TimeHandler.getTimeInTicks(properties().delay(), properties().delayType()));
                break;
            case REPEAT_TICK:
                long reTicks;
                if (properties().isTicks()) {
                    reTicks = properties().delay();
                } else {
                    reTicks = TimeHandler.getTimeInTicks(properties().delay(), properties().delayType());
                }

                Commons.getInstance().getThreadManager().registerSyncRepeatTask("Gadget[" + thrownItem.getUniqueId().toString() + "-TICK]", new BukkitRunnable() {
                    @Override
                    public void run() {
                        //Handle the thrown firstPageEnabled just as specified
                        handle(holder, thrownItem);

                        //Though if the firstPageEnabled is no longer available, then cancel the task!!
                        //This means that the firstPageEnabled must be removed within the handle method, to cancel this task.
                        if (!thrownItem.isValid()) {
                            cancel();
                        }
                    }
                }, reTicks, reTicks);
                break;
            case EXECUTE:
                long exTicks = properties().isTicks() ? properties().delay() : 50l;
                //Execute the task 2.5 seconds later, as it gives the firstPageEnabled time to travel!
                Commons.getInstance().getThreadManager().runTaskLater(() -> {
                    handle(holder, thrownItem);

                    if (properties().action() == Action.CANCEL) {
                        Chat.actionMessage(holder, properties().cancelMessage());
                        properties().action(Action.EXECUTE);
                    }

                    if (properties().removeItem()) {
                        if (thrownItem.isValid()) {
                            thrownItem.remove();
                        }
                    }
                }, exTicks);
                break;
            case CANCEL:
                Chat.actionMessage(holder, properties().cancelMessage());
                break;

        }

    }

    public abstract void handle(Player holder, Item thrownItem);

    @Override
    public Properties properties() {
        return properties;
    }

    public enum Action {
        DELAY,
        REPEAT_TICK,
        EXECUTE,
        CANCEL
    }

    public class Properties extends GadgetProperties {
        @Path("force")
        private double force;

        @Path("delay")
        private int delay = 40;

        @Path("delay-in-ticks")
        private boolean ticks = false;

        @Path("time-type")
        private String timeTypeString = TimeType.SECOND.name();

        @Path("pickupable")
        private boolean pickupable = false;

        @Path("remove-firstPageEnabled")
        private boolean removeItem = true;

        @Path("take-firstPageEnabled")
        @Comment("Whether or not the firstPageEnabled is taken once thrown (on interact / right click)")
        private boolean takeItem = true;

        @Path("action")
        @Comment("What action to perform after the firstPageEnabled has been thrown")
        private String action = Action.EXECUTE.name();

        @Path("cancel-message")
        private String cancelMessage = "";


        public Properties(File file) {
            super(file);
        }

        public Properties() {
            super();
        }


        public int delay() {
            return delay;
        }

        public Properties delay(int delay) {
            this.delay = delay;
            return this;
        }

        public Properties delayType(TimeType type) {
            this.timeTypeString = type.name();
            return this;
        }

        public Properties useTicks(boolean val) {
            this.ticks = val;
            return this;
        }

        public Properties removeItem(boolean val) {
            this.removeItem = val;
            return this;
        }

        public TimeType delayType() {
            return TimeType.valueOf(timeTypeString);
        }

        public double force() {
            return force;
        }

        public Properties force(double force) {
            this.force = force;
            return this;
        }

        public boolean canPickup() {
            return pickupable;
        }

        public Properties canPickup(boolean value) {
            this.pickupable = value;
            return this;
        }

        public Properties action(Action action) {
            this.action = action.name();
            return this;
        }

        public Properties cancel(String message) {
            this.cancelMessage = message;
            this.action = Action.CANCEL.name();
            return this;
        }

        public Properties cancelMessage(String message) {
            this.cancelMessage = message;
            return this;
        }

        public Action action() {
            return Action.valueOf(action);
        }

        public String cancelMessage() {
            return cancelMessage;
        }

        public boolean removeItem() {
            return removeItem;
        }

        public boolean isTicks() {
            return ticks;
        }

        public boolean takeItem() {
            return takeItem;
        }

        public Properties takeItem(boolean value) {
            this.takeItem = value;
            return this;
        }
    }

}

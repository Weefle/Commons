package com.caved_in.commons.game.item;

import com.caved_in.commons.game.clause.PlayerDamageEntityClause;
import com.caved_in.commons.game.gadget.ItemGadget;
import com.caved_in.commons.item.ItemBuilder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the {@link ItemGadget} class which implements the {@link Weapon} interface.
 *
 * Provides unimplemented methods to handle whenever the firstPageEnabled is activated (right clicked), swung, damages a(n) entity(entities), when it's dropped,
 * when it's broken, and so forth.
 *
 * Extending this class will be a sure route to creating custom swords, axes, and tools of that nature.
 */
public abstract class BaseWeapon extends ItemGadget implements Weapon {
    private Set<PlayerDamageEntityClause> damageClauses = new HashSet<>();

    private WeaponProperties properties = new WeaponProperties();

    public BaseWeapon(ItemStack item) {
        super(item);
        properties.durability(item);
    }

    public BaseWeapon(ItemBuilder builder) {
        super(builder);
        properties.durability(getItem());
    }

    @Override
    public void perform(Player holder) {
        onSwing(holder);
    }

    public void addDamageClause(PlayerDamageEntityClause... clauses) {
        Collections.addAll(damageClauses, clauses);
    }

    @Override
    public boolean canDamage(Player p, LivingEntity e) {
        for (PlayerDamageEntityClause clause : damageClauses) {
            if (!clause.canDamage(p, e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    //TODO inspect if this causes errors
    public WeaponProperties properties() {
        return properties;
    }

    @Override
    public abstract void onSwing(Player p);

    @Override
    public abstract void onActivate(Player p);

    @Override
    public abstract void onAttack(Player p, LivingEntity e);

    @Override
    public abstract void onBreak(Player p);
}

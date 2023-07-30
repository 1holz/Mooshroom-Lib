package de.einholz.ehmooshroom.gui.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public class UnitFormatter {
    private static final DecimalFormat format = new DecimalFormat("#.##");

    public static String formatFluid(long droplets) {
        long magnitude = Math.abs(droplets);
        if (magnitude >= FluidConstants.BUCKET)
            return formatLong(droplets, "BU");
        if (magnitude >= FluidConstants.BOTTLE)
            return format.format((double) droplets / FluidConstants.BOTTLE) + "BO";
        if (magnitude >= FluidConstants.INGOT)
            return format.format((double) droplets / FluidConstants.INGOT) + "IN";
        if (magnitude >= FluidConstants.NUGGET)
            return format.format((double) droplets / FluidConstants.NUGGET) + "NU";
        return droplets + "DR";
    }

    public static String formatLong(long amount, String unit) {
        long magnitude = Math.abs(amount);
        if (amount == Long.MAX_VALUE)
            return "∞" + unit;
        if (amount == Long.MIN_VALUE)
            return "-∞" + unit;
        if (magnitude >= 1_000_000_000_000_000_000L)
            return format.format((double) amount / 1_000_000_000_000_000_000L) + "E" + unit;
        if (magnitude >= 1_000_000_000_000_000L)
            return format.format((double) amount / 1_000_000_000_000_000L) + "P" + unit;
        if (magnitude >= 1_000_000_000_000L)
            return format.format((double) amount / 1_000_000_000_000L) + "T" + unit;
        if (magnitude >= 1_000_000_000L)
            return format.format((double) amount / 1_000_000_000L) + "G" + unit;
        if (magnitude >= 1_000_000L)
            return format.format((double) amount / 1_000_000L) + "M" + unit;
        if (magnitude >= 1_000L)
            return format.format((double) amount / 1_000L) + "k" + unit;
        return amount + unit;
    }

    static {
        format.setRoundingMode(RoundingMode.HALF_UP);
    }
}

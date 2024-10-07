package com.force.formula;

import jakarta.annotation.Nullable;

/**
 * Represents a location on earth, defined by a latitude and a longitude.
 *
 * @author ahersans
 * @see com.force.formula.util.FormulaGeolocationService
 * @since 180
 */
public interface FormulaGeolocation
{

    @Nullable
    Number getLatitude();

    @Nullable
    Number getLongitude();

    // Optional enum for helping with display.  Not required, but used by the GeolocationService
    enum GeolocationDisplayMode
    {
        DecimalDegrees,
        DegreesMinutesSeconds,
    }
}
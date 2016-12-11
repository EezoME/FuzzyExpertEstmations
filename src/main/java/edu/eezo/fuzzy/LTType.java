package edu.eezo.fuzzy;

/**
 * Represents linguistic term's type.
 * Created by Eezo on 26.11.2016.
 */
public enum LTType {
    TRIANGULAR {
        @Override
        public String getReadableString() {
            return "Triangular";
        }
    },
    TRAPEZOIDAL {
        @Override
        public String getReadableString() {
            return "Trapezoidal";
        }
    };

    public abstract String getReadableString();
}

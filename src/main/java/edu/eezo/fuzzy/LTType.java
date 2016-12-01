package edu.eezo.fuzzy;

/**
 * Created by Eezo on 26.11.2016.
 */
public enum LTType {
    TRIANGULAR {
        @Override
        public String getReadableString() {
            return "Треугольная";
        }
    },
    TRAPEZOIDAL {
        @Override
        public String getReadableString() {
            return "Трапециевидная";
        }
    };

    public abstract String getReadableString();

    @Override
    public String toString() {
        return getReadableString();
    }
}

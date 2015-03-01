package com.github.sevntu.checkstyle.checks.design;

public class InputNestedSwitchCheck {

    private int color;
    private int type;

    {
        switch (color) {
        case 3:
            switch (type) { // error
            case 6:
            }
        }
        switch (color) {
        default:
        }
    }

    public void simple() {
        switch (hashCode()) {
        case 1:
            switch (hashCode()) { // error
            case 1:
            default:
            }
        case 2:
            switch (type) { // error
            }
        default:
        }

        switch (type) {
        default:
            switch (color) { // error
            case 5:
                switch (type) { // error
                default:
                }
            }
        }
    }

    public void inClass(final Short s) {
        switch (type) {
        case 3:
            new Object() {
                public void anonymousMethod() {
                    {
                        switch (s) { // error
                        case 5:
                            switch (type) { // error
                            default:
                            }
                        }
                    }
                }
            };
        default:
            new Object() {
                class SwitchClass {
                    {
                        switch (color) { // not pure nested switch but still should be exposed
                        case 5:
                            switch (type) { // error
                            default:
                            }
                        }
                    }
                }
            };
        }
    }
}

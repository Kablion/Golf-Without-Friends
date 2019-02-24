package de.kablion.golf.utils;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class NumbersOnlyFilter implements TextField.TextFieldFilter {
    @Override
    public boolean acceptChar (TextField textField, char c) {
        return Character.isDigit(c)|| c=='.' || c=='-';
    }

}

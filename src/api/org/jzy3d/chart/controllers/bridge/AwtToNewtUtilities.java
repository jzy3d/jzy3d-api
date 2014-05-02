package org.jzy3d.chart.controllers.bridge;

import com.jogamp.newt.event.KeyEvent;

/**
 * Utilities for mapping key codes and events from awt to 
 * newt.
 * 
 * @author Nils Hoffmann
 */
public class AwtToNewtUtilities {
	public static int mapKeyCode(KeyEvent event) {
        
        if(event.getEventType()==KeyEvent.EVENT_KEY_TYPED) {
            return java.awt.event.KeyEvent.VK_UNDEFINED;
        }
        
        switch(event.getKeyCode()) {
            case KeyEvent.VK_0:
                return java.awt.event.KeyEvent.VK_0;
            case KeyEvent.VK_1:
                return java.awt.event.KeyEvent.VK_1;
            case KeyEvent.VK_2:
                return java.awt.event.KeyEvent.VK_2;
            case KeyEvent.VK_3:
                return java.awt.event.KeyEvent.VK_3;
            case KeyEvent.VK_4:
                return java.awt.event.KeyEvent.VK_4;
            case KeyEvent.VK_5:
                return java.awt.event.KeyEvent.VK_5;
            case KeyEvent.VK_6:
                return java.awt.event.KeyEvent.VK_6;
            case KeyEvent.VK_7:
                return java.awt.event.KeyEvent.VK_7;                
            case KeyEvent.VK_8:
                return java.awt.event.KeyEvent.VK_8;
            case KeyEvent.VK_9:
                return java.awt.event.KeyEvent.VK_9;
            case KeyEvent.VK_A:
                return java.awt.event.KeyEvent.VK_A;
            case KeyEvent.VK_ACCEPT:
                return java.awt.event.KeyEvent.VK_ACCEPT;
            case KeyEvent.VK_ADD:
                return java.awt.event.KeyEvent.VK_ADD;
            case KeyEvent.VK_ALL_CANDIDATES:
                return java.awt.event.KeyEvent.VK_ALL_CANDIDATES;
            case KeyEvent.VK_ALPHANUMERIC:
                return java.awt.event.KeyEvent.VK_ALPHANUMERIC;                
            case KeyEvent.VK_ALT:
                return java.awt.event.KeyEvent.VK_ALT;                
            case KeyEvent.VK_ALT_GRAPH:
                return java.awt.event.KeyEvent.VK_ALT_GRAPH;                
            case KeyEvent.VK_AMPERSAND:
                return java.awt.event.KeyEvent.VK_AMPERSAND;                
            case KeyEvent.VK_ASTERISK:
                return java.awt.event.KeyEvent.VK_ASTERISK;                
            case KeyEvent.VK_AT:
                return java.awt.event.KeyEvent.VK_AT;                
            case KeyEvent.VK_B:
                return java.awt.event.KeyEvent.VK_B;                
            case KeyEvent.VK_BACK_QUOTE:
                return java.awt.event.KeyEvent.VK_BACK_QUOTE;                
            case KeyEvent.VK_BACK_SLASH:
                return java.awt.event.KeyEvent.VK_BACK_SLASH;                
            case KeyEvent.VK_BACK_SPACE:
                return java.awt.event.KeyEvent.VK_BACK_SPACE;                
            case KeyEvent.VK_BEGIN:
                return java.awt.event.KeyEvent.VK_BEGIN;                
            case KeyEvent.VK_BRACELEFT:
                return java.awt.event.KeyEvent.VK_BRACELEFT;                
            case KeyEvent.VK_BRACERIGHT:
                return java.awt.event.KeyEvent.VK_BRACERIGHT;                
            case KeyEvent.VK_C:
                return java.awt.event.KeyEvent.VK_C;                
            case KeyEvent.VK_CANCEL:
                return java.awt.event.KeyEvent.VK_CANCEL;                
            case KeyEvent.VK_CAPS_LOCK:
                return java.awt.event.KeyEvent.VK_CAPS_LOCK;                
            case KeyEvent.VK_CIRCUMFLEX:
                return java.awt.event.KeyEvent.VK_CIRCUMFLEX;                
            case KeyEvent.VK_CLEAR:
                return java.awt.event.KeyEvent.VK_CLEAR;                
            case KeyEvent.VK_CLOSE_BRACKET:
                return java.awt.event.KeyEvent.VK_CLOSE_BRACKET;                
            case KeyEvent.VK_CODE_INPUT:
                return java.awt.event.KeyEvent.VK_CODE_INPUT;                
            case KeyEvent.VK_COLON:
                return java.awt.event.KeyEvent.VK_COLON;                
            case KeyEvent.VK_COMMA:
                return java.awt.event.KeyEvent.VK_COMMA;                
            case KeyEvent.VK_COMPOSE:
                return java.awt.event.KeyEvent.VK_COMPOSE;                
            case KeyEvent.VK_CONTEXT_MENU:
                return java.awt.event.KeyEvent.VK_CONTEXT_MENU;                
            case KeyEvent.VK_CONTROL:
                return java.awt.event.KeyEvent.VK_CONTROL;                
            case KeyEvent.VK_CONVERT:
                return java.awt.event.KeyEvent.VK_CONVERT;                
            case KeyEvent.VK_COPY:
                return java.awt.event.KeyEvent.VK_COPY;                
            case KeyEvent.VK_CUT:
                return java.awt.event.KeyEvent.VK_CUT;                
            case KeyEvent.VK_D:
                return java.awt.event.KeyEvent.VK_D;                
            case KeyEvent.VK_DEAD_ABOVEDOT:
                    return java.awt.event.KeyEvent.VK_DEAD_ABOVEDOT;
            case KeyEvent.VK_DEAD_ABOVERING:
                    return java.awt.event.KeyEvent.VK_DEAD_ABOVERING;
            case KeyEvent.VK_DEAD_ACUTE:
                    return java.awt.event.KeyEvent.VK_DEAD_ACUTE;
            case KeyEvent.VK_DEAD_BREVE:
                    return java.awt.event.KeyEvent.VK_DEAD_BREVE;
            case KeyEvent.VK_DEAD_CARON:
                    return java.awt.event.KeyEvent.VK_DEAD_CARON;
            case KeyEvent.VK_DEAD_CEDILLA:
                    return java.awt.event.KeyEvent.VK_DEAD_CEDILLA;
            case KeyEvent.VK_DEAD_CIRCUMFLEX:
                    return java.awt.event.KeyEvent.VK_DEAD_CIRCUMFLEX;
            case KeyEvent.VK_DEAD_DIAERESIS:
                    return java.awt.event.KeyEvent.VK_DEAD_DIAERESIS;
            case KeyEvent.VK_DEAD_DOUBLEACUTE:
                    return java.awt.event.KeyEvent.VK_DEAD_DOUBLEACUTE;
            case KeyEvent.VK_DEAD_GRAVE:
                    return java.awt.event.KeyEvent.VK_DEAD_GRAVE;
            case KeyEvent.VK_DEAD_IOTA:
                    return java.awt.event.KeyEvent.VK_DEAD_IOTA;
            case KeyEvent.VK_DEAD_MACRON:
                    return java.awt.event.KeyEvent.VK_DEAD_MACRON;
            case KeyEvent.VK_DEAD_OGONEK:
                    return java.awt.event.KeyEvent.VK_DEAD_OGONEK;
            case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:
                    return java.awt.event.KeyEvent.VK_DEAD_SEMIVOICED_SOUND;
            case KeyEvent.VK_DEAD_TILDE:
                    return java.awt.event.KeyEvent.VK_DEAD_TILDE;
            case KeyEvent.VK_DEAD_VOICED_SOUND:
                    return java.awt.event.KeyEvent.VK_DEAD_VOICED_SOUND;
            case KeyEvent.VK_DECIMAL:
                    return java.awt.event.KeyEvent.VK_DECIMAL;
            case KeyEvent.VK_DELETE:
                    return java.awt.event.KeyEvent.VK_DELETE;
            case KeyEvent.VK_DIVIDE:
                    return java.awt.event.KeyEvent.VK_DIVIDE;
            case KeyEvent.VK_DOLLAR:
                    return java.awt.event.KeyEvent.VK_DOLLAR;
            case KeyEvent.VK_DOWN:
                    return java.awt.event.KeyEvent.VK_DOWN;
            case KeyEvent.VK_E:
                    return java.awt.event.KeyEvent.VK_E;
            case KeyEvent.VK_END:
                    return java.awt.event.KeyEvent.VK_END;
            case KeyEvent.VK_ENTER:
                    return java.awt.event.KeyEvent.VK_ENTER;
            case KeyEvent.VK_EQUALS:
                    return java.awt.event.KeyEvent.VK_EQUALS;
            case KeyEvent.VK_ESCAPE:
                    return java.awt.event.KeyEvent.VK_ESCAPE;
            case KeyEvent.VK_EURO_SIGN:
                    return java.awt.event.KeyEvent.VK_EURO_SIGN;
            case KeyEvent.VK_EXCLAMATION_MARK:
                    return java.awt.event.KeyEvent.VK_EXCLAMATION_MARK;
            case KeyEvent.VK_F:
                    return java.awt.event.KeyEvent.VK_F;
            case KeyEvent.VK_F1:
                    return java.awt.event.KeyEvent.VK_F1;
            case KeyEvent.VK_F10:
                    return java.awt.event.KeyEvent.VK_F10;
            case KeyEvent.VK_F11:
                    return java.awt.event.KeyEvent.VK_F11;
            case KeyEvent.VK_F12:
                    return java.awt.event.KeyEvent.VK_F12;
            case KeyEvent.VK_F13:
                    return java.awt.event.KeyEvent.VK_F13;
            case KeyEvent.VK_F14:
                    return java.awt.event.KeyEvent.VK_F14;
            case KeyEvent.VK_F15:
                    return java.awt.event.KeyEvent.VK_F15;
            case KeyEvent.VK_F16:
                    return java.awt.event.KeyEvent.VK_F16;
            case KeyEvent.VK_F17:
                    return java.awt.event.KeyEvent.VK_F17;
            case KeyEvent.VK_F18:
                    return java.awt.event.KeyEvent.VK_F18;
            case KeyEvent.VK_F19:
                    return java.awt.event.KeyEvent.VK_F19;
            case KeyEvent.VK_F2:
                    return java.awt.event.KeyEvent.VK_F2;
            case KeyEvent.VK_F20:
                    return java.awt.event.KeyEvent.VK_F20;
            case KeyEvent.VK_F21:
                    return java.awt.event.KeyEvent.VK_F21;
            case KeyEvent.VK_F22:
                    return java.awt.event.KeyEvent.VK_F22;
            case KeyEvent.VK_F23:
                    return java.awt.event.KeyEvent.VK_F23;
            case KeyEvent.VK_F24:
                    return java.awt.event.KeyEvent.VK_F24;
            case KeyEvent.VK_F3:
                    return java.awt.event.KeyEvent.VK_F3;
            case KeyEvent.VK_F4:
                    return java.awt.event.KeyEvent.VK_F4;
            case KeyEvent.VK_F5:
                    return java.awt.event.KeyEvent.VK_F5;
            case KeyEvent.VK_F6:
                    return java.awt.event.KeyEvent.VK_F6;
            case KeyEvent.VK_F7:
                    return java.awt.event.KeyEvent.VK_F7;
            case KeyEvent.VK_F8:
                    return java.awt.event.KeyEvent.VK_F8;
            case KeyEvent.VK_F9:
                    return java.awt.event.KeyEvent.VK_F9;
            case KeyEvent.VK_FINAL:
                    return java.awt.event.KeyEvent.VK_FINAL;
            case KeyEvent.VK_FIND:
                    return java.awt.event.KeyEvent.VK_FIND;
            case KeyEvent.VK_FULL_WIDTH:
                    return java.awt.event.KeyEvent.VK_FULL_WIDTH;
            case KeyEvent.VK_G:
                    return java.awt.event.KeyEvent.VK_G;
            case KeyEvent.VK_GREATER:
                    return java.awt.event.KeyEvent.VK_GREATER;
            case KeyEvent.VK_H:
                    return java.awt.event.KeyEvent.VK_H;
            case KeyEvent.VK_HALF_WIDTH:
                    return java.awt.event.KeyEvent.VK_HALF_WIDTH;
            case KeyEvent.VK_HELP:
                    return java.awt.event.KeyEvent.VK_HELP;
            case KeyEvent.VK_HIRAGANA:
                    return java.awt.event.KeyEvent.VK_HIRAGANA;
            case KeyEvent.VK_HOME:
                    return java.awt.event.KeyEvent.VK_HOME;
            case KeyEvent.VK_I:
                    return java.awt.event.KeyEvent.VK_I;
            case KeyEvent.VK_INPUT_METHOD_ON_OFF:
                    return java.awt.event.KeyEvent.VK_INPUT_METHOD_ON_OFF;
            case KeyEvent.VK_INSERT:
                    return java.awt.event.KeyEvent.VK_INSERT;
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
                    return java.awt.event.KeyEvent.VK_INVERTED_EXCLAMATION_MARK;
            case KeyEvent.VK_J:
                    return java.awt.event.KeyEvent.VK_J;
            case KeyEvent.VK_JAPANESE_HIRAGANA:
                    return java.awt.event.KeyEvent.VK_JAPANESE_HIRAGANA;
            case KeyEvent.VK_JAPANESE_KATAKANA:
                    return java.awt.event.KeyEvent.VK_JAPANESE_KATAKANA;
            case KeyEvent.VK_JAPANESE_ROMAN:
                    return java.awt.event.KeyEvent.VK_JAPANESE_ROMAN;
            case KeyEvent.VK_K:
                    return java.awt.event.KeyEvent.VK_K;
            case KeyEvent.VK_KANA:
                    return java.awt.event.KeyEvent.VK_KANA;
            case KeyEvent.VK_KANA_LOCK:
                    return java.awt.event.KeyEvent.VK_KANA_LOCK;
            case KeyEvent.VK_KANJI:
                    return java.awt.event.KeyEvent.VK_KANJI;
            case KeyEvent.VK_KATAKANA:
                    return java.awt.event.KeyEvent.VK_KATAKANA;
            case KeyEvent.VK_KP_DOWN:
                    return java.awt.event.KeyEvent.VK_KP_DOWN;
            case KeyEvent.VK_KP_LEFT:
                    return java.awt.event.KeyEvent.VK_KP_LEFT;
            case KeyEvent.VK_KP_RIGHT:
                    return java.awt.event.KeyEvent.VK_KP_RIGHT;
            case KeyEvent.VK_KP_UP:
                    return java.awt.event.KeyEvent.VK_KP_UP;
            case KeyEvent.VK_L:
                    return java.awt.event.KeyEvent.VK_L;
            case KeyEvent.VK_LEFT:
                    return java.awt.event.KeyEvent.VK_LEFT;
            case KeyEvent.VK_LEFT_PARENTHESIS:
                    return java.awt.event.KeyEvent.VK_LEFT_PARENTHESIS;
            case KeyEvent.VK_LESS:
                    return java.awt.event.KeyEvent.VK_LESS;
            case KeyEvent.VK_M:
                    return java.awt.event.KeyEvent.VK_M;
            case KeyEvent.VK_META:
                    return java.awt.event.KeyEvent.VK_META;
            case KeyEvent.VK_MINUS:
                    return java.awt.event.KeyEvent.VK_MINUS;
            case KeyEvent.VK_MODECHANGE:
                    return java.awt.event.KeyEvent.VK_MODECHANGE;
            case KeyEvent.VK_MULTIPLY:
                    return java.awt.event.KeyEvent.VK_MULTIPLY;
            case KeyEvent.VK_N:
                    return java.awt.event.KeyEvent.VK_N;
            case KeyEvent.VK_NONCONVERT:
                    return java.awt.event.KeyEvent.VK_NONCONVERT;
            case KeyEvent.VK_NUM_LOCK:
                    return java.awt.event.KeyEvent.VK_NUM_LOCK;
            case KeyEvent.VK_NUMBER_SIGN:
                    return java.awt.event.KeyEvent.VK_NUMBER_SIGN;
            case KeyEvent.VK_NUMPAD0:
                    return java.awt.event.KeyEvent.VK_NUMPAD0;
            case KeyEvent.VK_NUMPAD1:
                    return java.awt.event.KeyEvent.VK_NUMPAD1;
            case KeyEvent.VK_NUMPAD2:
                    return java.awt.event.KeyEvent.VK_NUMPAD2;
            case KeyEvent.VK_NUMPAD3:
                    return java.awt.event.KeyEvent.VK_NUMPAD3;
            case KeyEvent.VK_NUMPAD4:
                    return java.awt.event.KeyEvent.VK_NUMPAD4;
            case KeyEvent.VK_NUMPAD5:
                    return java.awt.event.KeyEvent.VK_NUMPAD5;
            case KeyEvent.VK_NUMPAD6:
                    return java.awt.event.KeyEvent.VK_NUMPAD6;
            case KeyEvent.VK_NUMPAD7:
                    return java.awt.event.KeyEvent.VK_NUMPAD7;
            case KeyEvent.VK_NUMPAD8:
                    return java.awt.event.KeyEvent.VK_NUMPAD8;
            case KeyEvent.VK_NUMPAD9:
                    return java.awt.event.KeyEvent.VK_NUMPAD9;
            case KeyEvent.VK_O:
                    return java.awt.event.KeyEvent.VK_O;
            case KeyEvent.VK_OPEN_BRACKET:
                    return java.awt.event.KeyEvent.VK_OPEN_BRACKET;
            case KeyEvent.VK_P:
                    return java.awt.event.KeyEvent.VK_P;
            case KeyEvent.VK_PAGE_DOWN:
                    return java.awt.event.KeyEvent.VK_PAGE_DOWN;
            case KeyEvent.VK_PAGE_UP:
                    return java.awt.event.KeyEvent.VK_PAGE_UP;
            case KeyEvent.VK_PASTE:
                    return java.awt.event.KeyEvent.VK_PASTE;
            case KeyEvent.VK_PAUSE:
                    return java.awt.event.KeyEvent.VK_PAUSE;
            case KeyEvent.VK_PERIOD:
                    return java.awt.event.KeyEvent.VK_PERIOD;
            case KeyEvent.VK_PLUS:
                    return java.awt.event.KeyEvent.VK_PLUS;
            case KeyEvent.VK_PREVIOUS_CANDIDATE:
                    return java.awt.event.KeyEvent.VK_PREVIOUS_CANDIDATE;
            case KeyEvent.VK_PRINTSCREEN:
                    return java.awt.event.KeyEvent.VK_PRINTSCREEN;
            case KeyEvent.VK_PROPS:
                    return java.awt.event.KeyEvent.VK_PROPS;
            case KeyEvent.VK_Q:
                    return java.awt.event.KeyEvent.VK_Q;
            case KeyEvent.VK_QUOTE:
                    return java.awt.event.KeyEvent.VK_QUOTE;
            case KeyEvent.VK_QUOTEDBL:
                    return java.awt.event.KeyEvent.VK_QUOTEDBL;
            case KeyEvent.VK_R:
                    return java.awt.event.KeyEvent.VK_R;
            case KeyEvent.VK_RIGHT:
                    return java.awt.event.KeyEvent.VK_RIGHT;
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                    return java.awt.event.KeyEvent.VK_RIGHT_PARENTHESIS;
            case KeyEvent.VK_ROMAN_CHARACTERS:
                    return java.awt.event.KeyEvent.VK_ROMAN_CHARACTERS;
            case KeyEvent.VK_S:
                    return java.awt.event.KeyEvent.VK_S;
            case KeyEvent.VK_SCROLL_LOCK:
                    return java.awt.event.KeyEvent.VK_SCROLL_LOCK;
            case KeyEvent.VK_SEMICOLON:
                    return java.awt.event.KeyEvent.VK_SEMICOLON;
            case KeyEvent.VK_SEPARATOR:
                    return java.awt.event.KeyEvent.VK_SEPARATOR;
            case KeyEvent.VK_SHIFT:
                    return java.awt.event.KeyEvent.VK_SHIFT;
            case KeyEvent.VK_SLASH:
                    return java.awt.event.KeyEvent.VK_SLASH;
            case KeyEvent.VK_SPACE:
                    return java.awt.event.KeyEvent.VK_SPACE;
            case KeyEvent.VK_STOP:
                    return java.awt.event.KeyEvent.VK_STOP;
            case KeyEvent.VK_SUBTRACT:
                    return java.awt.event.KeyEvent.VK_SUBTRACT;
            case KeyEvent.VK_T:
                    return java.awt.event.KeyEvent.VK_T;
            case KeyEvent.VK_TAB:
                    return java.awt.event.KeyEvent.VK_TAB;
            case KeyEvent.VK_U:
                    return java.awt.event.KeyEvent.VK_U;
            case KeyEvent.VK_UNDEFINED:
                    return java.awt.event.KeyEvent.VK_UNDEFINED;
            case KeyEvent.VK_UNDERSCORE:
                    return java.awt.event.KeyEvent.VK_UNDERSCORE;
            case KeyEvent.VK_UNDO:
                    return java.awt.event.KeyEvent.VK_UNDO;
            case KeyEvent.VK_UP:
                    return java.awt.event.KeyEvent.VK_UP;
            case KeyEvent.VK_V:
                    return java.awt.event.KeyEvent.VK_V;
            case KeyEvent.VK_W:
                    return java.awt.event.KeyEvent.VK_W;
            case KeyEvent.VK_WINDOWS:
                    return java.awt.event.KeyEvent.VK_WINDOWS;
            case KeyEvent.VK_X:
                    return java.awt.event.KeyEvent.VK_X;
            case KeyEvent.VK_Y:
                    return java.awt.event.KeyEvent.VK_Y;
            case KeyEvent.VK_Z:
                    return java.awt.event.KeyEvent.VK_Z; 
            default:
                    throw new RuntimeException("Unmapped key: "+event.toString());
        }
    }
    
    public static int mask(com.jogamp.newt.event.MouseEvent event) {
        int newtMods = event.getModifiers();
        int awtMods = 0;
        if ((newtMods & com.jogamp.newt.event.InputEvent.SHIFT_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.SHIFT_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.CTRL_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.CTRL_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.META_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.META_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.ALT_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_GRAPH_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.ALT_GRAPH_MASK;
        }
        if (event.getButton() == com.jogamp.newt.event.MouseEvent.BUTTON1) {
            awtMods |= java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
        }
        if (event.getButton() == com.jogamp.newt.event.MouseEvent.BUTTON2) {
            awtMods |= java.awt.event.InputEvent.BUTTON2_DOWN_MASK;
        }
        if (event.getButton() == com.jogamp.newt.event.MouseEvent.BUTTON3) {
            awtMods |= java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
        }
        return awtMods;
    }
	
    public static int mask(com.jogamp.newt.event.KeyEvent event) {
        int newtMods = event.getModifiers();
        int awtMods = 0;
        if ((newtMods & com.jogamp.newt.event.InputEvent.SHIFT_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.SHIFT_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.CTRL_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.CTRL_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.META_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.META_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.ALT_MASK;
        }
        if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_GRAPH_MASK) != 0) {
            awtMods |= java.awt.event.InputEvent.ALT_GRAPH_MASK;
        }
        return awtMods;
    }
}

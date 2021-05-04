/*
 * @(#)glut_menu.java 0.2 03/05/20
 *
 * jGL 3-D graphics library for Java Copyright (c) 2001-2003 Robin Bing-Yu Chen
 * (robin@nis-lab.is.s.u-tokyo.ac.jp)
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or any later version. the GNU Lesser General Public License should be
 * included with this distribution in the file LICENSE.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package jgl.glut;

import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * glut_menu is menu object of the GLUT class of jGL 2.4.
 *
 * @version 0.2, 20 May 2003
 * @author Robin Bing-Yu Chen
 */

public class glut_menu implements ActionListener {

  private Method menuMethod = null;
  private PopupMenu JavaMenu = null;
  private Hashtable<String, Integer> JavaMenuItem = null;
  private int menuID = 0;

  private Component JavaComponent = null;

  public void actionPerformed(ActionEvent e) {
    Object[] arguments = new Object[] {JavaMenuItem.get(e.getActionCommand())};
    try {
      menuMethod.invoke(JavaComponent, arguments);
    } catch (IllegalAccessException ee) {
      System.out.println(ee);
    } catch (InvocationTargetException ee) {
      System.out.println(ee);
    }
  }

  public void glutAddMenuEntry(String label, int value) {
    MenuItem mi = new MenuItem(label);
    mi.addActionListener(this);
    JavaMenuItem.put(label, new Integer(value));
    JavaMenu.add(mi);
  }

  public void glutAddSubMenu(String label, glut_menu submenu) {
    submenu.JavaMenu.setLabel(label);
    JavaMenu.add(submenu.JavaMenu);
  }

  public void glutChangeToMenuEntry(int item, String label, int value) {
    MenuItem mi = new MenuItem(label);
    mi.addActionListener(this);
    JavaMenuItem.put(label, new Integer(value));
    JavaMenu.remove(item);
    JavaMenu.insert(mi, item);
  }

  public void glutChangeToSubMenu(int item, String label, glut_menu submenu) {
    submenu.JavaMenu.setLabel(label);
    JavaMenu.remove(item);
    JavaMenu.insert(submenu.JavaMenu, item);
  }

  public void glutRemoveMenuItem(int item) {
    JavaMenu.remove(item);
  }

  public void glutDestroyMenu() {
    menuMethod = null;
    JavaMenu = null;
    JavaMenuItem = null;
    menuID = 0;
    JavaComponent = null;
  }

  public int glutGetMenuID() {
    return menuID;
  }

  public void glutSetMenuID(int id) {
    menuID = id;
  }

  public PopupMenu glutGetMenu() {
    return JavaMenu;
  }

  public PopupMenu glutDupPopupMenu(PopupMenu pm) {
    PopupMenu npm = new PopupMenu(pm.getLabel());
    MenuItem mi;
    for (int i = 0; i < pm.getItemCount(); i++) {
      mi = pm.getItem(i);
      if (mi instanceof PopupMenu) {
        mi = glutDupPopupMenu((PopupMenu) mi);
      } else {
        mi = new MenuItem(mi.getLabel());
        mi.addActionListener(this);
      }
      npm.add(mi);
    }
    return npm;
  }

  public glut_menu glutDupMenu() {
    glut_menu newMenu = new glut_menu();
    newMenu.menuMethod = menuMethod;
    newMenu.JavaMenu = glutDupPopupMenu(JavaMenu);
    newMenu.JavaMenuItem = (Hashtable<String, Integer>) JavaMenuItem.clone();
    return newMenu;
  }

  public glut_menu(String func, int id, Component comp) {
    if (func != null) {
      Class[] parameterTypes = new Class[] {int.class};
      try {
        menuMethod = comp.getClass().getMethod(func, parameterTypes);
        JavaMenu = new PopupMenu();
        JavaMenuItem = new Hashtable<String, Integer>();
        menuID = id;
        JavaComponent = comp;
      } catch (NoSuchMethodException e) {
        System.out.println("No method named " + func);
      }
      /*
       * } else { menuMethod = null; JavaMenu = null; JavaMenuItem = null; menuID = 0:
       */
    }
  }

  public glut_menu() {}

}

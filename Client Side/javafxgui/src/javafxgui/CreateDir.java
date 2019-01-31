/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxgui;

import java.io.File;

/**
 *
 * @author Admin
 */
public class CreateDir {
    CreateDir( String dir) {
        File direction = new File(dir);
        if(!direction.exists()) {
            System.out.println("Created!");
            direction.mkdirs();
        }
    }
}

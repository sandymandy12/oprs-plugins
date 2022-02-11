package net.runelite.client.plugins.deecat.magic;

import net.runelite.api.Point;
import net.runelite.client.plugins.deecat.magic.Utility;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.Instant;


public class VirtualKeyboard extends Robot
{

    public VirtualKeyboard() throws AWTException
    {
        super();
    }

    public void pressKeys(String keysCombination) throws IllegalArgumentException
    {
        for (String key : keysCombination.split("\\+"))
        {
            try
            {
                this.keyPress((int) KeyEvent.class.getField("VK_" + key.toUpperCase()).getInt(null));

            } catch (IllegalAccessException e)
            {
                e.printStackTrace();

            }catch(NoSuchFieldException e )
            {
                throw new IllegalArgumentException(key.toUpperCase()+" is invalid key\n"+"VK_"+key.toUpperCase() + " is not defined in java.awt.event.KeyEvent");
            }


        }


    }

    public void releaseKeys(String keysCombination) throws IllegalArgumentException
    {

        for (String key : keysCombination.split("\\+"))
        {
            try
            { // KeyRelease method inherited from java.awt.Robot
                this.keyRelease((int) KeyEvent.class.getField("VK_" + key.toUpperCase()).getInt(null));

            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }catch(NoSuchFieldException e )
            {
                throw new IllegalArgumentException(key.toUpperCase()+" is invalid key\n"+"VK_"+key.toUpperCase() + " is not defined in java.awt.event.KeyEvent");
            }
        }
    }

    public static void click(int x, int y) {

        Robot bot = null;
        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        bot.mouseMove(x + 4, y + 27);
        long now = Instant.now().toEpochMilli();
        Utility.sleep(300);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
    public static void click(Point point) {

        Robot bot = null;
        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        };
        bot.mouseMove(point.getX() + 4, point.getY() + 27);
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void doubleClick(Point point) {

        Robot bot = null;
        try {
            bot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        bot.mouseMove(point.getX() + 4, point.getY() + 27);
        Utility.sleep(Utility.rand(27,47));
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        //Utility.sleep(Utility.rand(27,47));
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        Utility.sleep(  80);
        bot.mouseMove(point.getX() + 4, point.getY() + 27);
        Utility.sleep(Utility.rand(27,47));
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        //Utility.sleep(Utility.rand(27,47));
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

        Utility.sleep(  8);
        bot.mouseMove(point.getX() + 4, point.getY() + 27);
        Utility.sleep(Utility.rand(27,47));
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        //Utility.sleep(Utility.rand(27,47));
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);

    }

    public static void sendKeys(String keyCombination) throws AWTException {

        VirtualKeyboard kb = new VirtualKeyboard();

        kb.pressKeys(keyCombination);
        kb.releaseKeys(keyCombination);
        }

    public static void main(String[] args) throws AWTException
    {
        VirtualKeyboard kb = new VirtualKeyboard();


        String keyCombination = "control+a"; // select all text on screen
        //String keyCombination = "shift+a+1+c"; // types A!C on screen

        // For your case
        //String keyCombination = "alt+1+2+3";


        kb.pressKeys(keyCombination);
        kb.releaseKeys(keyCombination);



    }


}

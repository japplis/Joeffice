/*
 * Copyright 2013 Japplis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joeffice.presentation;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import javax.swing.Timer;

/**
 * Listeners to events when the presentation is in full screen mode.
 *
 * @author Anthony Goubard - Japplis
 */
public class FullScreenListener implements KeyListener, MouseMotionListener, MouseListener {

    public final static int HIDE_MOUSE_DELAY_MS = 1500;

    private FullScreenFrame frame;

    private Cursor transparentCursor;

    private Timer hideMouseTime;

    public FullScreenListener(FullScreenFrame frame) {
        this.frame = frame;
        int[] pixels = new int[16 * 16];
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
        transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(image, new Point(0, 0), "invisibleCursor");
        hideMouseTime = new Timer(HIDE_MOUSE_DELAY_MS, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setMouseVisible(false);
            }
        });
        hideMouseTime.setRepeats(false);
    }

    public void setMouseVisible(boolean visible) {
        if (visible) {
            Cursor defaultCursor = Cursor.getDefaultCursor();
            frame.setCursor(defaultCursor);
        } else {
            frame.setCursor(transparentCursor);
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                setMouseVisible(true);
                frame.dispose();
                break;
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_PAGE_DOWN:
            case KeyEvent.VK_RIGHT:
                frame.setSlideIndex(frame.getSlideIndex() + 1);
                break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_PAGE_UP:
            case KeyEvent.VK_LEFT:
                frame.setSlideIndex(frame.getSlideIndex() - 1);
                break;
            case KeyEvent.VK_HOME:
                frame.setSlideIndex(0);
                break;
            case KeyEvent.VK_END:
                frame.setSlideIndex(frame.getPresentation().getSlides().size() - 1);
                break;
            case KeyEvent.VK_COLON:
                frame.switchBlack();
                break;
        }
        // Prevent the event to go in the underling platform app which would lost the focus
        ke.consume();
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        setMouseVisible(true);
        hideMouseTime.restart();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 || me.getButton() == 5) {
            frame.setSlideIndex(frame.getSlideIndex() + 1);
        } else if (me.getButton() == MouseEvent.BUTTON3 || me.getButton() == 4) {
            frame.setSlideIndex(frame.getSlideIndex() - 1);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
}

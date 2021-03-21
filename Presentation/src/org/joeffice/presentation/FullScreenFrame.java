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

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.joeffice.desktop.ui.OfficeTopComponent;

/**
 * The frame showing the presentation in full screen.
 *
 * @author Anthony Goubard - Japplis
 */
public class FullScreenFrame extends JFrame {

    private FullScreenListener eventListener;

    private int screenIndex = -1;

    private int slideIndex = 0;

    private XMLSlideShow presentation;

    private boolean blackScreen;

    public FullScreenFrame() {
        setUndecorated(true);
        eventListener = new FullScreenListener(this);
        Component glassPane = getRootPane().getGlassPane();
        glassPane.addKeyListener(eventListener);
        glassPane.addMouseListener(eventListener);
        glassPane.addMouseMotionListener(eventListener);
        glassPane.setFocusable(true);
        glassPane.setVisible(true);
        ImageIcon icon = new ImageIcon(getClass().getResource("/org/joeffice/presentation/presentation-16.png"));
        setIconImage(icon.getImage());
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().setBackground(Color.BLACK);
    }

    public void showSlides(XMLSlideShow presentation) {
        this.presentation = presentation;
        GraphicsDevice screen = getScreen();
        setSize(screen.getDisplayMode().getWidth(), screen.getDisplayMode().getHeight());
        setVisible(true);
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            setSlideIndex(currentTopComponent.getSelectedSlideIndex());
        } else {
            setSlideIndex(0);
        }
    }

    public void setScreenIndex(int screenIndex) {
        this.screenIndex = screenIndex;
        if (isVisible()) {
            GraphicsDevice screen = getScreen();
            setSize(screen.getDisplayMode().getWidth(), screen.getDisplayMode().getHeight());
            screen.setFullScreenWindow(this);
        }
    }

    public int getScreenIndex() {
        return screenIndex;
    }

    public void nextScreen() {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screens.length <= 1) return;
        GraphicsDevice screen = getScreen();
        screen.setFullScreenWindow(null);
        int nextScreen = (screenIndex + 1) % screens.length;
        setScreenIndex(nextScreen);
    }

    public void setSlideIndex(int slideIndex) {
        if (slideIndex < 0 || slideIndex >= presentation.getSlides().size()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        this.slideIndex = slideIndex;
        if (isVisible()) {
            DisplayMode display = getScreen().getDisplayMode();
            XSLFSlide slide = presentation.getSlides().get(slideIndex);
            Dimension displaySize = new Dimension(display.getWidth(), display.getHeight());
            SlideComponent slidePanel = new SlideComponent(slide, null, displaySize);
            if (getContentPane().getComponentCount() > 0) {
                getContentPane().remove(0);
            }
            add(slidePanel);
            setSize(displaySize);
            revalidate();
        }
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            currentTopComponent.setSelectedSlideIndex(slideIndex);
        }
    }

    public int getSlideIndex() {
        return slideIndex;
    }

    public XMLSlideShow getPresentation() {
        return presentation;
    }

    public GraphicsDevice getScreen() {
        GraphicsDevice[] screens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screenIndex == -1 || screenIndex >= screens.length) {
            screenIndex = screens.length - 1;
        }
        return screens[screenIndex];
    }

    public void switchBlack() {
        blackScreen = !blackScreen;
        if (blackScreen) {
            if (getContentPane().getComponentCount() > 0) {
                getContentPane().remove(0);
                repaint();
            }
        } else {
            setSlideIndex(getSlideIndex());
        }
    }
}

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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JPanel;

import org.apache.poi.xslf.usermodel.XSLFBackground;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSheet;

/**
 * A component to show one slide.
 *
 * @author Anthony Goubard - Japplis
 */
public class SlideComponent extends JPanel {

    private XSLFSheet slide;

    private SlidesTopComponent slidesComponent;

    private double scale = 1.0;

    private BufferedImage backgroundImage;

    public SlideComponent(XSLFSheet slide, SlidesTopComponent slidesComponent) {
        this(slide, slidesComponent, new Dimension(1280, 720));
    }

    public SlideComponent(XSLFSheet slide, SlidesTopComponent slidesComponent, Dimension maxSize) {
        this.slide = slide;
        this.slidesComponent = slidesComponent;

        if (slide.getBackground() != null) {
            Rectangle2D backgroundSize = slide.getBackground().getAnchor();
            double scaleX = maxSize.getWidth() / backgroundSize.getWidth();
            double scaleY = maxSize.getHeight() / backgroundSize.getHeight();
            scale = Math.min(scaleX, scaleY);
            Dimension preferredSize = new Dimension((int) (backgroundSize.getWidth() * scale), (int) (backgroundSize.getHeight() * scale));
            setPreferredSize(preferredSize);
        }
        initComponent();
    }

    private void initComponent() {
        setLayout(null);
        //setOpaque(false);
        XSLFBackground background = slide.getBackground();
        if (background != null) {
            backgroundImage = ShapeComponent.shapeToImage(background, scale);
            Color backgroundColor = background.getFillColor();
            setBackground(backgroundColor);
        } else {
            setBackground(Color.WHITE);
        }
        List<XSLFShape> shapes = slide.getShapes();
        for (XSLFShape shape : shapes) {
            ShapeComponent shapeComponent = new ShapeComponent(shape, this);
            add(shapeComponent);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        }
    }

    public double getScale() {
        return scale;
    }

    public XSLFSheet getSlide() {
        return slide;
    }

    public SlidesTopComponent getSlidesComponent() {
        return slidesComponent;
    }
}

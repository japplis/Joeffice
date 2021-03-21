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
package org.joeffice.tools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.*;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 * Small examples that shows that shapes are not drawn.
 * You need to test it with a presentation that has for example an image.
 *
 * @author Anthony Goubard
 */
public class PptxShapeNotDrawn {

    public static void showDemo(JComponent demo, String title) {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle(title);

        mainFrame.add(demo);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Please profile a file path to open");
            System.exit(-1);
        }

        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
        XMLSlideShow presentation = new XMLSlideShow(new FileInputStream(args[0]));
        XSLFSlide[] slides = presentation.getSlides();
        for (XSLFSlide slide : slides) {
            XSLFShape[] shapes = slide.getShapes();
            for (XSLFShape shape : shapes) {

                BufferedImage img = new BufferedImage((int) shape.getAnchor().getWidth(), (int) shape.getAnchor().getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D graphics = img.createGraphics();
                graphics.translate(-shape.getAnchor().getX(), -shape.getAnchor().getY());
                shape.draw(graphics);
                graphics.dispose();
                JLabel shapeLabel = new JLabel(new ImageIcon(img));
                shapeLabel.setBorder(BorderFactory.createLineBorder(Color.RED));

                mainPanel.add(shapeLabel);
            }
        }
        showDemo(new JScrollPane(mainPanel), "Shape not displayed");
    }
}